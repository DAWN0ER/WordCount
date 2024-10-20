package priv.dawn.workers.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

//@Mapper
@Deprecated
public interface WordCountMapper {

    @Select("SELECT CONCAT(word,'=',cnt) as count FROM t_file_word_count WHERE file_uid=#{uid} ORDER BY cnt DESC LIMIT 100")
    List<String> getTop100WordCount(@Param("uid") int fileUID);

    @Select("SELECT word FROM t_file_word_count WHERE file_uid=#{uid} ORDER BY cnt DESC LIMIT 100")
    List<String> getTop100Words(@Param("uid") int fileUID);

}
