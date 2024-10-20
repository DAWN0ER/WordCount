package priv.dawn.workers.domain;

import priv.dawn.workers.utils.hash.HashEnum;
import priv.dawn.workers.utils.hash.WordHashFunction;
import priv.dawn.workers.utils.hash.WordHashFunctionFactory;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: Dawn Yang
 * @Since: 2024/10/20/20:31
 */
public class WordMurmurEntry extends WordHashEntry{

    private static final WordHashFunction hashFunction = WordHashFunctionFactory.getHashFunc(HashEnum.Murmur3);

    public WordMurmurEntry(String word) {
        super(word);
    }

    @Override
    protected int hash(String word) {
        return hashFunction.hash(word);
    }
}
