package priv.dawn.workers;

import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;
import lombok.SneakyThrows;
import org.apache.kafka.common.PartitionInfo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import priv.dawn.mapreduceapi.api.WorkerService;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

public class WorkerServiceTest extends WorkersApplicationTests{

    @Autowired
    WorkerService service;

    @Autowired
    KafkaTemplate<String,String> kafka;



    @Test
    public void simpleTest() throws InterruptedException {
        int res = service.loadFile(114514,1,1);
        logger.info("finished "+res);
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
