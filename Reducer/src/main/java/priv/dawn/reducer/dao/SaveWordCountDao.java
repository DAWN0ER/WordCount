package priv.dawn.reducer.dao;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import priv.dawn.reducer.mapper.WordCountMapper;

import javax.annotation.Resource;
import java.util.HashMap;

@Slf4j
@Repository
public class SaveWordCountDao {

    @Autowired
    WordCountMapper wcMapper;

    @Qualifier("reducerRedisson")
    @Resource
    RedissonClient reducerRedisson;

    // TODO:
    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = RuntimeException.class)
    public void saveFromWordCountMap(int fileUID, int partition, HashMap<String, Integer> wordCntMap) {

        String lockKey = fileUID + "-" + partition;
        RLock lock = reducerRedisson.getLock(lockKey);
        lock.lock();

        try{
            wordCntMap.forEach((word, count) -> {
                if (checkIfIllegal(word, count)) {
                    throw new RuntimeException("非法参数: " + word + " | " + count);
                }
                wcMapper.saveWordCount(fileUID, word, count);
            });
        }finally {
            lock.unlock();
        }

    }

    private boolean checkIfIllegal(String w, int c) {
        if (w == null || "".equals(w)) return true;
        return c < 0;
    }


}
