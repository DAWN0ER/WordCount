package priv.dawn.workers.service;

import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import priv.dawn.wordcount.api.WorkerService;
import priv.dawn.wordcount.domain.ChunkDto;
import priv.dawn.wordcount.domain.FileChunkListDto;
import priv.dawn.workers.wrapper.FileServiceWrapper;

import javax.annotation.Resource;
import java.util.Objects;

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

    @Resource
    private FileServiceWrapper fileServiceWrapper;

    @Override
    public Integer countWordsOfChunk(FileChunkListDto fileChunkListDto) {
        return countWordsOfChunkCore(fileChunkListDto);
    }

    @Override
    public void countWordOfChunkAsync(FileChunkListDto fileChunkListDto) {
    }

    private Integer countWordsOfChunkCore(FileChunkListDto fileChunkListDto) {
        if (Objects.isNull(fileChunkListDto)) {
            logger.error("[countWordsOfChunkCore] 参数为 null");
        }
        Integer fileUid = fileChunkListDto.getFileUid();
        Long taskId = fileChunkListDto.getTaskId();
        Integer chunkId = fileChunkListDto.getChunkId();
        if (Objects.isNull(fileUid) || fileUid <= 0
                || Objects.isNull(taskId) || taskId <= 0
                || Objects.isNull(chunkId) || chunkId <= 0) {
            logger.error("[countWordsOfChunkCore] 参数异常: fileUid:{},taskId:{},chunkId:{}", fileUid, taskId, chunkId);
        }
        logger.info("[countWordsOfChunkCore] 参数: fileUid:{},taskId:{},chunkId:{}", fileUid, taskId, chunkId);
        ChunkDto chunk = fileServiceWrapper.getChunk(fileUid, chunkId);
        // TODO 分词计数
        return 0;

    }
}
