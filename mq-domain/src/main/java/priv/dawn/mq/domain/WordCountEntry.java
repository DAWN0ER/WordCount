package priv.dawn.mq.domain;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: Dawn Yang
 * @Since: 2024/10/21/11:01
 */

@Data
public class WordCountEntry {

    private String word;
    private Integer count;

}
