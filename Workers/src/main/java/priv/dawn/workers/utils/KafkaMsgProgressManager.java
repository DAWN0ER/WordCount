package priv.dawn.workers.utils;

import com.sun.istack.internal.NotNull;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import priv.dawn.kafkamessage.message.CustomMessage;

import javax.annotation.Resource;

@Component
public class KafkaMsgProgressManager {

    private static final String LOCK = "WORKER_LOCK";

    @Resource
    @Qualifier("workerRedisson")
    private RedissonClient redissonClient;

    // TODO: 2024/5/5 需要 redis 改为 redisson 完成分布式锁
    public void updateProgress(@NotNull SendResult<String, String> success, int partitionNum) {
        ProducerRecord<String, String> record = success.getProducerRecord();
        int fileUID = Integer.parseInt(record.key());
        int chunkId = CustomMessage.getFromJson(record.value()).getChunkId();
        String key = fileUID + "&" + chunkId;
        RAtomicLong cnt = redissonClient.getAtomicLong(key);
        RLock lock = redissonClient.getLock(LOCK);
        long res = cnt.incrementAndGet();
        if (res == partitionNum) {
            lock.lock();
            if (res == partitionNum) {
                // TODO: 2024/5/5 更新进度
                System.out.println("更新进度:" + key);
            }
        }
    }

}
