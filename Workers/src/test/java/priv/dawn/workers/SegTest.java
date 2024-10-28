package priv.dawn.workers;

import com.google.gson.Gson;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: Dawn Yang
 * @Since: 2024/10/25/17:18
 */
public class SegTest {

    @Test
    public void segTest(){
        String s = "bilibili是一个'全国流行--的计算机-学习网站，我也是一个喜欢计算机编程的人，所以想要了解这些";
        Map<String, Integer> map = countWordOf(s);
        for (String word : map.keySet()) {
            System.out.println("word = " + word);
        }
    }

    private static final String[] whiteList = {"a", "g", "n", "v", "i"};

    private static final Segment SEGMENT = HanLP.newSegment();

    public static Map<String, Integer> countWordOf(String context) {
        if (StringUtils.isBlank(context)) {
            return Collections.emptyMap();
        }
        List<Term> seg = SEGMENT.seg(context);
        HashMap<String, Integer> wordCount = new HashMap<>(128);
        for (Term term : seg) {
//            System.out.println("term.word = " + term.word + new Gson().toJson(term));
            if (!filterNature(term) || !filterWord(term.word)) continue;
            String word = term.word;
            System.out.println("filter word = "+ term.word + new Gson().toJson(term));
            wordCount.merge(word,1,Integer::sum);
        }
        return wordCount;
    }

    private static boolean filterNature(Term term) {
        for (String str : whiteList) {
            if (term.nature.startsWith(str)) return true;
        }
        return false;
    }

    private static boolean filterWord(String word){
        if (word.equals("是") || word.equals("有")){
            return false;
        }
        if(word.replaceAll("'", "").replaceAll("-", "").equals("")){
            return false;
        }
        return true;
    }

}
