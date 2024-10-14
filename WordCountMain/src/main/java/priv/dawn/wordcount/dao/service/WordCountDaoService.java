package priv.dawn.wordcount.dao.service;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import priv.dawn.wordcount.dao.domain.WordCount;
import priv.dawn.wordcount.dao.domain.WordCountExample;
import priv.dawn.wordcount.dao.mapper.primary.WordCountMapper;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: Dawn Yang
 * @Since: 2024/10/14/16:51
 */

@Repository
@Slf4j
public class WordCountDaoService {

    // TODO 还没写完

    @Resource
    WordCountMapper wordCountMapper;

    public List<WordCount> queryCountByWords(int fileUid, List<String> words) {
        WordCountExample example = new WordCountExample();
        example.createCriteria()
                .andFileUidEqualTo(fileUid)
                .andWordIn(words);
        List<WordCount> wordCounts;
        try {
            wordCounts = wordCountMapper.selectByExample(example);
        } catch (Exception e) {
            // TODO 异常日志文案
            return Collections.emptyList();
        }
        return wordCounts;
    }

    public List<WordCount> queryTopKWords(int fileUid, int k) {

        WordCountExample example = new WordCountExample();
        example.createCriteria().andFileUidEqualTo(fileUid);
        example.setOrderByClause("cnt DESC LIMIT " + k); // 这个字符串是加在末尾的，所以可以用来写一些奇奇怪怪的

        List<WordCount> wordCounts;
        try {
            wordCounts = wordCountMapper.selectByExample(example);
        } catch (Exception e) {
            return Collections.emptyList();
        }
        log.info("[queryTopKWords]fileUid:{}, topK:{}, Words:{}",fileUid,k,new Gson().toJson(wordCounts));
        return wordCounts;
    }

}
