package priv.dawn.workers.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import priv.dawn.kafkamessage.message.CustomMessage;
import priv.dawn.mapreduceapi.api.WorkerService;
import priv.dawn.workers.mapper.ChunkReadMapper;
import priv.dawn.workers.pojo.ChunkDTO;
import priv.dawn.workers.utils.CountWordUtil;
import priv.dawn.workers.utils.ProgressManager;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;

//@DubboService
@Slf4j
@Service
public class WorkerServiceImpl implements WorkerService {


    @Qualifier(value = "WorkerReadThreadPool")
    @Resource
    private ThreadPoolTaskExecutor readThreadPool;

    @Autowired
    private ChunkReadMapper chunkReadMapper;

    @Autowired
    private ProgressManager progressManager;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    private static final String TOPIC = "word_count";

    // DONE: 2024/5/4 这边线程不够用了之后会抛出拒绝异常, 在这边捕获返回值就是已经进入线程池的chunk的id, 表示完成个数
    @Override
    public int loadFile(int fileUID, int chunkBegin, int chunkNum) {
        log.info("load " + chunkNum + " chunks of file: " + fileUID + " begin form chunk " + chunkBegin);
        int chunkEnd = chunkBegin + chunkNum - 1;
        List<ChunkDTO> chunks = chunkReadMapper.getChunks(fileUID, chunkBegin, chunkEnd);
        // 判断chunk缺失和数字对不上的问题
        if (chunks.size() != chunkNum) return -1;
        // TODO: 2024/5/6 上面都是为了模拟分布式分块存储洗头膏的交互, 因为这个框架里面没有这样一个实际的系统, 所以写的很潦草

        int finished = 0;
        for (ChunkDTO chunk : chunks) {
            try {
                readThreadPool.execute(new processChunk(fileUID, chunk));
                finished = finished + 1;
            } catch (RejectedExecutionException e) {
                log.error("When file:" + fileUID + " chunk " + chunk.getChunkId() + " was executed, the ThreadPool rejected");
                break;
            }
        }
        return finished; // 真要出问题了反正查日志去
    }

    @Override
    public float getProgress(int fileUID) {
        return progressManager.getProgress(fileUID);
    }

    @Override
    public List<String> getWords(int fileUID) {
        return null;
    }

    @Override
    public int createOrder(int fileUID, int chunkNum) {
        if(chunkNum<=0) return -2; // chunkNum 违规
        return progressManager.createProgress(fileUID,chunkNum);
    }


    // TODO: 2024/5/4  Runner 处理 chunk 的, 整合了 Kafka , 需要解决序列化问题和分区的mapper问题
    // 使用内部类就可以直接使用 kafkaTemplate 更加方便
    @AllArgsConstructor
    private class processChunk implements Runnable {

        private int fileUID;
        private ChunkDTO chunk;

        @Override
        public void run() {
//            log.info(Thread.currentThread().getName() + " process file " + fileUID + " chunk " + chunk.getChunkId());

            // kafka 分区代替 map 发送消息
            // TODO: 2024/5/5 到时候把分区策略新写一个kafka template这个临时就这么用着
            int partitionNum = kafkaTemplate.partitionsFor(TOPIC).size();

            // 构造消息并发送
            ArrayList<CustomMessage> messages = CountWordUtil.generateMsgPartitionMapperFromChunk(fileUID, chunk, partitionNum);
            for (int partition = 0; partition < partitionNum; partition++) {
                CustomMessage msg = messages.get(partition);
                String key = String.valueOf(fileUID);
                kafkaTemplate.send(TOPIC, partition, key, msg.toJsonStr()).addCallback(
                        success -> {
                            if (success != null)
                                progressManager.updateProgress(success.getProducerRecord(), partitionNum);
                        },
                        failure -> {
                            // TODO: 2024/5/5 还没想好失败怎么办
                            log.warn(Thread.currentThread().getName() + " call back failure: " + failure);
                        }
                );
            }
        }
    }


}
