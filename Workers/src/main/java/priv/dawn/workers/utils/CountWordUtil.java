package priv.dawn.workers.utils;

import com.google.common.hash.Hashing;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;
import priv.dawn.kafkamessage.message.CustomMessage;
import priv.dawn.workers.pojo.ChunkDTO;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class CountWordUtil {

    public static ArrayList<CustomMessage> generateMsgPartitionMapperFromChunk(int fileUID, ChunkDTO chunk,int partitionNum){

        // 分词过滤
        List<Term> terms = StandardTokenizer.segment(chunk.getContext()).stream().filter(t
                -> !t.nature.startsWith("u")
                && !t.nature.startsWith("w")
                && !t.nature.startsWith("r")
                && !t.nature.startsWith("m")
        ).collect(Collectors.toList()); // 过滤符号, 助词, 代词, 数量词

        ArrayList<HashMap<String, Integer>> partitionMaps = new ArrayList<>(partitionNum);
        for (int idx = 0; idx < partitionNum; idx++)
            partitionMaps.add(new HashMap<>(terms.size() / 4 * 2));
        // 词频统计
        terms.forEach((Term term) -> {
            String word = term.word;
            int partition = (int) (Hashing.murmur3_32_fixed().hashString(word, StandardCharsets.UTF_8).padToLong() % partitionNum);
            HashMap<String, Integer> wordCnt = partitionMaps.get(partition);
            if (wordCnt.containsKey(word)) {
                int tmp = wordCnt.get(word);
                wordCnt.put(word, tmp + 1);
            } else wordCnt.put(word, 1);
        });
        // 构造消息
        // 直接用 json 序列化, 省的麻烦
        ArrayList<CustomMessage> messages = new ArrayList<>(partitionNum);
        partitionMaps.forEach(map -> messages.add(new CustomMessage(chunk.getChunkId(), map)));
        return messages;
    }
}
