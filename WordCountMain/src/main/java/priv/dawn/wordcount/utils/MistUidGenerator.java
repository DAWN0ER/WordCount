package priv.dawn.wordcount.utils;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description: 分布式迷雾算法
 * @Auther: Dawn Yang
 * @Since: 2024/10/12/21:17
 */
public class MistUidGenerator {

    private final long START = 1704038400000L; //2024-01-01 00:00:00
    private final AtomicLong inc = new AtomicLong(System.currentTimeMillis() - START);
    private volatile static MistUidGenerator instance;

    private MistUidGenerator() {
    }

    public static MistUidGenerator getInstance() {
        // 懒加载单例
        if (Objects.isNull(instance)) {
            synchronized (MistUidGenerator.class) {
                if (Objects.isNull(instance)) {
                    instance = new MistUidGenerator();
                    return instance;
                }
            }
        }
        return instance;
    }

    public long getUid() {
        int r1 = ThreadLocalRandom.current().nextInt(256);
        int r2 = ThreadLocalRandom.current().nextInt(256);
        long current = inc.incrementAndGet();
        long currentTime = System.currentTimeMillis() - START;
        if (current < currentTime) {
            synchronized (MistUidGenerator.class) {
                if (inc.get() < currentTime) {
                    inc.set(currentTime);
                }
            }
        }
        return current << 16 | r2 << 8 | r1;
    }

}
