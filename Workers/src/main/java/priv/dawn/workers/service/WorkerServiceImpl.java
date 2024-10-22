package priv.dawn.workers.service;

import org.apache.commons.collections.MapUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import priv.dawn.mq.domain.WordCountEntry;
import priv.dawn.mq.domain.WordCountMessage;
import priv.dawn.wordcount.api.WorkerService;
import priv.dawn.wordcount.domain.ChunkCountTaskDto;
import priv.dawn.wordcount.domain.ChunkDto;
import priv.dawn.workers.mq.enums.TopicEnums;
import priv.dawn.workers.mq.service.KafkaWrapperService;
import priv.dawn.workers.utils.SegmentCounter;
import priv.dawn.workers.utils.hash.HashEnum;
import priv.dawn.workers.utils.hash.WordHashFunction;
import priv.dawn.workers.utils.hash.WordHashFunctionFactory;
import priv.dawn.workers.wrapper.FileServiceWrapper;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: Dawn Yang
 * @Since: 2024/10/20/15:31
 */
@DubboService
public class WorkerServiceImpl implements WorkerService {

    private final Logger logger = LoggerFactory.getLogger(WorkerServiceImpl.class);

    private final WordHashFunction murmurHash = WordHashFunctionFactory.getHashFunc(HashEnum.Murmur3);

    @Resource
    private FileServiceWrapper fileServiceWrapper;

    @Resource
    private KafkaWrapperService kafkaWrapperService;

    @Resource
    private ThreadPoolTaskExecutor workerReadThreadPool;

    @Resource
    private RedissonClient redissonClient;

    @Override
    public int countWordsOfChunk(ChunkCountTaskDto chunkCountTaskDto) {
        return countWordsOfChunkCore(chunkCountTaskDto);
    }

    @Override
    public void countWordOfChunkAsync(ChunkCountTaskDto chunkCountTaskDto) {
        workerReadThreadPool.submit(() -> {
            countWordsOfChunkCore(chunkCountTaskDto);
        });
    }

    private int countWordsOfChunkCore(ChunkCountTaskDto chunkCountTaskDto) {
        if (Objects.isNull(chunkCountTaskDto)) {
            logger.error("[countWordsOfChunkCore] 参数为 null");
            return 0;
        }
        Integer fileUid = chunkCountTaskDto.getFileUid();
        Long taskId = chunkCountTaskDto.getTaskId();
        Integer chunkId = chunkCountTaskDto.getChunkId();
        if (Objects.isNull(fileUid) || fileUid <= 0
                || Objects.isNull(taskId) || taskId <= 0
                || Objects.isNull(chunkId) || chunkId <= 0) {
            logger.error("[countWordsOfChunkCore] 参数异常: fileUid:{},taskId:{},chunkId:{}", fileUid, taskId, chunkId);
            return 0;
        }

        logger.info("[countWordsOfChunkCore] 参数: fileUid:{},taskId:{},chunkId:{}", fileUid, taskId, chunkId);
        ChunkDto chunk = fileServiceWrapper.getChunk(fileUid, chunkId);
        if (Objects.isNull(chunk)) {
            logger.error("[countWordsOfChunkCore] RPC 查询结果为 null");
            return 0;
        }
        String context = chunk.getContext();
        Map<String, Integer> wordCount = SegmentCounter.countWordOf(context);
        if (MapUtils.isEmpty(wordCount)) {
            logger.warn("[countWordsOfChunkCore] 对 Chunk:{} 分词计数结果为空!", chunk.getChunkId());
            return 0;
        }
        String topic = TopicEnums.WORD_COUNT.getTopic();
        int partitionNum = kafkaWrapperService.getPartitionNum(topic);
        if (partitionNum <= 0) {
            logger.error("[countWordsOfChunkCore] Topic:{} 获取 partition 信息失败", topic);
            return 0;
        }
        // TODO 需要在这里同步到 Redis 里面去记录 taskId 的 BitSet 中

        List<WordCountMessage> messageList = initMessageList(partitionNum, taskId, fileUid, chunkId, wordCount.size());

        // 换一个 Hash 算法 Map 到不同的 Partition 去
        for (Map.Entry<String, Integer> entry : wordCount.entrySet()) {
            String word = entry.getKey();
            Integer count = entry.getValue();
            int partition = murmurHash.hash(word) % partitionNum;
            WordCountEntry wordCountEntry = new WordCountEntry();
            wordCountEntry.setWord(word);
            wordCountEntry.setCount(count);
            messageList.get(partition).getWordCounts().add(wordCountEntry);
        }
        for (int partition = 0; partition < partitionNum; partition++) {
            kafkaWrapperService.send(topic, messageList.get(partition), partition);
        }
        return partitionNum;
    }

    private List<WordCountMessage> initMessageList(int partitionNum, long taskId, int fileUid, int chunkId, int len) {
        List<WordCountMessage> list = new ArrayList<>(partitionNum);
        for (int i = 0; i < partitionNum; i++) {
            WordCountMessage message = new WordCountMessage();
            message.setTaskId(taskId);
            message.setFileUid(fileUid);
            message.setChunkId(chunkId);
            message.setWordCounts(new ArrayList<>(len / partitionNum));
            list.add(message);
        }
        return list;
    }
}
