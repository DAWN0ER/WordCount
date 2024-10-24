package priv.dawn.wordcount.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.redisson.api.RBucket;
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
        // TODO 任务持久化
        // TODO Redis 里面的东西还很麻烦
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
        if (taskId <= 0) {
            log.error("参数异常:taskId:{}", taskId);
            return -1;
        }
        String keyOfChunkNum = String.format("word_count_%d", taskId);
        RBucket<Integer> bucket = wordCountRedisson.getBucket(keyOfChunkNum);
        if (!bucket.isExists()) {
            log.error("任务过期或启动失败:taskId:{}", taskId);
            return -1;
        }
        // TODO 要修改, 包括 Worker 和 Reducer
//        Integer chunkNum = bucket.get();
//        String redisKeys = String.format("word_count_%d_p*", taskId);
//        Iterable<String> keys = wordCountRedisson.getKeys().getKeysByPattern(redisKeys);
//        double sum = 0.0;
//        int partitionCnt = 0;
//        for (String key : keys) {
//            RBitSet bitSet = wordCountRedisson.getBitSet(key);
//            sum += bitSet.cardinality();
//            partitionCnt++;
//        }
//        return sum / partitionCnt / chunkNum;
        return 0.0;
    }

}
