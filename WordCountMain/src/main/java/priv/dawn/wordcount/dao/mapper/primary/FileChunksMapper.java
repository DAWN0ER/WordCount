package priv.dawn.wordcount.dao.mapper.primary;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import priv.dawn.wordcount.dao.domain.FileChunks;
import priv.dawn.wordcount.dao.domain.FileChunksExample;

public interface FileChunksMapper {
    int deleteByExample(FileChunksExample example);

    int insert(FileChunks record);

    int insertSelective(FileChunks record);

    List<FileChunks> selectByExampleWithBLOBs(FileChunksExample example);

    List<FileChunks> selectByExample(FileChunksExample example);

    int updateByExampleSelective(@Param("record") FileChunks record, @Param("example") FileChunksExample example);

    int updateByExampleWithBLOBs(@Param("record") FileChunks record, @Param("example") FileChunksExample example);

    int updateByExample(@Param("record") FileChunks record, @Param("example") FileChunksExample example);
}