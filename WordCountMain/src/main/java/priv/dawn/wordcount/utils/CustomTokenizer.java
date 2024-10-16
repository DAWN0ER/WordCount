package priv.dawn.wordcount.utils;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: Dawn Yang
 * @Since: 2024/10/16/20:47
 */
public class CustomTokenizer {

    private static final Segment SEGMENT = HanLP.newSegment().enableOffset(true);

    public static List<Term> segment(String text){
        return SEGMENT.seg(text);
    }

}
