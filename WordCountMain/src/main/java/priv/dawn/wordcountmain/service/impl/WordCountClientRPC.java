package priv.dawn.wordcountmain.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import priv.dawn.mapreduceapi.api.WorkerService;
import priv.dawn.wordcountmain.domain.FileWordCountStateEnum;
import priv.dawn.wordcountmain.mapper.FileMapper;
import priv.dawn.wordcountmain.pojo.dto.FileInfoDTO;
import priv.dawn.wordcountmain.pojo.vo.WordCountListVO;
import priv.dawn.wordcountmain.service.WordCountService;

import java.util.List;

@Slf4j
@Service("wordCountClient")
public class WordCountClientRPC implements WordCountService {

    @DubboReference
    WorkerService workerService;

    @Autowired // TODO: 2024/5/11 这个 Mapper 实际上是代替文件分块储存服务的 API, 后续会优化简单优化性能
    FileMapper fileMapper;

    @Override
    public FileWordCountStateEnum startCountWord(int fileUID) {

        FileInfoDTO info = fileMapper.getFileInfoById(fileUID);
        int chunkNum = info.getChunkNum();

        if (info.getChunkNum() <= 0) return FileWordCountStateEnum.FILE_NOT_FOUNT;
        if (workerService.createOrder(fileUID, chunkNum) < 0) return FileWordCountStateEnum.START_FAIL;

        // 每个chunk 是 2kb 的数据, 希望每个worker能一次处理2mb-3mb的数据
        log.info("Order created: " + fileUID);
        int chunksPreWorker = 10;
        int workersNum = Math.round(1.f * chunkNum / chunksPreWorker);
        int begin = 1;
        while (workersNum-- > 1) {
            workerService.loadFile(fileUID, begin, chunksPreWorker);
            begin += chunksPreWorker;
            // TODO: 2024/5/9 主打一个错了但不改的策略, 后续解决一下这个问题
        }
        workerService.loadFile(fileUID, begin, chunkNum - begin + 1);

        return FileWordCountStateEnum.START_SUCCESS;
    }

    @Override
    public float getProgress(int fileUID) {
        return workerService.getProgress(fileUID);
    }

    @Override
    public WordCountListVO getWordCounts(int fileUID) {
        List<String> wcs = workerService.getWords(fileUID);
        FileInfoDTO info = fileMapper.getFileInfoById(fileUID);
        return new WordCountListVO(info,wcs,"=");

    }
}
