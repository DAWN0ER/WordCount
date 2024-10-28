package priv.dawn.wordcount.api;

import priv.dawn.wordcount.domain.ChunkCountTaskDto;

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
     * @param chunkCountTaskDto 入参
     * @return 返回 map 到的 partition 数量
     */
    int countWordsOfChunk(ChunkCountTaskDto chunkCountTaskDto);

    /**
     * 异步完成计数
     * @param chunkCountTaskDto 入参
     */
    void countWordOfChunkAsync(ChunkCountTaskDto chunkCountTaskDto);

}
