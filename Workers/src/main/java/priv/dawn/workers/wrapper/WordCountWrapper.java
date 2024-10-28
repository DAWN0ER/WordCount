package priv.dawn.workers.wrapper;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import priv.dawn.wordcount.api.WordDaoService;
import priv.dawn.wordcount.domain.FileWordCountDto;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: Dawn Yang
 * @Since: 2024/10/20/15:52
 */

@Slf4j
@Service
public class WordCountWrapper {

    @DubboReference
    private WordDaoService wordDaoService;

    public List<String> saveWordCounts(FileWordCountDto fileWordCountDto){
        if(Objects.isNull(fileWordCountDto)){
            log.error("[saveWordCounts] 参数异常:null");
            return null;
        }
        try {
            return wordDaoService.saveWordCount(fileWordCountDto);
        } catch (Exception e) {
            log.error("[saveWordCounts]调用异常:",e);
            return Collections.emptyList();
        }
    }

}
