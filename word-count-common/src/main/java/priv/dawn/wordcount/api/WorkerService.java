package priv.dawn.wordcount.api;

import priv.dawn.wordcount.domain.FileChunkListDto;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: Dawn Yang
 * @Since: 2024/10/09/11:05
 */

public interface WorkerService {

    /**
     * 同步计数
     * @param fileChunkListDto 入参
     * @return 计数成功的 chunkIds
     */
    Integer countWordsOfChunk(FileChunkListDto fileChunkListDto);

    /**
     * 异步完成计数
     * @param fileChunkListDto 入参
     */
    void countWordOfChunkAsync(FileChunkListDto fileChunkListDto);

}
