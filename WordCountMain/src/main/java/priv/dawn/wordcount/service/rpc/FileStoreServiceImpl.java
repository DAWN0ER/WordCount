package priv.dawn.wordcount.service.rpc;

import org.apache.dubbo.config.annotation.DubboService;
import priv.dawn.wordcount.api.FileStoreService;
import priv.dawn.wordcount.domain.FileChunksVo;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: Dawn Yang
 * @Since: 2024/10/12/22:02
 */

@DubboService
public class FileStoreServiceImpl implements FileStoreService {

    @Override
    public FileChunksVo getFile(Long fileUid) {
        return null;
    }

    @Override
    public FileChunksVo getPagesByFile(Long fileUid, int startChunk, int endChunk) {
        return null;
    }

    @Override
    public FileChunksVo getFileInfo(Long fileUid) {
        return null;
    }
}
