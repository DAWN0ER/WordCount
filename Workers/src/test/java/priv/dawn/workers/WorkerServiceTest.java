package priv.dawn.workers;

import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;
import org.apache.kafka.common.PartitionInfo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import priv.dawn.mapreduceapi.api.WorkerService;
import priv.dawn.workers.utils.ProgressManager;

import java.util.List;

public class WorkerServiceTest extends WorkersApplicationTests {

    @Autowired
    WorkerService service;

    @Autowired
    ProgressManager manager;

    @Autowired
    KafkaTemplate<String, String> kafka;


    @Test
    public void simpleTest() throws InterruptedException {
        int file = 1222161892;
        int chunkNum = 6;

        manager.createProgress(file, chunkNum); // 创建进程信息(订单信息)

        int res = service.loadFile(file, 1, chunkNum);
        log.info("finished " + res);
        Thread.sleep(5 * 1000); // 等15s看情况
        log.info("Progress: " + service.getProgress(file));
        log.info("exit");
    }

    @Test
    public void tokenTest() {
        for (Term term : StandardTokenizer.segment("你是我的，宝贝, 宝贝、你①真的很美")) {
            log.info(term.toString());
        }

    }

    @Test
    public void kafkaTest() {
        List<PartitionInfo> list = kafka.partitionsFor("word_count");
        for (PartitionInfo info : list) {
            log.info(info.toString());
        }
    }


}
