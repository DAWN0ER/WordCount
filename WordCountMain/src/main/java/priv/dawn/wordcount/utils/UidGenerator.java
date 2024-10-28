package priv.dawn.wordcount.utils;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

public class UidGenerator {

    public static int getHashUid(String fileName) {
        HashFunction hashFunction = Hashing.murmur3_32_fixed();
        int hash1 = hashFunction.hashString(fileName, StandardCharsets.UTF_8).asInt();
        int hash2 = hashFunction.hashLong(System.currentTimeMillis()).asInt();
        return Math.abs(hash1 ^ hash2);
    }

    public static long getMistUid(){
        return MistUidGenerator.getInstance().getUid();
    }

}
