package priv.dawn.wordcountmain.service;

import java.util.List;

public interface WordCountService {
    int startCountWord(int fileUID);
    float getProgress(int fileUID);
    List<String> getWordCounts(int fileUID);
}
