package priv.dawn.workers.service;

import org.apache.dubbo.config.annotation.DubboService;
import priv.dawn.wordcount.api.WorkerService;
import priv.dawn.wordcount.domain.FileChunkListDto;
import priv.dawn.workers.wrapper.FileServiceWrapper;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: Dawn Yang
 * @Since: 2024/10/20/15:31
 */
@DubboService
public class WorkerServiceImpl implements WorkerService {

    @Resource
    FileServiceWrapper fileServiceWrapper;

    @Override
    public List<Integer> countWordsOfChunk(FileChunkListDto fileChunkListDto) {
        Integer fileUid = fileChunkListDto.getFileUid();
        Long taskId = fileChunkListDto.getTaskId();
        List<Integer> chunkIds = fileChunkListDto.getChunkIds();


        return null;
    }

    @Override
    public void countWordOfChunkAsync(FileChunkListDto fileChunkListDto) {
    }
}
