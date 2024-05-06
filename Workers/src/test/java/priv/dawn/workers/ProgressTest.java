package priv.dawn.workers;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import priv.dawn.kafkamessage.message.CustomMessage;
import priv.dawn.workers.mapper.ProgressMapper;
import priv.dawn.workers.utils.ProgressManager;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProgressTest extends WorkersApplicationTests {

    @Autowired
    private ProgressMapper mapper;

    @Autowired
    private ProgressManager manager;

    @Test
    public void mapperTest() {
        int uid = 996;
        Float f = mapper.getProgress(uid);
        log.info("if progress don't exist, the getProgress is: " + f);
        manager.createProgress(uid, 3);
        mapper.progressAdvanceOne(uid);
        f = mapper.getProgress(uid);
        log.info("if progress is " + uid + " the getProgress is: " + f);
    }

    @Test
    public void mapperTestForInc() {
        int uid = 999;
        mapper.progressAdvanceOne(uid);
        Float f = mapper.getProgress(uid);
        log.info("if progress is " + uid + " the getProgress is: " + f);
    }

    @Test
    public void threadTest() {
        int pn = 4;
        int chunId = 3;
        int uid = 996;
        String json = new CustomMessage(chunId, new ArrayList<>()).toJsonStr();
        ProducerRecord<String, String> record = new ProducerRecord<>("topic", 0, String.valueOf(uid), json);
        ExecutorService pool = Executors.newFixedThreadPool(pn);
        int loop = pn;
        while (loop-- > 0) {
            pool.execute(() -> {
                log.info(Thread.currentThread().getName() + " working");
                manager.updateProgress(record, pn);
            });
        }


    }


}
