package priv.dawn.wordcount.dao.mapper.primary;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import priv.dawn.wordcount.dao.domain.WordCount;
import priv.dawn.wordcount.dao.domain.WordCountExample;

public interface WordCountMapper {
    int deleteByExample(WordCountExample example);

    int insert(WordCount record);

    int insertSelective(WordCount record);

    List<WordCount> selectByExample(WordCountExample example);

    int updateByExampleSelective(@Param("record") WordCount record, @Param("example") WordCountExample example);

    int updateByExample(@Param("record") WordCount record, @Param("example") WordCountExample example);
}