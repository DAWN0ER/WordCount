package priv.dawn.reducer.repository;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import priv.dawn.kafkamessage.message.CustomMessage;
import priv.dawn.reducer.mapper.WordCountMapper;

import javax.annotation.Resource;

@Slf4j
@Repository
public class SaveWordCountRepository {

    @Autowired
    WordCountMapper wcMapper;

    @Qualifier("reducerRedisson")
    @Resource
    RedissonClient reducerRedisson;

    // 只有同一个 partition 的才会有冲突死锁的问题, 所以提前上锁
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void saveFromMsg(int fileUID, int partition, CustomMessage msg) {

        String lockKey = fileUID + "-" + partition;
        RLock lock = reducerRedisson.getLock(lockKey);
        try {
            lock.lock();
            msg.getWordCountList().forEach(e->{
                String word = e.getWord();
                int count = e.getCount();
                if (checkIfIllegal(word, count)) {
                    throw new RuntimeException("Illegal argument: " + word + " | " + count);
                }
                wcMapper.saveWordCount(fileUID, word, count);
            });
        } finally {
            lock.unlock();
        }

    }

    private boolean checkIfIllegal(String w, int c) {
        if (w == null || "".equals(w)) return true;
        return c < 0;
    }


}
