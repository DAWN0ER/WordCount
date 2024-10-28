package priv.dawn.wordcount.controller;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import priv.dawn.wordcount.dao.service.FileStoreDaoService;
import priv.dawn.wordcount.dao.service.WordCountDaoService;
import priv.dawn.wordcount.dao.service.WordCountTaskDaoService;
import priv.dawn.wordcount.pojo.dto.DaoFileInfoDto;
import priv.dawn.wordcount.pojo.dto.DaoWordCountDto;
import priv.dawn.wordcount.pojo.dto.DaoWordCountTaskDto;
import priv.dawn.wordcount.pojo.enums.FileInfoStatusEnums;
import priv.dawn.wordcount.pojo.enums.WordCountTaskStatusEnums;
import priv.dawn.wordcount.pojo.vo.FileWordCountTaskVo;
import priv.dawn.wordcount.pojo.vo.FileWordCountVo;
import priv.dawn.wordcount.pojo.vo.WordCountVo;
import priv.dawn.wordcount.service.WordCountClientService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    private final Gson gson = new Gson();

    @PostMapping("/count")
    public FileWordCountTaskVo startCountWord(@RequestBody int fileUid, HttpServletResponse response) throws IOException {
        DaoFileInfoDto fileInfo = fileStoreDaoService.getFileInfo(fileUid);
        if (Objects.isNull(fileInfo) || !FileInfoStatusEnums.STORED.getStatus().equals(fileInfo.getStatus())) {
            log.warn("[startCountWord] 文件未找到或已经损坏, fileInfo:{}", gson.toJson(fileInfo));
            response.sendError(404, "文件不存在或不完整");
        }
        List<DaoWordCountTaskDto> taskListOfFile = wordCountTaskDaoService.getByFileUid(fileUid);
        if (CollectionUtils.isNotEmpty(taskListOfFile)) {
            log.info("");
            response.sendError(405, "文件已有计数任务");
        }
        long taskId = wordCountClientService.startWordCountOfFile(fileUid);
        if (taskId <= 0) {
            log.error("task 启动失败, fileUid:{}", fileUid);
            response.sendError(500, "任务启动失败");
        }
        FileWordCountTaskVo vo = new FileWordCountTaskVo();
        vo.setFileUid(fileUid);
        vo.setFileName(fileInfo.getFileName());
        vo.setTaskId(taskId);
        vo.setStatus(WordCountTaskStatusEnums.NEW.getStatus());
        return vo;
    }

    @GetMapping("/file/{fileUid}/top/{K}")
    public FileWordCountVo getTopKWordCount(
            @PathVariable int fileUid,
            @PathVariable int K,
            HttpServletResponse response
    ) throws IOException {
        if (fileUid <= 0 || K <= 0 || K > 100) {
            response.sendError(400, "非法参数");
        }
        if (!judgeTaskIsValidOfFile(fileUid)) {
            response.sendError(404, "文件不存在或不完整，或无计数任务结果");
        }
        List<DaoWordCountDto> dtoList = wordCountDaoService.queryTopKWords(fileUid, K);
        if (CollectionUtils.isEmpty(dtoList)) {
            log.error("[getTopKWordCount] 未找到计数结果, fileUid:{}", fileUid);
            response.sendError(500, "文件计数出现异常");
        }
        List<WordCountVo> wordCountVoList = dtoList.stream()
                .map(this::dto2vo)
                .collect(Collectors.toList());
        FileWordCountVo fileWordCountVo = new FileWordCountVo();
        fileWordCountVo.setWordCounts(wordCountVoList);
        fileWordCountVo.setFileUid(fileUid);
        return fileWordCountVo;
    }

    @PostMapping("/file/{fileUid}/words")
    public FileWordCountVo getCountOfWords(
            @PathVariable int fileUid,
            @RequestBody List<String> words,
            HttpServletResponse response
    ) throws IOException {
        if (fileUid <= 0 || CollectionUtils.isEmpty(words)) {
            response.sendError(400, "非法参数");
        }
        if (!judgeTaskIsValidOfFile(fileUid)) {
            response.sendError(404, "文件不存在或不完整，或无计数任务结果");
        }
        List<DaoWordCountDto> dtoList = wordCountDaoService.queryCountByWords(fileUid, words);
        List<WordCountVo> wordCountVoList = null;
        if (CollectionUtils.isEmpty(dtoList)) {
            log.error("[getCountOfWords] 未找到计数结果, fileUid:{}, words:{}", fileUid,words);
        } else {
            wordCountVoList = dtoList.stream().map(this::dto2vo).collect(Collectors.toList());
        }
        FileWordCountVo fileWordCountVo = new FileWordCountVo();
        fileWordCountVo.setWordCounts(wordCountVoList);
        fileWordCountVo.setFileUid(fileUid);
        return fileWordCountVo;
    }

    @GetMapping("/progress/{taskId}")
    public FileWordCountTaskVo getTaskProgress(@PathVariable long taskId, HttpServletResponse response) throws IOException {
        DaoWordCountTaskDto taskDto = wordCountTaskDaoService.getByTaskId(taskId);
        if (Objects.isNull(taskDto)) {
            response.sendError(404, "Task Not Found");
        }

        if (WordCountTaskStatusEnums.EXCEPTION.getStatus().equals(taskDto.getStatus())) {
            response.sendError(500, "Task Exception of " + taskId);
        }
        if (WordCountTaskStatusEnums.FINISHED.getStatus().equals(taskDto.getStatus())) {
            FileWordCountTaskVo vo = new FileWordCountTaskVo();
            vo.setTaskId(taskDto.getTaskId());
            vo.setFileUid(taskDto.getFileUid());
            vo.setProgress(100.0);
            return vo;
        }

        // 如果是 NEW 的task
        double progress = wordCountClientService.getProgress(taskId);
        if (progress < 0) {
            wordCountTaskDaoService.updateTaskStatus(taskId, WordCountTaskStatusEnums.EXCEPTION);
            response.sendError(500, "Task Exception of " + taskId);
        }
        if (progress > 99.9999) {
            wordCountTaskDaoService.updateTaskStatus(taskId, WordCountTaskStatusEnums.FINISHED);
        }
        log.info("task:{} 正在进行中进度:{}", taskId, progress);

        FileWordCountTaskVo vo = new FileWordCountTaskVo();
        vo.setTaskId(taskDto.getTaskId());
        vo.setFileUid(taskDto.getFileUid());
        vo.setProgress(progress);
        return vo;
    }

    private boolean judgeTaskIsValidOfFile(int fileUid) {
        DaoFileInfoDto fileInfo = fileStoreDaoService.getFileInfo(fileUid);
        if (Objects.isNull(fileInfo) || !FileInfoStatusEnums.STORED.getStatus().equals(fileInfo.getStatus())) {
            log.warn("[judgeTaskIsValidOfFile] 文件未找到或已经损坏, fileInfo:{}", gson.toJson(fileInfo));
            return false;
        }
        List<DaoWordCountTaskDto> taskDtoList = wordCountTaskDaoService.getByFileUid(fileUid, WordCountTaskStatusEnums.FINISHED);
        if (CollectionUtils.isEmpty(taskDtoList)) {
            log.warn("[judgeTaskIsValidOfFile] 计数任务未完成, fileUid:{}", fileUid);
            return false;
        }
        return true;
    }

    private WordCountVo dto2vo(DaoWordCountDto dto){
        WordCountVo vo = new WordCountVo();
        vo.setWord(dto.getWord());
        vo.setCount(dto.getCount());
        return vo;
    }

}
