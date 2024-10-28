package priv.dawn.workers.wrapper;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import priv.dawn.wordcount.api.FileStoreService;
import priv.dawn.wordcount.domain.ChunkDto;
import priv.dawn.wordcount.domain.FileChunksDto;

import java.util.List;
import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: Dawn Yang
 * @Since: 2024/10/20/15:33
 */

@Slf4j
@Service
public class FileServiceWrapper {

    @DubboReference
    private FileStoreService fileStoreService;

    public ChunkDto getChunk(int fileUid, int chunkId) {
        if (fileUid <= 0 || chunkId <= 0) {
            log.error("[getChunk]参数异常: fileUid:{},chunkId:{}", fileUid, chunkId);
            return null;
        }
        FileChunksDto pagesByFile = null;
        try {
            pagesByFile = fileStoreService.getPagesByFile(fileUid, chunkId, chunkId);
        } catch (Exception e) {
            log.error("[getChunk]调用异常:", e);
            return null;
        }
        if (Objects.isNull(pagesByFile)) {
            return null;
        }
        List<ChunkDto> chunks = pagesByFile.getChunks();
        if (CollectionUtils.isEmpty(chunks)) {
            return null;
        }
        return chunks.get(0);
    }


}
