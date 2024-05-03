package priv.dawn.wordcountmain.service;

import priv.dawn.wordcountmain.domain.FileWordCountStateEnum;

import java.util.List;

public interface WordCountService {
    FileWordCountStateEnum startCountWord(int fileUID);
    float getProgress(int fileUID);
    List<String> getWordCounts(int fileUID);
}
