package priv.dawn.wordcount.domain;

import lombok.Data;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description: 文件信息
 * @Auther: Dawn Yang
 * @Since: 2024/10/12/21:23
 */

@Data
public class FileChunksVo {

    private Integer fileUid;
    private String filename;
    private Integer chunkNum;
    private List<ChunkVo> chunks;
}
