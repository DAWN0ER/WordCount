package priv.dawn.wordcount.service.rpc;

import org.apache.dubbo.config.annotation.DubboService;
import priv.dawn.wordcount.api.WordDaoService;
import priv.dawn.wordcount.dao.service.WordCountDaoService;
import priv.dawn.wordcount.domain.FileWordCountDto;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: Dawn Yang
 * @Since: 2024/10/12/22:50
 */

@DubboService
public class WordDaoServiceImpl implements WordDaoService {

    @Resource
    WordCountDaoService wordCountDaoService;

    @Override
    public List<Integer> saveWordCount(FileWordCountDto fileWordCountDto) {
        return null;
    }

    @Override
    public FileWordCountDto queryTopKWords(Integer fileUid, Integer K) {
        if(Objects.isNull(fileUid) || Objects.isNull(K) || K <= 0){
            // TODO 抛出异常
            return null;
        }


        return null;
    }

    @Override
    public FileWordCountDto queryWordCounts(Integer fileUid, List<String> words) {
        return null;
    }
}
