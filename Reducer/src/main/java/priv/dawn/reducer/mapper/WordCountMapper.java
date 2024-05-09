package priv.dawn.reducer.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface WordCountMapper {

    @Insert("INSERT INTO t_file_word_count (file_uid, word, cnt) VALUE (#{uid},#{w},#{c}) ON DUPLICATE KEY UPDATE cnt=cnt+#{c};")
    void saveWordCount(@Param("uid") int fileUID, @Param("w") String word, @Param("c") int count);

}
