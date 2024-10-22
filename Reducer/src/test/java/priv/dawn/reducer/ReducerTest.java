package priv.dawn.reducer;

import org.junit.Test;
import priv.dawn.reducer.wrapper.WordCountDaoWrapper;
import priv.dawn.wordcount.domain.FileWordCountDto;
import priv.dawn.wordcount.domain.WordCountDto;

import javax.annotation.Resource;
import java.util.ArrayList;

public class ReducerTest extends ReducerApplicationTests {

    String data = "大地回春 春花怒放 春色撩人 春色满园 淅淅沥沥 雨声沙沙 细雨淅沥 春雨连绵 雨过天晴 细雨如丝 春雨阵阵 含苞欲放 风和日丽 和风细雨 柳绿花红 气象万千";

    @Resource
    private WordCountDaoWrapper wordCountDaoWrapper;

    @Test
    public void simpleTest() {

        FileWordCountDto countDto = new FileWordCountDto();
        countDto.setFileUid(123456);
        countDto.setWordCounts(new ArrayList<>());

        String[] s = data.split(" ");
        for (String str : s) {
            WordCountDto dto = new WordCountDto();
            dto.setWord(str);
            dto.setCount(3);
            countDto.getWordCounts().add(dto);
        }
        wordCountDaoWrapper.saveWordCount(countDto);
    }




}
