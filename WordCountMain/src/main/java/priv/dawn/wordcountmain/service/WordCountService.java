package priv.dawn.wordcountmain.service;

import priv.dawn.wordcountmain.domain.WordCountStateEnum;
import priv.dawn.wordcountmain.pojo.vo.WordCountListVO;

public interface WordCountService {
    WordCountStateEnum startCountWord(int fileUID);
    WordCountListVO getWordCounts(int fileUID);
}
