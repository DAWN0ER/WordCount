package priv.dawn.wordcountmain.utils;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

public class UidGenerator {

    public static int getOneUid() {
        int uid = Hashing.murmur3_32_fixed().newHasher()
                .putString(UUID.randomUUID().toString(),StandardCharsets.UTF_8)
                .putLong(new Date().getTime())
                .hash().asInt();
        return uid>0?uid:-uid;
    }

}
