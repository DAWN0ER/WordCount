package priv.dawn.wordcount.api;

import priv.dawn.wordcount.domain.FileChunksDto;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description: 文件存储调用接口
 * @Auther: Dawn Yang
 * @Since: 2024/10/12/21:23
 */
public interface FileStoreService {

    /**
     * 返回指定文件
     * @param fileUid 文件id 标识符
     * @return FileVO chunks 默认全部
     */
    FileChunksDto getFile(Long fileUid);

    /**
     * 返回指定区间 [startChunk,endChunk] 的文件区块
     * @param fileUid 文件id 标识符
     * @param startChunk 起始区块索引 >= 1
     * @param endChunk 结束区块索引(包含 endChunk)
     * @return FileVo chunks 只包含指定区间
     */
    FileChunksDto getPagesByFile(Long fileUid, int startChunk, int endChunk);

    /**
     * 返回指定文件的基础信息，没有 chunks，减小网路开销
     * @param fileUid 文件id 标识符
     * @return chunks 为空的 FileVo
     */
    FileChunksDto getFileInfo(Long fileUid);
}
