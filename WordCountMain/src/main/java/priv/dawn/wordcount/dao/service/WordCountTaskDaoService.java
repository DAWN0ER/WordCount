package priv.dawn.wordcount.dao.service;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import priv.dawn.wordcount.dao.domain.WordCountTask;
import priv.dawn.wordcount.dao.domain.WordCountTaskExample;
import priv.dawn.wordcount.dao.mapper.primary.WordCountTaskMapper;
import priv.dawn.wordcount.pojo.dto.WordCountTaskDto;
import priv.dawn.wordcount.pojo.enums.WordCountTaskStatusEnums;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: Dawn Yang
 * @Since: 2024/10/27/16:59
 */

@Slf4j
@Service
public class WordCountTaskDaoService {

    private final Gson gson = new Gson();

    @Resource
    private WordCountTaskMapper wordCountTaskMapper;

    public List<WordCountTaskDto> queryTasksOfFile(int fileUid) {
        if (fileUid <= 0) {
            log.error("[queryTasksOfFile] fileUid校验失败:{}", fileUid);
            return null;
        }
        WordCountTaskExample example = new WordCountTaskExample();
        example.createCriteria().andFileUidEqualTo(fileUid);
        try {
            List<WordCountTask> wordCountTasks = wordCountTaskMapper.selectByExample(example);
            return wordCountTasks.stream().map(this::record2dto).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("[queryTasksOfFile] 查询异常:", e);
            return null;
        }
    }

    public WordCountTaskDto getByTaskId(long taskId) {
        if (taskId <= 0) {
            log.error("[getByTaskId] taskId校验失败:taskId={}", taskId);
            return null;
        }
        WordCountTaskExample example = new WordCountTaskExample();
        example.createCriteria().andTaskIdEqualTo(taskId);
        List<WordCountTask> taskList = null;
        try {
            taskList = wordCountTaskMapper.selectByExample(example);
        } catch (Exception e) {
            log.error("[getByTaskId] 查询异常:", e);
            return null;
        }
        if (CollectionUtils.isEmpty(taskList)) {
            log.error("[getByTaskId] 查询结果为空: taskId:{}", taskId);
            return null;
        }
        return record2dto(taskList.get(0));

    }

    public boolean saveTaskRecord(WordCountTaskDto recordDto) {
        if (!judgeValid(recordDto)) {
            log.error("[saveTaskRecord]基础字段校验失败:{}", gson.toJson(recordDto));
            return false;
        }
        WordCountTask record = new WordCountTask();
        record.setTaskId(recordDto.getTaskId());
        record.setFileUid(recordDto.getFileUid());
        record.setStatus(recordDto.getStatus());
        try {
            wordCountTaskMapper.insertSelective(record);
        } catch (Exception e) {
            log.error("[saveTaskRecord]储存新任务异常:{}", gson.toJson(recordDto), e);
            return false;
        }
        log.info("[saveTaskRecord]存入新任务:{}", gson.toJson(recordDto));
        return true;
    }

    public boolean updateTaskStatus(long taskId, WordCountTaskStatusEnums status) {
        if (taskId <= 0) {
            log.error("[updateTaskStatus] taskId校验失败:taskId={}", taskId);
            return false;
        }
        WordCountTaskExample example = new WordCountTaskExample();
        example.createCriteria().andTaskIdEqualTo(taskId);
        WordCountTask record = new WordCountTask();
        record.setStatus(status.getStatus());
        int update = -1;
        try {
            update = wordCountTaskMapper.updateByExampleSelective(record, example);
        } catch (Exception e) {
            log.error("[updateTaskStatus] 异常:", e);
            return false;
        }
        if (update <= 0) {
            log.warn("[updateTaskStatus] 失败, 未找到相关记录, taskId:{}", taskId);
            return false;
        }
        log.info("[updateTaskStatus] 成功, taskId:{}, status:{}", taskId, status.getStatus());
        return true;
    }

    private boolean judgeValid(WordCountTaskDto recordDto) {
        if (Objects.isNull(recordDto)) {
            return false;
        }
        // 校验 TaskId
        if (Objects.isNull(recordDto.getTaskId()) || recordDto.getTaskId() <= 0) {
            return false;
        }
        // 校验 FileUid
        if (Objects.isNull(recordDto.getFileUid()) || recordDto.getFileUid() <= 0) {
            return false;
        }
        // Status
        if (Objects.isNull(recordDto.getStatus())) {
            return false;
        }
        return true;
    }

    private WordCountTaskDto record2dto(WordCountTask record) {
        WordCountTaskDto dto = new WordCountTaskDto();
        dto.setTaskId(record.getTaskId());
        dto.setFileUid(record.getFileUid());
        dto.setStatus(record.getStatus());
        return dto;
    }
}
