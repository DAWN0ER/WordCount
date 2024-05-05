package priv.dawn.workers.service.impl;

import com.google.common.hash.Hashing;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import priv.dawn.kafkamessage.message.CustomMessage;
import priv.dawn.mapreduceapi.api.WorkerService;
import priv.dawn.workers.mapper.ChunkReadMapper;
import priv.dawn.workers.pojo.ChunkDTO;

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
    @AllArgsConstructor
    private class processChunk implements Runnable {

        private int fileUID;
        private ChunkDTO chunk;

        @Override
        public void run() {
            logger.info(Thread.currentThread().getName() + " process file " + fileUID + " chunk " + chunk.getChunkId());
            // 分词
            List<Term> terms = StandardTokenizer.segment(chunk.getContext()).stream()
                    .filter(t ->
                                    !t.nature.startsWith("u") &&
                                            !t.nature.startsWith("w") &&
                                            !t.nature.startsWith("r") &&
                                            !t.nature.startsWith("m")
                            // 过滤符号, 助词, 代词, 数量词
                    ).collect(Collectors.toList());

            // kafka 分区策略
            // TODO: 2024/5/5 到时候把分区策略新写一个kafka template这个临时就这么用着
            int partitionNum = kafkaTemplate.partitionsFor(TOPIC).size();
//            logger.info("partitionNum=" + partitionNum);
            ArrayList<HashMap<String, Integer>> partitionMaps = new ArrayList<>(partitionNum);
            for (int idx = 0; idx < partitionNum; idx++) partitionMaps.add(new HashMap<>(terms.size() / 4 * 2));
            // 分区计数
            terms.forEach((Term term) -> {
                        String word = term.word;
                        int partition = (int) (Hashing.murmur3_32_fixed().hashString(word, StandardCharsets.UTF_8).padToLong() % partitionNum);
                        HashMap<String, Integer> wordCnt = partitionMaps.get(partition);
                        if (wordCnt.containsKey(word)) {
                            int tmp = wordCnt.get(word);
                            wordCnt.put(word, tmp + 1);
                        } else wordCnt.put(word, 1);
                    }
            );
            // 构造消息并发送
            // TODO: 2024/5/5 因为暂时很简单, 如果后续复杂的话就换用其他的
            // TODO: 2024/5/5 序列化器, 分区策略, 批处理工厂, 看来 kafka 的 config 迫在眉睫啊
            List<CustomMessage> messages = new ArrayList<>(partitionNum);
            partitionMaps.forEach((map) -> {
                messages.add(new CustomMessage(fileUID, map));
            });
            for (int partition = 0; partition < partitionNum; partition++)
                kafkaTemplate.send(TOPIC, partition, String.valueOf(fileUID), messages.get(partition).toString());
        }
    }


}
