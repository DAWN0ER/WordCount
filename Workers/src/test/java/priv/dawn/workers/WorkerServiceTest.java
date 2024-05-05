package priv.dawn.workers;

import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;
import org.apache.kafka.common.PartitionInfo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import priv.dawn.mapreduceapi.api.WorkerService;

import java.util.List;

public class WorkerServiceTest extends WorkersApplicationTests{

    @Autowired
    WorkerService service;

    @Autowired
    KafkaTemplate<String,String> kafka;

    @Test
    public void simpleTest() throws InterruptedException {
        int res = service.loadFile(1222161892,1,3);
        logger.info("finished "+res);
        Thread.sleep(1000);
    }

    @Test
    public void tokenTest(){
        for (Term term : StandardTokenizer.segment("你是我的，宝贝, 宝贝、你①真的很美")) {
            logger.info(term.toString());
        }

    }

    @Test
    public void kafkaTest(){
        List<PartitionInfo> list = kafka.partitionsFor("word_count");
        for(PartitionInfo info:list){
            logger.info(info.toString());
        }
    }

}
