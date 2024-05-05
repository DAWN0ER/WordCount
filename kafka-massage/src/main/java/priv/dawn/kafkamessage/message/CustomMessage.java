package priv.dawn.kafkamessage.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomMessage {

    @JsonProperty("cid")
    private int chunkId;
    @JsonProperty("words")
    private List<WordCount> wordCountList;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WordCount implements Serializable{
        @JsonProperty("w")
        String word;
        @JsonProperty("c")
        int count;
    }

    public CustomMessage(int chunkId, HashMap<String,Integer> map){
        this.chunkId=chunkId;
        this.wordCountList = new ArrayList<>(map.size());
        map.forEach((String word,Integer cnt)-> this.wordCountList.add(new WordCount(word,cnt)));
    }


    public String toJsonStr(){
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static CustomMessage getFromJson(String json){
        try {
            return new ObjectMapper().readValue(json,CustomMessage.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
