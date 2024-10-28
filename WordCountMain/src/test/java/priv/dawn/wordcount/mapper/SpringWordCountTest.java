package priv.dawn.wordcount.mapper;

import org.junit.jupiter.api.Test;
import priv.dawn.wordcount.WordCountMainApplicationTests;
import priv.dawn.wordcount.dao.domain.WordCount;
import priv.dawn.wordcount.dao.mapper.primary.WordCountMapper;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: Dawn Yang
 * @Since: 2024/10/20/16:10
 */
public class SpringWordCountTest extends WordCountMainApplicationTests {

    @Resource
    private WordCountMapper wordCountMapper;

    @Test
    public void updateTest(){
        WordCount wordCount = new WordCount();
        wordCount.setFileUid(123);
        wordCount.setWord("hello");
        wordCount.setCnt(17);
        int update = wordCountMapper.updateAppendCount(wordCount);
        System.out.println("update = " + update);
    }

}
