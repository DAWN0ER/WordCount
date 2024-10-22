package priv.dawn.databaseproxy.dao.mapper.primary;

import org.apache.ibatis.annotations.Param;
import priv.dawn.databaseproxy.dao.domain.FileInfo;
import priv.dawn.databaseproxy.dao.domain.FileInfoExample;

import java.util.List;

public interface FileInfoMapper {
    int deleteByExample(FileInfoExample example);

    int insert(FileInfo record);

    int insertSelective(FileInfo record);

    List<FileInfo> selectByExample(FileInfoExample example);

    int updateByExampleSelective(@Param("record") FileInfo record, @Param("example") FileInfoExample example);

    int updateByExample(@Param("record") FileInfo record, @Param("example") FileInfoExample example);
}