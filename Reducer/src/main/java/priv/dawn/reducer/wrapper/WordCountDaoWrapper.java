package priv.dawn.reducer.wrapper;

import com.google.gson.Gson;
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
 * @Since: 2024/10/22/9:54
 */

@Slf4j
@Service
public class WordCountDaoWrapper {

    private final Gson gson = new Gson();

    @DubboReference
    private WordDaoService wordDaoService;

    public List<String> saveWordCount(FileWordCountDto fileWordCountDto) {
        if (Objects.isNull(fileWordCountDto)) {
            log.error("[saveWordCount] 输入为 null");
            return Collections.emptyList();
        }
        log.info("[saveWordCount] fileWordCountDto:{}", gson.toJson(fileWordCountDto));
        try {
            List<String> words = wordDaoService.saveWordCount(fileWordCountDto);
            log.info("[saveWordCount] 调用结果: success words:{}", words);
            return words;
        } catch (Exception e) {
            log.info("[saveWordCount] 调用异常:", e);
            return Collections.emptyList();
        }
    }

}
