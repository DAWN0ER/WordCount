package priv.dawn.wordcount.domain;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: Dawn Yang
 * @Since: 2024/10/14/15:00
 */

@Data
public class FileWordCountTaskVo {

    private Integer fileUid;
    private Long taskId;
    private String fileName;

}
