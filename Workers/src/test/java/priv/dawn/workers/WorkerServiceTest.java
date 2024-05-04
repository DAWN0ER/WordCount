package priv.dawn.workers;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import priv.dawn.mapreduceapi.api.WorkerService;

public class WorkerServiceTest extends WorkersApplicationTests{

    @Autowired
    WorkerService service;

    @Test
    public void simpleThreadTest() throws InterruptedException {

    }

}
