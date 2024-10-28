package priv.dawn.wordcount.pojo.dto;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: Dawn Yang
 * @Since: 2024/10/15/15:45
 */
@Data
public class DaoFileInfoDto {

    private Integer fileUid;
    private String fileName;
    private Integer chunkNum;
    private Integer status;

}
