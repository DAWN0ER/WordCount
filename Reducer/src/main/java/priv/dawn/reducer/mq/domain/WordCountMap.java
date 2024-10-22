package priv.dawn.reducer.mq.domain;

import lombok.Data;
import priv.dawn.mq.domain.WordCountEntry;

import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: Dawn Yang
 * @Since: 2024/10/22/10:31
 */
@Data
public class WordCountMap {

    private long taskId;
    private int fileUid;
    private HashMap<String,Integer> wordCountMap;

    public void merge(String word,int count){
        wordCountMap.merge(word,count,Integer::sum);
    }

    public void merge(List<WordCountEntry> wordCountEntryList){
        for (WordCountEntry entry : wordCountEntryList) {
            this.merge(entry.getWord(),entry.getCount());
        }
    }

}
