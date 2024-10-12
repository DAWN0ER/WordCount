package priv.dawn.wordcount.api;

import priv.dawn.wordcount.domain.FileWordCountVo;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: Dawn Yang
 * @Since: 2024/10/12/21:40
 */
public interface WordDaoService {

    List<Integer> saveWordCount(FileWordCountVo fileWordCountVo);

    FileWordCountVo queryTopKWords(Long fileUid,Integer K);

    FileWordCountVo queryWordCounts(Long fileUid, List<String> words);

}
