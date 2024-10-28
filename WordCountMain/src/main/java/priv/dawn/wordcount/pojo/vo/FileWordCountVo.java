package priv.dawn.wordcount.pojo.vo;

import lombok.Data;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: Dawn Yang
 * @Since: 2024/10/14/18:21
 */
@Data
public class FileWordCountVo {

    private Integer fileUid;
    private List<WordCountVo> wordCounts;

}
