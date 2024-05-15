package priv.dawn.workers.utils;

import com.sun.istack.internal.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import priv.dawn.kafkamessage.message.CustomMessage;
import priv.dawn.workers.mapper.ProgressMapper;

import javax.annotation.Resource;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class ProgressManager {

    // TODO: 2024/5/6 由于 finished chunk 这个原子自增操作是在 mysql 中通过 On Duplicate key 完成的, 未必线程安全,
    //  所以每次更新进度就需要上锁, 后续改成现在 Redis 里面更新进度, 然后定时推送到 Mysql 这种异步更新策略

    private static final String LOCK = "WORKER_LOCK";

    @Resource
    @Qualifier("workerRedisson")
    private RedissonClient redissonClient;

    @Autowired
    private ProgressMapper progressMapper;

    // TODO: 2024/5/5 需要 redis 改为 redisson 完成分布式锁
    public void updateProgress(@NotNull ProducerRecord<String, String> record, int partitionNum) {
        int fileUID = Integer.parseInt(record.key());
        CustomMessage msg = CustomMessage.getFromJson(record.value());
        if (msg == null) {
            log.error("Record json deserialize fail: " + record);
            return;
        }
        int chunkId = msg.getChunkId();
//        log.info(" Callback: " + fileUID + "-" + chunkId + "-@P" + record.partition());
        updateProgress(fileUID, chunkId, partitionNum);
    }

    public boolean createProgress(int fileUID, int todoChunksNum) {
        try {
            progressMapper.saveNewProgress(fileUID, todoChunksNum);
        } catch (DuplicateKeyException e) {
            return false; // 订单冲突
        }
        return true;
    }

    private void updateProgress(int fileUID, int chunkId, int partitionNum) {
        String key = fileUID + "&" + chunkId; // redis 作为临时计数的 key
        RAtomicLong cnt = redissonClient.getAtomicLong(key);
        RLock lock = redissonClient.getLock(LOCK);
        long res = cnt.incrementAndGet();
        if (res == partitionNum) {
            try {
                lock.lock();
                if (cnt.get() == partitionNum) {
                    progressMapper.progressAdvanceOne(fileUID);
                    cnt.set(0); //
                    cnt.expire(new Random().nextInt(5) + 5, TimeUnit.MINUTES); // 十分钟内删除
//                log.info("Progress-" + key + " finished");
                }
            } finally {
                lock.unlock();
            }
        }
    }

    // TODO: 2024/5/15 Redis 优化为什么没做, 服了
    public Float getProgress(int fileUID) {

        return progressMapper.getProgress(fileUID);

    }
}
