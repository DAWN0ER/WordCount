package priv.dawn.databaseproxy.rpc;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboService;
import priv.dawn.wordcount.api.FileStoreService;
import priv.dawn.databaseproxy.dao.service.FileStoreDaoService;
import priv.dawn.wordcount.domain.ChunkDto;
import priv.dawn.wordcount.domain.FileChunksDto;
import priv.dawn.databaseproxy.dao.dto.DaoFileChunkDto;
import priv.dawn.databaseproxy.dao.dto.DaoFileInfoDto;
import priv.dawn.databaseproxy.dao.enums.FileInfoStatusEnums;

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
 * @Since: 2024/10/12/22:02
 */

@Slf4j
@DubboService
public class FileStoreServiceImpl implements FileStoreService {

    @Resource
    FileStoreDaoService fileStoreDaoService;

    @Override
    public FileChunksDto getFile(Integer fileUid) {
        if (Objects.isNull(fileUid) || fileUid <= 0) return null;

        DaoFileInfoDto fileInfo = fileStoreDaoService.getFileInfo(fileUid);
        if (Objects.isNull(fileInfo) || !FileInfoStatusEnums.STORED.getStatus().equals(fileInfo.getStatus())) {
            return null;
        }
        Integer chunkNum = fileInfo.getChunkNum();
        List<Integer> chunks = new ArrayList<>(chunkNum);
        for (int chunkId = 1; chunkId <= chunkNum; chunkId++) {
            chunks.add(chunkId);
        }
        List<DaoFileChunkDto> daoFileChunks = fileStoreDaoService.getFileChunks(fileUid, chunks);
        if (daoFileChunks.size() != chunkNum) {
            fileInfo.setStatus(FileInfoStatusEnums.DAMAGED.getStatus());
            fileStoreDaoService.updateFileInfoSelective(fileInfo);
            log.warn("[getFile] 文件损坏, fileUid:{}, chunkNum:{},selected chunks:{}",
                    fileUid, chunkNum, daoFileChunks.size());
            return null;
        }

        FileChunksDto fileChunksDto = new FileChunksDto();
        List<ChunkDto> chunkDtoList = daoFileChunks.stream().map(e -> {
            ChunkDto chunkDto = new ChunkDto();
            chunkDto.setChunkId(e.getChunkId());
            chunkDto.setContext(e.getContext());
            return chunkDto;
        }).collect(Collectors.toList());
        fileChunksDto.setChunks(chunkDtoList);
        fileChunksDto.setFileUid(fileUid);
        fileChunksDto.setFilename(fileInfo.getFileName());
        fileChunksDto.setChunkNum(chunkNum);
        return fileChunksDto;
    }

    @Override
    public FileChunksDto getPagesByFile(Integer fileUid, int startChunk, int endChunk) {
        if (Objects.isNull(fileUid) || fileUid <= 0) {
            log.error("[getPagesByFile] 错误参数: fileUid:{}",fileUid);
            return null;
        }
        if (startChunk < 1 || endChunk < 1 || endChunk < startChunk) {
            log.error("[getPagesByFile] 错误参数: startChunk:{}, endChunk:{}",startChunk,endChunk);
            return null;
        }

        int chunkNum = endChunk - startChunk + 1;

        ArrayList<Integer> list = new ArrayList<>(chunkNum);
        for (int chunkId = startChunk; chunkId <= endChunk; chunkId++) {
            list.add(chunkId);
        }
        List<DaoFileChunkDto> daoFileChunkDtoList = fileStoreDaoService.getFileChunks(fileUid, list);

        List<ChunkDto> chunkDtoList = Collections.emptyList();
        if (!CollectionUtils.isEmpty(daoFileChunkDtoList)) {
            chunkDtoList = daoFileChunkDtoList.stream().map(e -> {
                ChunkDto chunkDto = new ChunkDto();
                chunkDto.setChunkId(e.getChunkId());
                chunkDto.setContext(e.getContext());
                return chunkDto;
            }).collect(Collectors.toList());
        }
        FileChunksDto fileChunksDto = new FileChunksDto();
        fileChunksDto.setChunks(chunkDtoList);
        fileChunksDto.setFileUid(fileUid);
        fileChunksDto.setChunkNum(chunkDtoList.size());
        return fileChunksDto;

    }

    @Override
    public FileChunksDto getFileInfo(Integer fileUid) {
        if (Objects.isNull(fileUid) || fileUid <= 0) {
            return null;
        }
        DaoFileInfoDto fileInfo = fileStoreDaoService.getFileInfo(fileUid);
        if (Objects.isNull(fileInfo)) {
            return null;
        }
        FileChunksDto fileChunksDto = new FileChunksDto();
        fileChunksDto.setChunkNum(fileInfo.getChunkNum());
        fileChunksDto.setFileUid(fileUid);
        fileChunksDto.setFilename(fileInfo.getFileName());
        return fileChunksDto;
    }

}
