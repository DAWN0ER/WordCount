package priv.dawn.wordcount.dao.service;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;
import priv.dawn.wordcount.dao.domain.FileChunks;
import priv.dawn.wordcount.dao.domain.FileChunksExample;
import priv.dawn.wordcount.dao.domain.FileInfo;
import priv.dawn.wordcount.dao.domain.FileInfoExample;
import priv.dawn.wordcount.dao.mapper.primary.FileChunksMapper;
import priv.dawn.wordcount.dao.mapper.primary.FileInfoMapper;
import priv.dawn.wordcount.pojo.dto.DaoFileChunkDto;
import priv.dawn.wordcount.pojo.dto.DaoFileInfoDto;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: Dawn Yang
 * @Since: 2024/10/15/14:25
 */

@Repository
@Slf4j
public class FileStoreDaoService {

    private final Gson gson = new Gson();

    @Resource
    private FileInfoMapper fileInfoMapper;

    @Resource
    private FileChunksMapper fileChunksMapper;

    public List<Integer> saveFileChunks(List<DaoFileChunkDto> fileChunkDtoList, int fileUid) {
        if (fileUid <= 0) {
            return Collections.emptyList();
        }
        List<Integer> success = new ArrayList<>();
        for (DaoFileChunkDto dto : fileChunkDtoList) {
            FileChunks record = new FileChunks();
            record.setFileUid(fileUid);
            record.setChunkId(dto.getChunkId());
            record.setContext(dto.getContext());

            int insert = -1;
            try {
                insert = fileChunksMapper.insertSelective(record);
            } catch (Exception e) {
                log.error("[saveFileChunks] 异常: record:{}", gson.toJson(record), e);
            }
            if (insert > 0) {
                success.add(dto.getChunkId());
            }
        }
        return success;
    }

    public int saveFileInfo(DaoFileInfoDto fileInfoDto) {
        int success = 0;
        FileInfo record = new FileInfo();
        record.setFileUid(fileInfoDto.getFileUid());
        record.setFileName(fileInfoDto.getFileName());
        record.setChunkNum(fileInfoDto.getChunkNum());
        if (Objects.nonNull(fileInfoDto.getStatus())) {
            record.setStatus(fileInfoDto.getStatus().byteValue());
        }
        try {
            success = fileInfoMapper.insert(record);
        } catch (Exception e) {
            log.error("[saveFileInfo] 异常: record;{}", gson.toJson(record), e);
        }
        return success;
    }

    public List<DaoFileChunkDto> getFileChunks(int fileUid, List<Integer> chunkIdList) {
        if (CollectionUtils.isEmpty(chunkIdList) || fileUid <= 0) {
            return Collections.emptyList();
        }
        FileChunksExample example = new FileChunksExample();
        example.createCriteria().andFileUidEqualTo(fileUid).andChunkIdIn(chunkIdList);
        List<FileChunks> select;
        try {
            select = fileChunksMapper.selectByExampleWithBLOBs(example);
        } catch (Exception e) {
            log.error("[getFileChunks] 异常: fileUid:{},chunkIdList:{}", fileUid, chunkIdList, e);
            return Collections.emptyList();
        }
        List<DaoFileChunkDto> result = castToDto(select);
        log.info("[getFileChunks]: fileUid:{},chunks:{}, result:{}", fileUid, chunkIdList, gson.toJson(result));
        return result;
    }

    public DaoFileInfoDto getFileInfo(int fileUid) {
        FileInfoExample example = new FileInfoExample();
        example.createCriteria().andFileUidEqualTo(fileUid);
        List<FileInfo> list = null;
        try {
            list = fileInfoMapper.selectByExample(example);
        } catch (Exception e) {
            log.error("[getFileInfo] 异常: fileUid;{}", fileUid, e);
        }
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        FileInfo fileInfo = list.get(0);
        DaoFileInfoDto result = new DaoFileInfoDto();
        result.setFileName(fileInfo.getFileName());
        result.setChunkNum(fileInfo.getChunkNum());
        result.setFileUid(fileInfo.getFileUid());
        result.setStatus(fileInfo.getStatus().intValue());
        return result;
    }

    public int updateFileInfoSelective(DaoFileInfoDto fileInfoDto) {
        if (Objects.isNull(fileInfoDto.getFileUid()) || fileInfoDto.getFileUid() <= 0) {
            log.warn("[updateFileInfo] illegal param:{}", gson.toJson(fileInfoDto));
            return 0;
        }
        FileInfoExample example = new FileInfoExample();
        example.createCriteria().andFileUidEqualTo(fileInfoDto.getFileUid());

        FileInfo record = new FileInfo();
        record.setFileUid(fileInfoDto.getFileUid());
        record.setFileName(fileInfoDto.getFileName());
        record.setChunkNum(fileInfoDto.getChunkNum());
        record.setStatus(fileInfoDto.getStatus().byteValue());

        try {
            return fileInfoMapper.updateByExampleSelective(record, example);
        }catch (Exception e){
            log.error("[updateFileInfo] 异常: record:{}",gson.toJson(record));
            return 0;
        }
    }

    private List<DaoFileChunkDto> castToDto(List<FileChunks> fileChunks) {
        if (CollectionUtils.isEmpty(fileChunks)) {
            return Collections.emptyList();
        }
        return fileChunks.stream().map(element -> {
            DaoFileChunkDto dto = new DaoFileChunkDto();
            dto.setChunkId(element.getChunkId());
            dto.setContext(element.getContext());
            return dto;
        }).collect(Collectors.toList());
    }
}
