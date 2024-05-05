package priv.dawn.workers.service.impl;

import com.google.common.hash.Hashing;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;
import lombok.AllArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import priv.dawn.kafkamessage.message.CustomMessage;
import priv.dawn.mapreduceapi.api.WorkerService;
import priv.dawn.workers.mapper.ChunkReadMapper;
import priv.dawn.workers.pojo.ChunkDTO;
import priv.dawn.workers.utils.CountWordUtil;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;
import java.util.stream.Collectors;

//@DubboService
@Service
public class WorkerServiceImpl implements WorkerService {

    private final Logger logger = LoggerFactory.getLogger(WorkerServiceImpl.class);

    @Qualifier(value = "WorkerReadThreadPool")
    @Resource
    private ThreadPoolTaskExecutor readThreadPool;

    @Autowired
    private ChunkReadMapper chunkReadMapper;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    private static final String TOPIC = "word_count";

    // DONE: 2024/5/4 这边线程不够用了之后会抛出拒绝异常, 在这边捕获返回值就是已经进入线程池的chunk的id, 表示完成个数
    @Override
    public int loadFile(int fileUID, int chunkBegin, int chunkNum) {
        logger.info("load " + chunkNum + " chunks of file: " + fileUID + " begin form chunk " + chunkBegin);
        int chunkEnd = chunkBegin + chunkNum - 1;
        List<ChunkDTO> chunks = chunkReadMapper.getChunks(fileUID, chunkBegin, chunkEnd);

        if (chunks.size() != chunkNum) return -1; // 暂时表示有chunk缺失或者没拿到chunk的问题

        int finished = 0;
        for (ChunkDTO chunk : chunks) {
            try {
                readThreadPool.execute(
                        new processChunk(fileUID, chunk)
                );
                finished = finished + 1;
            } catch (RejectedExecutionException e) {
                logger.info("When file:" + fileUID + " chunk " + chunk.getChunkId() + " was executed, the ThreadPool rejected");
                break;
            }
        }
        return finished;
    }

    @Override
    public float getProgress(int fileUID) {
        return 0;
    }

    @Override
    public List<String> getWords(int fileUID) {
        return null;
    }


    // TODO: 2024/5/4  Runner 处理 chunk 的, 整合了 Kafka , 需要解决序列化问题和分区的mapper问题
    // 使用内部类就可以直接使用 kafkaTemplate 更加方便
    @AllArgsConstructor
    private class processChunk implements Runnable {

        private int fileUID;
        private ChunkDTO chunk;

        @Override
        public void run() {
            logger.info(Thread.currentThread().getName() + " process file " + fileUID + " chunk " + chunk.getChunkId());

            // kafka 分区代替 map 发送消息
            // TODO: 2024/5/5 到时候把分区策略新写一个kafka template这个临时就这么用着
            int partitionNum = kafkaTemplate.partitionsFor(TOPIC).size();

            // 构造消息并发送
            ArrayList<CustomMessage> messages = CountWordUtil.generateMsgPartitionMapperFromChunk(fileUID, chunk, partitionNum);
            for (int partition = 0; partition < partitionNum; partition++) {
                CustomMessage msg = messages.get(partition);
                kafkaTemplate.send(TOPIC, partition, String.valueOf(fileUID), msg.toJsonStr()).addCallback(
                        success -> {

                        },
                        failure -> {
                            // TODO: 2024/5/5 还没想好失败怎么办
                            logger.warn(Thread.currentThread().getName() + " call back failure: " + failure);
                        }
                );
            }
        }
    }


}
