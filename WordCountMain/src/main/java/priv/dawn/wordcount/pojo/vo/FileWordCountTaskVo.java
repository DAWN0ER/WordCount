package priv.dawn.wordcount.pojo.vo;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: Dawn Yang
 * @Since: 2024/10/14/18:22
 */

@Data
public class FileWordCountTaskVo {

    private Integer fileUid;
    private Long taskId;
    private String fileName;
    private Integer status;
    private Double progress;

}
