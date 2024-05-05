package priv.dawn.workers.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface WordCountMapper {

    @Insert("INSERT INTO t_file_progress (file_uid, chunks_num) VALUES (#{uid},#{num});")
    void saveNewProgress(@Param("uid") int fileUID, @Param("num") int chunkNum);

    //如果 chunk_num=0 成功更新的话, 说明记录有问题啊
    @Insert("INSERT INTO t_file_progress (file_uid,chunks_num) VALUES (#{uid},0) ON DUPLICATE KEY UPDATE finished=finished+1;")
    void progressAdvanceOne(@Param("uid") int fileUID);

    @Select("SELECT TRUNCATE(finished / chunks_num *100,2) FROM t_file_progress WHERE file_uid=#{uid};")
    float getProgress(@Param("uid") int fileUID);

}
