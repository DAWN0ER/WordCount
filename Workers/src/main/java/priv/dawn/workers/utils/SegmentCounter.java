package priv.dawn.workers.utils;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: Dawn Yang
 * @Since: 2024/10/21/10:27
 */

public class SegmentCounter {

    private static final String[] whiteList = {"a", "n", "vg", "vi", "vl", "vn", "i"};

    private static final Segment SEGMENT = HanLP.newSegment();

    public static Map<String, Integer> countWordOf(String context) {
        if (StringUtils.isBlank(context)) {
            return Collections.emptyMap();
        }
        List<Term> seg = SEGMENT.seg(context);
        HashMap<String, Integer> wordCount = new HashMap<>(128);
        for (Term term : seg) {
            if (!filterNature(term) || !filterWord(term.word)) continue;
            String word = term.word;
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
        // ', -- 这些奇怪的无法消除的判断一下
        if(word.replaceAll("'", "").replaceAll("-", "").equals("")){
            return false;
        }
        return true;
    }

}
