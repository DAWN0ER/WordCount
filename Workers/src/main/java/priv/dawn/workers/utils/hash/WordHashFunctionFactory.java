package priv.dawn.workers.utils.hash;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: Dawn Yang
 * @Since: 2024/10/20/19:33
 */
public class WordHashFunctionFactory {

    private static final int SEED = 8675309;
    private static final HashFunction murmur32 = Hashing.murmur3_32_fixed(SEED);
    private static final HashFunction farmHash = Hashing.goodFastHash(32);


    public static WordHashFunction getHashFunc(HashEnum hashEnum) {
        switch (hashEnum) {
            case Murmur3:
                return new WordHashFunction() {
                    @Override
                    public int hash(String word) {
                        if (Objects.isNull(word)) return 0;
                        return murmur32.hashString(word, StandardCharsets.UTF_8).asInt();
                    }
                };
            case FarmHash:
                return new WordHashFunction() {
                    @Override
                    public int hash(String word) {
                        if (Objects.isNull(word)) return 0;
                        return farmHash.hashString(word, StandardCharsets.UTF_8).asInt();
                    }
                };
            default:
                return new WordHashFunction() {
                    @Override
                    public int hash(String word) {
                        return Objects.hashCode(word);
                    }
                };
        }
    }

}
