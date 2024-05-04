package priv.dawn.workers.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import priv.dawn.mapreduceapi.api.WorkerService;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.Executor;

//@DubboService
@Service
public class WorkerServiceImpl implements WorkerService {

    private final Logger logger = LoggerFactory.getLogger(WorkerServiceImpl.class);

    @Qualifier(value = "WorkerReadThreadPool")
    @Resource
    private Executor readThreadPool;

    @Override
    public int loadFile(int fileUID, int chunkBegin, int chunkNum) {

        return 0;
    }

    @Override
    public float getProgress(int fileUID) {
        return 0;
    }

    @Override
    public List<String> getWords(int fileUID) {
        return null;
    }


}
