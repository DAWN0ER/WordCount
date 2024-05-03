package priv.dawn.workers.service.impl;

import org.apache.dubbo.config.annotation.DubboService;
import priv.dawn.mapreduceapi.api.WorkerService;

import java.util.List;

@DubboService
public class WorkerServiceImpl implements WorkerService {

    @Override
    public void loadFile(int fileUID, int chunkBegin, int chunkNum) {

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
