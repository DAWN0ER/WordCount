package priv.dawn.workers.service.impl;

import lombok.AllArgsConstructor;
import org.apache.kafka.common.protocol.types.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import priv.dawn.mapreduceapi.api.WorkerService;
import priv.dawn.workers.mapper.ChunkReadMapper;
import priv.dawn.workers.pojo.ChunkDTO;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;

//@DubboService
@Service
public class WorkerServiceImpl implements WorkerService {

    private final Logger logger = LoggerFactory.getLogger(WorkerServiceImpl.class);

    @Qualifier(value = "WorkerReadThreadPool")
    @Resource
    private ThreadPoolTaskExecutor readThreadPool;

    @Autowired
    private ChunkReadMapper chunkReadMapper;

    // DONE: 2024/5/4 这边线程不够用了之后会抛出拒绝异常, 在这边捕获返回值就是已经进入线程池的chunk的id, 表示完成个数
    @Override
    public int loadFile(int fileUID, int chunkBegin, int chunkNum) {
        logger.info("load " + chunkNum + " chunks of file: " + fileUID + " begin form chunk " + chunkBegin);
        int chunkEnd = chunkBegin + chunkNum - 1;
        List<ChunkDTO> chunks = chunkReadMapper.getChunks(fileUID, chunkBegin, chunkEnd);

        if (chunks.size() != chunkNum) return -1; // 暂时表示有chunk缺失或者没拿到chunk的问题

        int finished = 0;
        for (ChunkDTO chunk : chunks) {
            try {
                readThreadPool.execute(
                        new processChunk(fileUID,chunkNum,chunkBegin,chunk)
                );
                if(chunk.getChunkId()==5) throw new RejectedExecutionException();
                finished = finished + 1;
            } catch (RejectedExecutionException e) {
                logger.info("When file:" + fileUID + " chunk " + chunk.getChunkId() + " was executed, the ThreadPool rejected");
                break;
            }
        }
        return finished;
    }

    @Override
    public float getProgress(int fileUID) {
        return 0;
    }

    @Override
    public List<String> getWords(int fileUID) {
        return null;
    }

    // TODO: 2024/5/4  Runner 处理 chunk 的, 暂时还没写完, 需要整合kafka才行
    @AllArgsConstructor
    private class processChunk implements Runnable {

        private int fileUID;
        private int chunkNum;
        private int chunkBegin;
        private ChunkDTO chunk;

        @Override
        public void run() {
//            logger.info(Thread.currentThread().getName() + " process file " + uid + " chunk " + chunk.getChunkId());
        }
    }



}
