package priv.dawn.wordcount.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import priv.dawn.wordcount.api.WorkerService;
import priv.dawn.wordcount.dao.service.FileStoreDaoService;
import priv.dawn.wordcount.domain.ChunkCountTaskDto;
import priv.dawn.wordcount.pojo.dto.DaoFileInfoDto;
import priv.dawn.wordcount.pojo.enums.FileInfoStatusEnums;
import priv.dawn.wordcount.utils.MistUidGenerator;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.Optional;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: Dawn Yang
 * @Since: 2024/10/24/20:41
 */

@Slf4j
@Service
public class WordCountClientService {

    @DubboReference
    private WorkerService workerService;

    @Resource
    private FileStoreDaoService fileStoreDaoService;

    @Resource
    private RedissonClient wordCountRedisson;

    public long startWordCountOfFile(int fileUid) {
        DaoFileInfoDto fileInfo = fileStoreDaoService.getFileInfo(fileUid);
        if (Objects.isNull(fileInfo)) {
            log.error("[startWordCountOfFile] 找不到文件: fileUid:{}", fileUid);
            return -1;
        }
        Integer status = fileInfo.getStatus();
        if (!FileInfoStatusEnums.STORED.getStatus().equals(status)) {
            log.error("[startWordCountOfFile] 文件状态异常: status={}", status);
            return -1;
        }
        int chunkNum = Optional.ofNullable(fileInfo.getChunkNum()).orElse(0);
        if (chunkNum <= 0) {
            log.error("[startWordCountOfFile] 文件没有找到chunk: fileUid={}", fileUid);
            return -1;
        }

        long taskId = MistUidGenerator.getInstance().getUid();
        log.info("开始 WordCount 任务:taskId;{},fileUid:{}", taskId, fileUid);

        // TODO 任务持久化以后做，暂时先用 Redis
//        RBucket<Integer> bucket = wordCountRedisson.getBucket(String.format("word_count_task_%d", taskId));
//        bucket.set(chunkNum);

        ChunkCountTaskDto param = new ChunkCountTaskDto();
        param.setFileUid(fileUid);
        param.setTaskId(taskId);
        for (int chunkId = 1; chunkId <= chunkNum; chunkId++) {
            param.setChunkId(chunkId);
            workerService.countWordOfChunkAsync(param);
        }
        return taskId;
    }

    public double getProgress(long taskId) {
//        if (taskId <= 0) {
//            log.error("参数异常:taskId:{}", taskId);
//            return -1;
//        }
//        RBucket<Integer> bucket = wordCountRedisson.getBucket(String.format("word_count_task_%d", taskId));
//        if (!bucket.isExists()) {
//            log.error("任务过期或启动失败:taskId:{}", taskId);
//            return -1;
//        }
//        Integer chunkNum = bucket.get();
//        RList<Long> list = wordCountRedisson.getList(String.format("word_count_chunks_%d", taskId));
//        int[] keys = new int[chunkNum];
//        for (int i = 0; i < chunkNum; i++) {
//            keys[i] = i;
//        }
//        List<Long> progress = list.get(keys);
//        if (CollectionUtils.isEmpty(progress)) {
//            log.error("没有搜索到进度:taskId:{}", taskId);
//            return 0.0;
//        }
//        double count = 0.0;
//        for (Long aLong : progress) {
//            if (Objects.nonNull(aLong) && aLong == 0) {
//                count += 1;
//            }
//        }
//        return count / chunkNum;
        return 100.0;
    }

}
