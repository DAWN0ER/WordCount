package priv.dawn.wordcount.pojo.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Deprecated
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WordCountListVO {

    @JsonProperty("file_uid")
    private int fileUID;
    @JsonProperty("word_count_list")
    private List<WordCountPair> wordCountList;

    // 通过 FileInfoDTO 和 RPC 返回的字符串对应序列构造
    public WordCountListVO(int fileUID, List<String> wordCountStrList, String wordCountSplitStr) {
        this.fileUID = fileUID;
        this.wordCountList = new ArrayList<>(wordCountStrList.size());
        wordCountStrList.forEach(e -> wordCountList.add(new WordCountPair(e, wordCountSplitStr)));
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class WordCountPair {

        @JsonProperty("word")
        private String word;
        @JsonProperty("count")
        private int count;

        // TODO: 2024/5/11 这个参数异常以后得做个特定的
        public WordCountPair(String pairStr, String split) {
            String[] wc = pairStr.split(split);
            if (wc.length != 2) throw new RuntimeException("临时异常, 字符转分割失败");
            this.word = wc[0];
            this.count = Integer.parseInt(wc[1]);
        }

    }
}