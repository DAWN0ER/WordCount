package priv.dawn.wordcount.wrapper;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import priv.dawn.wordcount.api.WorkerService;
import priv.dawn.wordcount.dao.service.FileStoreDaoService;
import priv.dawn.wordcount.domain.ChunkCountTaskDto;
import priv.dawn.wordcount.pojo.dto.DaoFileInfoDto;
import priv.dawn.wordcount.utils.MistUidGenerator;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: Dawn Yang
 * @Since: 2024/10/22/13:53
 */

@Service
public class WorkerServiceWrapper {

    @DubboReference
    private WorkerService workerService;

    @Resource
    private FileStoreDaoService fileStoreDaoService;

    public void countWordOfFile(int fileUid){

        long taskId = MistUidGenerator.getInstance().getUid();
        ChunkCountTaskDto taskDto = new ChunkCountTaskDto();
        DaoFileInfoDto fileInfo = fileStoreDaoService.getFileInfo(fileUid);

        taskDto.setTaskId(taskId);
        taskDto.setFileUid(fileUid);

        workerService.countWordOfChunkAsync(taskDto);
    }

}
