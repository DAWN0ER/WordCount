package priv.dawn.wordcountmain.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import priv.dawn.mapreduceapi.api.WorkerService;
import priv.dawn.wordcountmain.domain.WordCountStateEnum;
import priv.dawn.wordcountmain.domain.ProgressWebSocketServer;
import priv.dawn.wordcountmain.mapper.FileMapper;
import priv.dawn.wordcountmain.pojo.dto.FileInfoDTO;
import priv.dawn.wordcountmain.pojo.vo.WordCountListVO;
import priv.dawn.wordcountmain.service.WordCountService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service("wordCountClient")
public class WordCountClientRPC implements WordCountService {

    private static final int CHUNKS_PRE_WORKER = 10;

    @Resource
    @Qualifier("webSocketThreadPool")
    ThreadPoolTaskExecutor webSocketExecutor;

    @DubboReference
    WorkerService workerService;

    @Autowired
    FileMapper fileMapper;

    @Override
    public WordCountStateEnum startCountWord(int fileUID) {

        FileInfoDTO info = fileMapper.getFileInfoById(fileUID);
        int chunkNum = info.getChunkNum();

        if (info.getChunkNum() <= 0) return WordCountStateEnum.FILE_NOT_FOUNT;
        if (!workerService.createOrder(fileUID, chunkNum)) return WordCountStateEnum.START_FAIL;

        // 每个chunk 是 2kb 的数据, 希望每个worker能一次处理2mb-3mb的数据
        log.info("Order created: " + fileUID);
        int workersNum = Math.round(1.f * chunkNum / CHUNKS_PRE_WORKER);
        int begin = 1;

        while (workersNum-- > 1) {
            workerService.loadFile(fileUID, begin, CHUNKS_PRE_WORKER);
            begin += CHUNKS_PRE_WORKER;
        }
        workerService.loadFile(fileUID, begin, chunkNum - begin + 1);

        webSocketExecutor.execute(() -> {
            float progress;
            do {
                progress = workerService.getProgress(fileUID); // 获取进度
                ProgressWebSocketServer.sendMessage(fileUID, "已经完成: " + progress + "%");
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    log.error(e.toString());
                }
            } while (progress < 100);
        });

        return WordCountStateEnum.START_SUCCESS;
    }

    @Override
    // TODO: 2024/5/15 本地缓存代理没写, 以后写
    public WordCountListVO getWordCounts(int fileUID) {
        if(workerService.getProgress(fileUID)>=100){
            List<String> wcs = workerService.getWords(fileUID);
            return new WordCountListVO(fileUID, wcs, "=");
        }
        return new WordCountListVO(fileUID,new ArrayList<>());
    }
}
