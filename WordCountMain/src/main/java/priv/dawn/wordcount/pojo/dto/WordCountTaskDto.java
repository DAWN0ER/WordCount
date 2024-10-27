package priv.dawn.wordcount.pojo.dto;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: Dawn Yang
 * @Since: 2024/10/27/17:31
 */

@Data
public class WordCountTaskDto {

    private Long taskId;

    private Integer fileUid;

    private Integer status;

}
