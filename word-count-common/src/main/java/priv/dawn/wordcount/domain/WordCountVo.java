package priv.dawn.wordcount.domain;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: Dawn Yang
 * @Since: 2024/10/12/21:42
 */

@Data
public class WordCountVo {

    private String word;
    private Integer count;
}
