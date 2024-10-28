package priv.dawn.wordcount.dao.mapper.primary;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import priv.dawn.wordcount.dao.domain.WordCountTask;
import priv.dawn.wordcount.dao.domain.WordCountTaskExample;

public interface WordCountTaskMapper {
    int deleteByExample(WordCountTaskExample example);

    int insert(WordCountTask record);

    int insertSelective(WordCountTask record);

    List<WordCountTask> selectByExample(WordCountTaskExample example);

    int updateByExampleSelective(@Param("record") WordCountTask record, @Param("example") WordCountTaskExample example);

    int updateByExample(@Param("record") WordCountTask record, @Param("example") WordCountTaskExample example);
}