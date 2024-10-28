package priv.dawn.databaseproxy.dao.mapper.primary;

import org.apache.ibatis.annotations.Param;
import priv.dawn.databaseproxy.dao.domain.FileChunks;
import priv.dawn.databaseproxy.dao.domain.FileChunksExample;

import java.util.List;

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