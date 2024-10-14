package priv.dawn.wordcount.api;

import priv.dawn.wordcount.domain.FileWordCountDto;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: Dawn Yang
 * @Since: 2024/10/12/21:40
 */
public interface WordDaoService {

    List<Integer> saveWordCount(FileWordCountDto fileWordCountDto);

    FileWordCountDto queryTopKWords(Integer fileUid, Integer K);

    FileWordCountDto queryWordCounts(Integer fileUid, List<String> words);

}
