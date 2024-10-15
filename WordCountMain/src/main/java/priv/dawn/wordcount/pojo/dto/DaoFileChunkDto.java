package priv.dawn.wordcount.pojo.dto;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description: 用于 FileStoreDaoService 输入中间实体
 * @Auther: Dawn Yang
 * @Since: 2024/10/15/14:30
 */

@Data
public class DaoFileChunkDto {

    /** chunk 编号 **/
    private Integer chunkId;

    /** 文本 chunk 内容 **/
    private String context;

}
