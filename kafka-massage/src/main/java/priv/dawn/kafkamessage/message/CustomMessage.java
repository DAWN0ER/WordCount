package priv.dawn.kafkamessage.message;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
public class CustomMessage implements Serializable {
    private int chunkId;
    private List<WordCount> wordCountList;

    @Data
    @AllArgsConstructor
    public class WordCount implements Serializable{
        String word;
        int count;
    }

    public CustomMessage(int chunkId, HashMap<String,Integer> map){
        this.chunkId=chunkId;
        this.wordCountList = new ArrayList<>(map.size());
        map.forEach((String word,Integer cnt)->{
            this.wordCountList.add(new WordCount(word,cnt));
        });
    }

}
