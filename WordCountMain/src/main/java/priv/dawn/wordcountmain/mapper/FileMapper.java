package priv.dawn.wordcountmain.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import priv.dawn.wordcountmain.pojo.dto.FileInfoDTO;

import java.util.List;

@Mapper
public interface FileMapper {

    @Insert("insert into t_file_chunks (file_uid,chunk_id,context) values(#{uid},#{chunkId},#{context});")
    void saveFileChunk(@Param("uid") int uid, @Param("chunkId") int chunkId, @Param("context") String context);

    @Insert("insert into file_info (file_name,file_uid,chunk_num) values(#{filename},#{uid},#{chunkNum});")
    void saveFileInfo(FileInfoDTO fileInfo);

    @Select("select context from t_file_chunks where file_uid=#{uid};")
    List<String> getALLContextByUid(@Param("uid") int uid);

    @Select("select file_name,file_uid as uid,chunk_num from file_info where file_uid=#{uid}")
    FileInfoDTO getFileInfoById(@Param("uid") int uid);
}
