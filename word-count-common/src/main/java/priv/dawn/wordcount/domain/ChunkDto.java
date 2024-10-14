package priv.dawn.wordcount.domain;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description: 文件分区 VO
 * @Auther: Dawn Yang
 * @Since: 2024/10/12/21:28
 */

@Data
public class ChunkDto {

    private Integer chunkId;
    private String context;

}
