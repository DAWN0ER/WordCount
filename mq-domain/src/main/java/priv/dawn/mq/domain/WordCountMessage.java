package priv.dawn.mq.domain;

import lombok.Data;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: Dawn Yang
 * @Since: 2024/10/09/11:16
 */

@Data
public class WordCountMessage {

    private Long taskId;
    private Integer fileUid;
    private Integer chunkId;
    private List<WordCountEntry> wordCounts;

}
