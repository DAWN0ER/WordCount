package priv.dawn.wordcount.service.rpc;

import priv.dawn.wordcount.api.WordDaoService;
import priv.dawn.wordcount.dao.mapper.primary.WordCountMapper;
import priv.dawn.wordcount.domain.FileWordCountVo;

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

//@DubboService
public class WordDaoServiceImpl implements WordDaoService {

    @Resource
    WordCountMapper wordCountMapper;

    @Override
    public List<Integer> saveWordCount(FileWordCountVo fileWordCountVo) {
        return null;
    }

    @Override
    public FileWordCountVo queryTopKWords(Integer fileUid, Integer K) {
        if(Objects.isNull(fileUid) || Objects.isNull(K) || K <= 0){
            // TODO 抛出异常
            return null;
        }


        return null;
    }

    @Override
    public FileWordCountVo queryWordCounts(Integer fileUid, List<String> words) {
        return null;
    }
}
