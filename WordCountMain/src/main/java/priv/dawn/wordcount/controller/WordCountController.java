package priv.dawn.wordcount.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import priv.dawn.wordcount.dao.service.FileStoreDaoService;
import priv.dawn.wordcount.pojo.dto.DaoFileInfoDto;
import priv.dawn.wordcount.pojo.vo.FileWordCountTaskVo;
import priv.dawn.wordcount.pojo.vo.FileWordCountVo;
import priv.dawn.wordcount.service.WordCountClientService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/v2/api/word")
public class WordCountController {

    @Resource
    private WordCountClientService wordCountClientService;

    @Resource
    private FileStoreDaoService fileStoreDaoService;

    @GetMapping("/count")
    public FileWordCountTaskVo startCountWord(int id) {
        DaoFileInfoDto fileInfo = fileStoreDaoService.getFileInfo(id);
        String fileName = Optional.ofNullable(fileInfo).map(DaoFileInfoDto::getFileName).orElse("");
        long taskId = wordCountClientService.startWordCountOfFile(id);
        if (taskId <= 0) {
            log.error("task 启动失败, fileUid:{}", id);
            return null;
        }
        double progress = 0.0;
        int max = 100;
        while (progress < 0.99 && progress >= 0.0 && max > 0) {
            progress = wordCountClientService.getProgress(taskId);
            log.info("task:{} 进度:{}", taskId, progress);
            max--;
        }
        FileWordCountTaskVo vo = new FileWordCountTaskVo();
        vo.setFileUid(id);
        vo.setFileName(fileName);
        vo.setTaskId(taskId);
        vo.setStatus(100);
        return vo;
    }

    @GetMapping("/topK")
    public FileWordCountVo getTopKWordCount(int id) {
        return null;
    }

    @PostMapping("/get")
    public FileWordCountVo getCountOfWords(List<String> words) {
        return null;
    }

}
