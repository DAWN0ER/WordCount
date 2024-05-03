package priv.dawn.wordcountmain.service.impl;

import org.springframework.stereotype.Service;
import priv.dawn.wordcountmain.service.WordCountService;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WordCountServiceImpl implements WordCountService {

    
    ConcurrentHashMap<Integer,Boolean> progressMap;

    @Override
    public int startCountWord(int fileUID) {


        return 0;
    }

    @Override
    public float getProgress(int fileUID) {
        return 0;
    }

    @Override
    public List<String> getWordCounts(int fileUID) {
        return null;
    }
}
