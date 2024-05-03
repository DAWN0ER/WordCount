package priv.dawn.wordcountmain.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import priv.dawn.wordcountmain.domain.FileWordCountStateEnum;
import priv.dawn.wordcountmain.domain.WorkerDubboCilent;
import priv.dawn.wordcountmain.service.WordCountService;

import javax.annotation.Resource;
import java.lang.invoke.SwitchPoint;
import java.util.List;

@Service
public class WordCountServiceImpl implements WordCountService {

    static final String ALREADY_FINISHED = "ALREADY_FINISHED";
    static final String ON_PROCESS = "ON_PROCESS";
    static final String HAVE_NOT_PROCESSED = "HAVE_NOT_PROCESSED";

    @Autowired
    private WorkerDubboCilent workerDubboCilent;

    @Qualifier("redisTemplate")
    @Autowired
    private RedisTemplate progressRedisProxy;

    @Override
    public FileWordCountStateEnum startCountWord(int fileUID) { // 后续改用用一个 enum 代替算了

        // 查看是否已经完成, 或者正在进行
        boolean isFinished = (boolean) progressRedisProxy.opsForValue().get(fileUID);
        if(Boolean.FALSE.equals(isFinished)) return FileWordCountStateEnum.ON_PROCESS;
        if(Boolean.TRUE.equals(isFinished)) return FileWordCountStateEnum.ALREADY_FINISHED;

        int res = workerDubboCilent.startCountWord(fileUID);

        switch (res){
            case 1:
                return FileWordCountStateEnum.START_SUCCESS;
            case 0:
                return FileWordCountStateEnum.START_FAIL;
        }
        return FileWordCountStateEnum.HAVE_NOT_PROCESSED;
    }

    // TODO: 2024/5/3
    // 可能需要添加分布式锁防止缓存穿透
    @Override
    public float getProgress(int fileUID) {
        // 通过 Redis 缓存代理, 避免对 RPC 的调用
        String state = (String) progressRedisProxy.opsForValue().get(fileUID);
        if(ALREADY_FINISHED.equals(state)) return 100f;
        if(HAVE_NOT_PROCESSED.equals(state)) return -1f;
        // 缓存未命中, 访问RPC
        float progress = workerDubboCilent.getProgress(fileUID);
        if(progress>=100f) progressRedisProxy.opsForValue().set(fileUID,ALREADY_FINISHED);
        else if(progress<0) progressRedisProxy.opsForValue().set(fileUID,HAVE_NOT_PROCESSED);
        else if(!ON_PROCESS.equals(state)) progressRedisProxy.opsForValue().set(fileUID,ON_PROCESS);
        return progress;
    }

    @Override
    public List<String> getWordCounts(int fileUID) {
        return null;
    }
}
