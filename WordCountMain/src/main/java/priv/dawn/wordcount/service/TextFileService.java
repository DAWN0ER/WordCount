package priv.dawn.wordcount.service;

import com.hankcs.hanlp.seg.common.Term;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import priv.dawn.wordcount.dao.service.FileStoreDaoService;
import priv.dawn.wordcount.pojo.dto.DaoFileChunkDto;
import priv.dawn.wordcount.pojo.dto.DaoFileInfoDto;
import priv.dawn.wordcount.pojo.enums.FileInfoStatusEnums;
import priv.dawn.wordcount.pojo.vo.TextFileVo;
import priv.dawn.wordcount.utils.CustomTokenizer;
import priv.dawn.wordcount.utils.UidGenerator;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description: 文本文件处理相关
 * @Auther: Dawn Yang
 * @Since: 2024/10/16/12:59
 */

@Service
public class TextFileService {

    @Resource
    private FileStoreDaoService fileStoreDaoService;

    /**
     * 上传文件没有采用事务，而是使用 GFS 类似的思想，允许遗弃坏区和重试
     * 文件上传分为三个阶段：INIT，载入Chunks，STORED
     *
     * @param textFileVo 输入实体
     * @return fileUid
     */
    public int updateFile(TextFileVo textFileVo) {

        int fileUid = UidGenerator.getHashUid(textFileVo.getFilename());
        DaoFileInfoDto fileInfoDto = new DaoFileInfoDto();
        fileInfoDto.setFileUid(fileUid);
        fileInfoDto.setFileName(textFileVo.getFilename());
        fileInfoDto.setStatus(FileInfoStatusEnums.INIT.getStatus());

        List<DaoFileChunkDto> daoFileChunkDtoList = splitToChunks(textFileVo.getContext());
        fileInfoDto.setChunkNum(daoFileChunkDtoList.size());
        // 初始化 fileInfo
        int result = fileStoreDaoService.saveFileInfo(fileInfoDto);
        if (result == 0) {
            return -1;
        }
        fileInfoDto.setStatus(FileInfoStatusEnums.STORED.getStatus());
        // 分块存储
        List<Integer> chunks = fileStoreDaoService.saveFileChunks(daoFileChunkDtoList, fileUid);
        if (chunks.size() < daoFileChunkDtoList.size()) {
            fileInfoDto.setStatus(FileInfoStatusEnums.DAMAGED.getStatus());
        }
        // 更新 fileInfo 状态
        result = fileStoreDaoService.updateFileInfoSelective(fileInfoDto);
        if (result == 0) {
            return -1;
        }
        return fileUid;
    }

    public TextFileVo getFile(int fileUid) {
        if (fileUid <= 0) return null;
        DaoFileInfoDto fileInfo = fileStoreDaoService.getFileInfo(fileUid);
        if (Objects.isNull(fileInfo)) {
            return null;
        }
        Integer chunkNum = fileInfo.getChunkNum();
        StringBuilder contextBuilder = new StringBuilder();
        for (int chunkId = 1; chunkId <= chunkNum; chunkId++) {
            List<DaoFileChunkDto> chunks = fileStoreDaoService.getFileChunks(fileUid, Collections.singletonList(chunkId));
            if(CollectionUtils.isEmpty(chunks)){
                fileInfo.setStatus(FileInfoStatusEnums.DAMAGED.getStatus());
                fileStoreDaoService.saveFileInfo(fileInfo);
                return null;
            }
            contextBuilder.append(chunks.get(0).getContext());
        }
        TextFileVo textFileVo = new TextFileVo();
        textFileVo.setFilename(fileInfo.getFileName());
        textFileVo.setContext(contextBuilder.toString());
        return textFileVo;
    }

    private List<DaoFileChunkDto> splitToChunks(String context) {

        ArrayList<DaoFileChunkDto> list = new ArrayList<>();
        int begin = 0;
        int len = 1000;
        int id = 1;
        while (begin < context.length() - len) {
            int end = getSplitEnd(context, begin + len);
            String chunk = context.substring(begin, end);
            begin = end;

            DaoFileChunkDto chunkDto = new DaoFileChunkDto();
            chunkDto.setChunkId(id++);
            chunkDto.setContext(chunk);
            list.add(chunkDto);
        }
        String chunk = context.substring(begin);

        DaoFileChunkDto chunkDto = new DaoFileChunkDto();
        chunkDto.setChunkId(id);
        chunkDto.setContext(chunk);
        list.add(chunkDto);
        return list;
    }

    private int getSplitEnd(String context, int fromIndex) {
        int theBound = Math.min(context.length(), fromIndex + 20); // 默认相信 20 个字内一定有两个分词
        List<Term> segment = CustomTokenizer.segment(context.substring(fromIndex, theBound));
        return segment.get(1).offset + fromIndex;
    }

}
