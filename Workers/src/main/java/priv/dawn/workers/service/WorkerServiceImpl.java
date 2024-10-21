package priv.dawn.workers.service;

import org.apache.commons.collections.MapUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import priv.dawn.wordcount.api.WorkerService;
import priv.dawn.wordcount.domain.ChunkDto;
import priv.dawn.wordcount.domain.ChunkCountTaskDto;
import priv.dawn.workers.mq.service.KafkaWrapperService;
import priv.dawn.workers.utils.SegmentCounter;
import priv.dawn.workers.utils.hash.HashEnum;
import priv.dawn.workers.utils.hash.WordHashFunction;
import priv.dawn.workers.utils.hash.WordHashFunctionFactory;
import priv.dawn.workers.wrapper.FileServiceWrapper;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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

    @Override
    public Integer countWordsOfChunk(ChunkCountTaskDto chunkCountTaskDto) {
        return countWordsOfChunkCore(chunkCountTaskDto);
    }

    @Override
    public void countWordOfChunkAsync(ChunkCountTaskDto chunkCountTaskDto) {
    }

    private Integer countWordsOfChunkCore(ChunkCountTaskDto chunkCountTaskDto) {
        if (Objects.isNull(chunkCountTaskDto)) {
            logger.error("[countWordsOfChunkCore] 参数为 null");
        }
        Integer fileUid = chunkCountTaskDto.getFileUid();
        Long taskId = chunkCountTaskDto.getTaskId();
        Integer chunkId = chunkCountTaskDto.getChunkId();
        if (Objects.isNull(fileUid) || fileUid <= 0
                || Objects.isNull(taskId) || taskId <= 0
                || Objects.isNull(chunkId) || chunkId <= 0) {
            logger.error("[countWordsOfChunkCore] 参数异常: fileUid:{},taskId:{},chunkId:{}", fileUid, taskId, chunkId);
        }
        logger.info("[countWordsOfChunkCore] 参数: fileUid:{},taskId:{},chunkId:{}", fileUid, taskId, chunkId);
        ChunkDto chunk = fileServiceWrapper.getChunk(fileUid, chunkId);
        if(Objects.isNull(chunk)){
            logger.error("[countWordsOfChunkCore] RPC 查询结果为 null");
            return 0;
        }
        String context = chunk.getContext();
        Map<String, Integer> wordCount = SegmentCounter.countWordOf(context);
        if(MapUtils.isEmpty(wordCount)){
            logger.warn("[countWordsOfChunkCore] 对 Chunk:{} 分词计数结果为空!", chunk.getChunkId());
            return 1;
        }
        Set<String> words = wordCount.keySet();
        // TODO 这边换一个 Hash 算法 Map 到不同的 Partition 去

        return 1;
    }
}
