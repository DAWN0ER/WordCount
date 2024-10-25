package priv.dawn.workers.service;

import com.google.gson.Gson;
import org.apache.commons.collections.MapUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.redisson.api.RList;
import org.redisson.api.RLock;
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
import java.util.concurrent.TimeUnit;

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

    private final Gson gson = new Gson();

    private final WordHashFunction murmurHash = WordHashFunctionFactory.getHashFunc(HashEnum.Murmur3);

    @Resource
    private FileServiceWrapper fileServiceWrapper;

    @Resource
    private KafkaWrapperService kafkaWrapperService;

    @Resource
    private ThreadPoolTaskExecutor workerReadThreadPool;

    @Resource
    private RedissonClient workerRedisson;

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
        logger.info("[countWordsOfChunkCore] 对 Chunk:{} 分词计数结果大小:{}",chunk.getChunkId(),wordCount.size());
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
        logger.info("[countWordsOfChunkCore]taskId:{} ChunkId:{} 对应 PartitionNum:{}",taskId,chunkId,partitionNum);

        // Redis 更新对应 chunkId 的 partition 数量
        // TODO Redisson 怎么会导致线程等待啊,离谱
        saveRedis(taskId,chunkId,partitionNum);

        List<WordCountMessage> messageList = initMessageList(partitionNum, taskId, fileUid, chunkId, wordCount.size());
        logger.info("[initMessageList] 结果:{}",gson.toJson(messageList));

        // 换一个 Hash 算法 Map 到不同的 Partition 去
        Set<String> words = wordCount.keySet();
        for (String word : words) {
            Integer count = wordCount.get(word);
            int partition = murmurHash.hash(word) % partitionNum;
            logger.info("[Hash]:word:{},count:{},partition:{}",word,count,partition);
            WordCountEntry wordCountEntry = new WordCountEntry();
            wordCountEntry.setWord(word);
            wordCountEntry.setCount(count);
            messageList.get(partition).getWordCounts().add(wordCountEntry);
        }
        logger.info("[countWordsOfChunkCore] 构造消息成功:{}",gson.toJson(messageList));
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

    private void saveRedis(long taskId, int chunkId, int partitionNum) {
        String chunkLock = "word_count_chunk_%d";
        RList<Long> list = workerRedisson.getList(String.format("word_count_chunks_%d", taskId));
        RLock lock = workerRedisson.getLock(String.format(chunkLock, chunkId));
        logger.info("[countWordsOfChunkCore]记录初始化: taskId:{}, chunkId:{}",taskId,chunkId);
        boolean success = false;
        try {
            logger.info("尝试上锁:{}",lock.getName());
            success = lock.tryLock(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.error("上锁线程中断: taskId:{}, chunkId:{}", taskId, chunkId);
            return;
        }
        if (!success) {
            logger.error("上锁失败: taskId:{}, chunkId:{}", taskId, chunkId);
            return;
        }
        try {
            int idx = chunkId - 1;
            list.fastSet(idx, (1L << partitionNum) - 1);
        } finally {
            lock.unlock();
        }
        logger.info("[countWordsOfChunkCore]完成记录初始化: taskId:{}, chunkId:{}",taskId,chunkId);
    }
}
