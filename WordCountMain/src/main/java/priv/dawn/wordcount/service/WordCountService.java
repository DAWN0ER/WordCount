package priv.dawn.wordcount.service;

import priv.dawn.wordcount.domain.WordCountStateEnum;
import priv.dawn.wordcount.pojo.vo.WordCountListVO;

@Deprecated
public interface WordCountService {
    WordCountStateEnum startCountWord(int fileUID);

    WordCountListVO getWordCounts(int fileUID);
}
