package priv.dawn.wordcountmain;


import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import priv.dawn.wordcountmain.service.WordCountService;

public class WordCountServiceTest extends WordCountMainApplicationTests {

    @Autowired
    private WordCountService service;
    @Qualifier("redisTemplate")
    @Autowired
    private RedisTemplate redis;

    @Test
    public void simpleTest() {
        int n = 3912*3+3;
        System.out.println("redis = " + redis.opsForValue().get(n));
        System.out.println("p =" + service.getProgress(n));
        System.out.println("redis = " + redis.opsForValue().get(n));
        System.out.println("p =" + service.getProgress(n));
    }
}
