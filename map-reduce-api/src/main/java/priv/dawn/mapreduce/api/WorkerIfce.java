package priv.dawn.mapreduce.api;

import priv.dawn.mapreduce.domain.BaseResponse;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description: Worker 计数服务的接口
 * @Auther: Dawn Yang
 * @Since: 2024/10/05/17:09
 */
public interface WorkerIfce {

//     这个架构怎么这么怪呢，再看看

    BaseResponse<Integer> countChunk(int fileUid, int chunkId, boolean async);

    /**
     * 对连续的一段 chunks 计数
     * @param fileUid 文件UID
     * @param beginChunkId 起始 chunk 的 UID
     * @param endChunkId 截至 chunk 的 UID (不包含)
     * @param async 是否异步
     * @return 返回成功处理的 chunk 的个数
     */
    BaseResponse<Integer> countChunks(int fileUid, int beginChunkId, int endChunkId, boolean async);
}
