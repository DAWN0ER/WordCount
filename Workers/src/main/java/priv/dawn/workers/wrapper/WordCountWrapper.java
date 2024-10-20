package priv.dawn.workers.wrapper;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import priv.dawn.wordcount.api.WordDaoService;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: Dawn Yang
 * @Since: 2024/10/20/15:52
 */

@Service
public class WordCountWrapper {

    @DubboReference
    private WordDaoService wordDaoService;



}
