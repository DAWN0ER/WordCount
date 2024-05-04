package priv.dawn.workers;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import priv.dawn.mapreduceapi.api.WorkerService;

public class WorkerServiceTest extends WorkersApplicationTests{

    @Autowired
    WorkerService service;

    @Test
    public void simpleTest() throws InterruptedException {
        int res = service.loadFile(1222161892,1,7);
        logger.info("finished "+res);
        Thread.sleep(5*1000);
    }

}
