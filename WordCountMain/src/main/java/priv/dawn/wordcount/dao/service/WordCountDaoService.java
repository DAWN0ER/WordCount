package priv.dawn.wordcount.dao.service;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import priv.dawn.wordcount.dao.domain.WordCount;
import priv.dawn.wordcount.dao.domain.WordCountExample;
import priv.dawn.wordcount.dao.mapper.primary.WordCountMapper;
import priv.dawn.wordcount.pojo.dto.DaoWordCountDto;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

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

    @Resource
    private WordCountMapper wordCountMapper;

    public List<DaoWordCountDto> queryCountByWords(int fileUid, List<String> words) {
        WordCountExample example = new WordCountExample();
        example.createCriteria().andFileUidEqualTo(fileUid).andWordIn(words);
        List<WordCount> wordCounts;

        try {
            wordCounts = wordCountMapper.selectByExample(example);
        } catch (Exception e) {
            log.error("[queryCountByWords] 异常, fileUid:{},words:{}", fileUid, words, e);
            return Collections.emptyList();
        }

        List<DaoWordCountDto> result = castToDto(wordCounts);
        log.info("[queryCountByWords] fileUid:{}, words:{}, Words:{}", fileUid, words, new Gson().toJson(result));
        return result;
    }

    public List<DaoWordCountDto> queryTopKWords(int fileUid, int k) {

        WordCountExample example = new WordCountExample();
        example.createCriteria().andFileUidEqualTo(fileUid);
        example.setOrderByClause("cnt DESC LIMIT " + k); // 这个字符串是加在末尾的，暂时用来写一些奇奇怪怪的附加条件

        List<WordCount> wordCounts;
        try {
            wordCounts = wordCountMapper.selectByExample(example);
        } catch (Exception e) {
            log.error("[queryTopKWords] 异常, fileUid:{},topK:{}", fileUid, k, e);
            return Collections.emptyList();
        }

        List<DaoWordCountDto> result = castToDto(wordCounts);
        log.info("[queryTopKWords]fileUid:{}, topK:{}, Words:{}", fileUid, k, new Gson().toJson(result));
        return result;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<String> saveWordCounts(List<DaoWordCountDto> wordCountDtoList, int fileUid) {
        List<String> inCondition = wordCountDtoList.stream().map(DaoWordCountDto::getWord).collect(Collectors.toList());
        WordCountExample example = new WordCountExample();
        example.createCriteria().andFileUidEqualTo(fileUid).andWordIn(inCondition);
        List<WordCount> select = wordCountMapper.selectByExample(example);
        Set<String> existWords;
        if (CollectionUtils.isEmpty(select)) {
            existWords = Collections.emptySet();
        } else {
            existWords = select.stream().map(WordCount::getWord).collect(Collectors.toSet());
        }

        List<String> result = new ArrayList<>();

        for (DaoWordCountDto dto : wordCountDtoList) {
            WordCount record = new WordCount();
            record.setFileUid(fileUid);
            record.setWord(dto.getWord());
            record.setCnt(dto.getCount());

            // 如果已经存在则 update append, 不存在直接 insert, insert 失败再尝试 update
            int success;
            if (existWords.contains(record.getWord())) {
                success = wordCountMapper.updateAppendCount(record);
            } else {
                success = wordCountMapper.insert(record);
                if(success <= 0) {
                    success = wordCountMapper.updateAppendCount(record);
                }
            }
            if (success > 0) {
                result.add(dto.getWord());
            }
        }

        return result;
    }

    private List<DaoWordCountDto> castToDto(List<WordCount> wordCounts){
        if (CollectionUtils.isEmpty(wordCounts)){
            return Collections.emptyList();
        }
        return wordCounts.stream().map((select)->{
            DaoWordCountDto dto = new DaoWordCountDto();
            dto.setCount(select.getCnt());
            dto.setWord(select.getWord());
            return dto;
        }).collect(Collectors.toList());
    }

}
