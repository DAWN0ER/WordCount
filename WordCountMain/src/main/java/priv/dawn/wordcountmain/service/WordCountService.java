package priv.dawn.wordcountmain.service;

import priv.dawn.wordcountmain.domain.FileWordCountStateEnum;
import priv.dawn.wordcountmain.pojo.vo.WordCountListVO;

import java.util.List;

public interface WordCountService {
    FileWordCountStateEnum startCountWord(int fileUID);
    float getProgress(int fileUID);
    WordCountListVO getWordCounts(int fileUID);
}
