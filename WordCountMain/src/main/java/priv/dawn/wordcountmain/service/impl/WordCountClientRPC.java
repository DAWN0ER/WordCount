package priv.dawn.wordcountmain.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import priv.dawn.mapreduceapi.api.WorkerService;
import priv.dawn.wordcountmain.domain.FileWordCountStateEnum;
import priv.dawn.wordcountmain.mapper.FileMapper;
import priv.dawn.wordcountmain.pojo.dto.FileInfoDTO;
import priv.dawn.wordcountmain.pojo.vo.WordCountListVO;
import priv.dawn.wordcountmain.service.WordCountService;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service("wordCountClient")
public class WordCountClientRPC implements WordCountService {

    @Resource
    @Qualifier("webSocketThreadPool")
    ThreadPoolTaskExecutor executor;


    @DubboReference
    WorkerService workerService;

    @Autowired // TODO: 2024/5/11 这个 Mapper 实际上是代替文件分块储存服务的 API, 后续会优化简单优化性能
    FileMapper fileMapper;

    @Override
    public FileWordCountStateEnum startCountWord(int fileUID) {

        FileInfoDTO info = fileMapper.getFileInfoById(fileUID);
        int chunkNum = info.getChunkNum();

        if (info.getChunkNum() <= 0) return FileWordCountStateEnum.FILE_NOT_FOUNT;
        if (!workerService.createOrder(fileUID, chunkNum)) return FileWordCountStateEnum.START_FAIL;

        // 每个chunk 是 2kb 的数据, 希望每个worker能一次处理2mb-3mb的数据
        log.info("Order created: " + fileUID);
        int chunksPreWorker = 10;
        int workersNum = Math.round(1.f * chunkNum / chunksPreWorker);
        int begin = 1;
        // TODO: 2024/5/15 报错了的话还是直接去RPC那边看报错, 暂时是这个策略, 后续通过查资料再来看看怎么解决
        while (workersNum-- > 1) {
            workerService.loadFile(fileUID, begin, chunksPreWorker);
            begin += chunksPreWorker;
        }
        workerService.loadFile(fileUID, begin, chunkNum - begin + 1);

        executor.execute(()->{
            float progress;
            do{
                progress = workerService.getProgress(fileUID); // 获取进度

            }while (progress<=100);

        });

        return FileWordCountStateEnum.START_SUCCESS;
    }


    @Override
    // TODO: 2024/5/15 需要重新设计逻辑, 加上本地代理优化和一些 websocket
    public WordCountListVO getWordCounts(int fileUID) {
        List<String> wcs = workerService.getWords(fileUID);
        return new WordCountListVO(fileUID,wcs,"=");
    }
}
