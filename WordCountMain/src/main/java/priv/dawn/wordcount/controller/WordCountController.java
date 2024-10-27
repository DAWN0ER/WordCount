package priv.dawn.wordcount.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import priv.dawn.wordcount.dao.service.FileStoreDaoService;
import priv.dawn.wordcount.dao.service.WordCountDaoService;
import priv.dawn.wordcount.dao.service.WordCountTaskDaoService;
import priv.dawn.wordcount.pojo.dto.DaoFileInfoDto;
import priv.dawn.wordcount.pojo.dto.DaoWordCountDto;
import priv.dawn.wordcount.pojo.dto.WordCountTaskDto;
import priv.dawn.wordcount.pojo.enums.WordCountTaskStatusEnums;
import priv.dawn.wordcount.pojo.vo.FileWordCountTaskVo;
import priv.dawn.wordcount.pojo.vo.FileWordCountVo;
import priv.dawn.wordcount.service.WordCountClientService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/v2/api/word-count")
public class WordCountController {

    @Resource
    private WordCountClientService wordCountClientService;

    @Resource
    private FileStoreDaoService fileStoreDaoService;

    @Resource
    private WordCountDaoService wordCountDaoService;

    @Resource
    private WordCountTaskDaoService wordCountTaskDaoService;

    @PostMapping("/count")
    public FileWordCountTaskVo startCountWord(@RequestBody int fileUid) {
        DaoFileInfoDto fileInfo = fileStoreDaoService.getFileInfo(fileUid);
        String fileName = Optional.ofNullable(fileInfo).map(DaoFileInfoDto::getFileName).orElse("");
        long taskId = wordCountClientService.startWordCountOfFile(fileUid);
        if (taskId <= 0) {
            log.error("task 启动失败, fileUid:{}", fileUid);
            return null;
        }
        FileWordCountTaskVo vo = new FileWordCountTaskVo();
        vo.setFileUid(fileUid);
        vo.setFileName(fileName);
        vo.setTaskId(taskId);
        vo.setStatus(WordCountTaskStatusEnums.NEW.getStatus());
        return vo;
    }

    @GetMapping("/file/{fileUid}/top/{K}")
    public FileWordCountVo getTopKWordCount(@PathVariable int fileUid, @PathVariable int K) {
        List<DaoWordCountDto> dtoList = wordCountDaoService.queryTopKWords(fileUid, K);
        // TODO
        return null;
    }

    @PostMapping("/file/{fileUid}/words")
    public FileWordCountVo getCountOfWords(@PathVariable int fileUid, @RequestBody List<String> words) {
        return null;
    }

    @GetMapping("/progress/{taskId}")
    public FileWordCountTaskVo getTaskProgress(@PathVariable long taskId, HttpServletResponse response) throws IOException {
        WordCountTaskDto taskDto = wordCountTaskDaoService.getByTaskId(taskId);
        FileWordCountTaskVo vo = null;
        if (Objects.nonNull(taskDto)) {
            vo = new FileWordCountTaskVo();
            vo.setStatus(WordCountTaskStatusEnums.FINISHED.getStatus());
            vo.setTaskId(taskDto.getTaskId());
            vo.setFileUid(taskDto.getFileUid());
        }
        if (Objects.isNull(vo)) {
            response.sendError(404, "Task Not Found");
        }
        if (WordCountTaskStatusEnums.EXCEPTION.getStatus().equals(vo.getStatus())) {
            response.sendError(500, "Task Exception of " + taskId);
        }
        if (WordCountTaskStatusEnums.FINISHED.getStatus().equals(vo.getStatus())) {
            vo.setProgress(100.0);
            return vo;
        }

        double progress = wordCountClientService.getProgress(taskId);
        if (progress < 0) {
            wordCountTaskDaoService.updateTaskStatus(taskId, WordCountTaskStatusEnums.EXCEPTION);
            response.sendError(500, "Task Exception of " + taskId);
        }

        log.info("task:{} 正在进行中进度:{}", taskId, progress);
        vo.setProgress(progress);

        return vo;
    }

}
