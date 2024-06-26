package priv.dawn.workers.utils;

import com.sun.istack.internal.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBucket;
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

    // TODO: 2024/6/16 存在的问题:
    //  如果前面做了幂等处理, 那么这里的 double check 就多余了;
    //  在累计计数的时候依旧使用的是 on duplicate 更新, 存在对主键 ID 的浪费
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

    // DONE: 2024/5/15 Redis 优化为什么没做, 服了
    public float getProgress(int fileUID) {
        String key = "Progress-" + fileUID;
        String lockKey = "Progress-Lock-"+fileUID;
        RBucket<Double> rBucket = redissonClient.getBucket(key); // 喵的 redisson 默认转换为 double 真的是踩大坑
        Double aDoubleProgress = rBucket.get();
        float progress;
        if (aDoubleProgress!=null){
            progress = aDoubleProgress.floatValue();
            return progress;
        }

        // 防止缓存穿透和缓存击穿
        RLock lock = redissonClient.getLock(lockKey);
        try{
            lock.lock();
            aDoubleProgress = rBucket.get();
            if (aDoubleProgress!=null){
                progress = aDoubleProgress.floatValue();
                return progress;
            }
            Float mapperProgress = progressMapper.getProgress(fileUID);
            if(mapperProgress==null){
                rBucket.set(0.0,new Random().nextInt(5)+10,TimeUnit.SECONDS); // 大概 15s 左右获取不到进度
                return 0.0f;
            }
            if(mapperProgress>=100){
                rBucket.set(100.0);
                return 100.0f;
            }else{
                rBucket.set((double)mapperProgress,100,TimeUnit.MILLISECONDS); // 每秒更新一次
                return mapperProgress;
            }
        }finally {
            lock.unlock();
        }

    }
}
