package priv.dawn.workers.service.impl;

import com.hankcs.hanlp.classification.tokenizers.HanLPTokenizer;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;
import lombok.AllArgsConstructor;
import org.apache.kafka.common.protocol.types.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import priv.dawn.mapreduceapi.api.WorkerService;
import priv.dawn.workers.mapper.ChunkReadMapper;
import priv.dawn.workers.pojo.ChunkDTO;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
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
    private KafkaTemplate<String,String> kafkaTemplate;
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
                        new processChunk(fileUID, chunkNum, chunkBegin, chunk)
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
        private int chunkNum;
        private int chunkBegin;
        private ChunkDTO chunk;

        @Override
        public void run() {
            logger.info(Thread.currentThread().getName() + " process file " + fileUID + " chunk " + chunk.getChunkId());
            List<Term> terms = StandardTokenizer.segment(chunk.getContext()).stream()
                    .filter(t ->
                            !t.nature.startsWith("u") &&
                            !t.nature.startsWith("w") &&
                            !t.nature.startsWith("r") &&
                            !t.nature.startsWith("m")
                    // 过滤符号, 助词, 代词, 数量词
            ).collect(Collectors.toList());

            HashMap<String, Integer> wordCnt = new HashMap<>(terms.size() / 4);
            terms.forEach(new Consumer<Term>() {
                @Override
                public void accept(Term term) {
                    if (wordCnt.containsKey(term.word)) {
                        int tmp = wordCnt.get(term.word);
                        wordCnt.put(term.word, tmp + 1);
                    } else wordCnt.put(term.word, 1);
                }
            });

            // TODO: 2024/5/4 发消息和回调的问题, 如果回调依旧在这边阻塞的话, 虽然性能上不太好, 但是更加安全可靠一点
            String key = fileUID+"-"+chunk.getChunkId();
            kafkaTemplate.send(TOPIC, key, wordCnt.toString());



        }
    }


}
