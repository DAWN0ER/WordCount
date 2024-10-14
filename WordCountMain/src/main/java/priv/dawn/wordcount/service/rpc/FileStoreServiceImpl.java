package priv.dawn.wordcount.service.rpc;

import priv.dawn.wordcount.api.FileStoreService;
import priv.dawn.wordcount.domain.FileChunksDto;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: Dawn Yang
 * @Since: 2024/10/12/22:02
 */

//@DubboService
public class FileStoreServiceImpl implements FileStoreService {

    @Override
    public FileChunksDto getFile(Long fileUid) {
        return null;
    }

    @Override
    public FileChunksDto getPagesByFile(Long fileUid, int startChunk, int endChunk) {
        return null;
    }

    @Override
    public FileChunksDto getFileInfo(Long fileUid) {
        return null;
    }
}
