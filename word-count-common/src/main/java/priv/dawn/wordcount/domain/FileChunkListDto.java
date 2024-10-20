package priv.dawn.wordcount.domain;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: Dawn Yang
 * @Since: 2024/10/19/19:27
 */

@Data
public class FileChunkListDto {

    private Integer fileUid;
    private Long taskId;
    private Integer chunkId;

}
