package priv.dawn.workers.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

//@Mapper
@Deprecated
public interface ProgressMapper {

    @Insert("INSERT INTO t_file_progress (file_uid, chunks_num) VALUES (#{uid},#{num});")
    void saveNewProgress(@Param("uid") int fileUID, @Param("num") int chunkNum);

    @Insert("UPDATE t_file_progress SET finished=finished+1 WHERE file_uid = #{udi};")
    void progressAdvanceOne(@Param("uid") int fileUID);

    @Select("SELECT TRUNCATE(finished / chunks_num *100,2) FROM t_file_progress WHERE file_uid=#{uid};")
    Float getProgress(@Param("uid") int fileUID); // 用包装类防止返回null

}
