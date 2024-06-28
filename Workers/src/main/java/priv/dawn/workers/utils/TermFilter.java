package priv.dawn.workers.utils;

import com.hankcs.hanlp.seg.common.Term;

import java.util.function.Predicate;

public class TermFilter implements Predicate<Term> {

    private static final String[] whiteList = {"a","n","vg","vi","vl","vn","i"};

    @Override
    public boolean test(Term term) {
        for (String str : whiteList) {
            if(term.nature.startsWith(str)) return true;
        }
        return false;
    }
}