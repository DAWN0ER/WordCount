package priv.dawn.wordcount.domain;

import lombok.Data;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: Dawn Yang
 * @Since: 2024/10/12/21:50
 */

@Data
public class FileWordCountDto {

    private Integer fileUid;
    private List<WordCountDto> wordCounts;

}
