package priv.dawn.wordcountmain;


import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import priv.dawn.wordcountmain.domain.FileWordCountStateEnum;
import priv.dawn.wordcountmain.service.WordCountService;

import java.util.Date;
import java.util.List;

@Slf4j
public class WordCountServiceTest extends WordCountMainApplicationTests {

    @Autowired
    private WordCountService service;

    @Test
    public void simpleTest() {
        int uid = 923965605;

        FileWordCountStateEnum stateEnum = service.startCountWord(uid);
        log.info(stateEnum.toString());
        float progress;
        do{
            progress = service.getProgress(uid);
            log.info(new Date()+" progress: "+ progress);
        }while (progress<100);
    }

    @Test
    public void sTest2(){
        int uid = 923965605;

        List<String> list = service.getWordCounts(uid);
        log.info(list.toString());
    }
}
