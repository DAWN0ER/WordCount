package priv.dawn.wordcount.service.rpc;

import org.apache.dubbo.config.annotation.DubboService;
import priv.dawn.wordcount.api.WordDaoService;
import priv.dawn.wordcount.domain.FileWordCountVo;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: Dawn Yang
 * @Since: 2024/10/12/22:50
 */

@DubboService
public class WordDaoServiceImpl implements WordDaoService {

    @Override
    public List<Integer> saveWordCount(FileWordCountVo fileWordCountVo) {
        return null;
    }

    @Override
    public FileWordCountVo queryTopKWords(Long fileUid, Integer K) {
        return null;
    }

    @Override
    public FileWordCountVo queryWordCounts(Long fileUid, List<String> words) {
        return null;
    }
}
