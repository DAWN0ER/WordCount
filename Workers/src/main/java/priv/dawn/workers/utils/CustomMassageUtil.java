package priv.dawn.workers.utils;

import com.google.common.hash.Hashing;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;
import priv.dawn.kafkamessage.message.CustomMessage;
import priv.dawn.workers.pojo.ChunkDTO;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class CustomMassageUtil {

    private static final TermFilter termFilter = new TermFilter();

    public static ArrayList<CustomMessage> generateFromChunk(ChunkDTO chunk, int partitionNum) {

        // 分词过滤
        List<String> words = StandardTokenizer.segment(chunk.getContext()).stream()
                .filter(termFilter)
                .map(term -> term.word)
                .collect(Collectors.toList());

        // 词频统计
        ArrayList<HashMap<String, Integer>> partitionMaps = new ArrayList<>(partitionNum);
        for (int idx = 0; idx < partitionNum; idx++)
            partitionMaps.add(new HashMap<>(words.size() / 4 * 2));
        words.forEach((String word) -> {
            int partition = getHash(word) % partitionNum;
            HashMap<String, Integer> wordCnt = partitionMaps.get(partition);
            if (wordCnt.containsKey(word)) {
                int tmp = wordCnt.get(word);
                wordCnt.put(word, tmp + 1);
            } else wordCnt.put(word, 1);
        });

        // 构造消息 直接用 json 序列化, 省的麻烦
        ArrayList<CustomMessage> messages = new ArrayList<>(partitionNum);
        partitionMaps.forEach(map -> messages.add(new CustomMessage(map)));
        return messages;
    }

    // TODO: 2024/6/28 幂等重试的方法
    public static CustomMessage generateOneByKey(String key){
        return null;
    }

    private static int getHash(String word) {
        return (int) (Hashing.murmur3_32_fixed().hashString(word, StandardCharsets.UTF_8).padToLong());
    }
}
