package priv.dawn.reducer.mq.service;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import priv.dawn.mq.domain.WordCountEntry;
import priv.dawn.mq.domain.WordCountMessage;
import priv.dawn.reducer.mq.domain.WordCountMap;
import priv.dawn.reducer.wrapper.WordCountDaoWrapper;
import priv.dawn.wordcount.domain.FileWordCountDto;
import priv.dawn.wordcount.domain.WordCountDto;

import javax.annotation.Resource;
import java.util.*;

@Component
@Slf4j
public class WordCountReducerService {

    private final Gson gson = new Gson();

    private final String TOPIC = "word_count";

    @Resource
    private WordCountDaoWrapper wordCountDaoWrapper;

    @KafkaListener(groupId = "${spring.kafka.consumer.group-id}", topics = TOPIC)
    public void onMassage(List<ConsumerRecord<String, String>> records, Acknowledgment ack) {
        // 处理消息
        Map<Long, WordCountMap> insertMap = new HashMap<>();
        int partition = records.get(0).partition(); // 一个 Batch 都是来自同一个 Partition 的

        for (ConsumerRecord<String, String> record : records) {
            // 解析消息
            WordCountMessage message;
            try {
                message = gson.fromJson(record.value(), WordCountMessage.class);
            } catch (JsonSyntaxException e) {
                log.error("解析消息失败:{}, topic:{}-partition:{}-offset{}",
                        record.value(), TOPIC, record.partition(), record.offset());
                continue;
            }
            if (Objects.isNull(message.getTaskId()) || Objects.isNull(message.getFileUid())
                    || Objects.isNull(message.getChunkId()) || CollectionUtils.isEmpty(message.getWordCounts())) {
                log.error("解析失败,参数异常:taskId:{},fileUid:{},chunkId:{},wordCounts:{}",
                        message.getTaskId(), message.getFileUid(), message.getChunkId(),
                        gson.toJson(message.getWordCounts()));
                continue;
            }

            // 处理消息
            long taskId = message.getTaskId();
            int fileUid = message.getFileUid();
            int chunkId = message.getChunkId();
            List<WordCountEntry> wordCounts = message.getWordCounts();
            log.info("解析到消息:taskId:{},fileUid:{},chunkId:{},topic:{}-partition:{}-offset{}",
                    taskId,fileUid,chunkId,TOPIC,partition,record.offset());

            if (!insertMap.containsKey(taskId)) {
                WordCountMap map = new WordCountMap();
                map.setFileUid(fileUid);
                map.setTaskId(taskId);
                map.setWordCountMap(new HashMap<>(256));
                map.setChunkIdSet(new HashSet<>(16));
                insertMap.put(taskId, map);
            }
            insertMap.get(taskId).merge(chunkId,wordCounts);
        }
        for (WordCountMap wordCountMap : insertMap.values()) {
            boolean success = save(wordCountMap);
            if(!success){
                log.error("word-count 存储失败:taskId:{},fileUid:{},partition:{},chunkIds:{},words:{}",
                        wordCountMap.getTaskId(),wordCountMap.getFileUid(),partition,
                        wordCountMap.getChunkIdSet(),wordCountMap.getWordCountMap());
                continue;
            }
            log.info("word-count 存储成功:taskId:{},fileUid:{},partition:{},chunkIds:{},words:{}",
                    wordCountMap.getTaskId(),wordCountMap.getFileUid(),partition,
                    wordCountMap.getChunkIdSet(),wordCountMap.getWordCountMap());
            // TODO 更新 Redis
        }
        ack.acknowledge();
    }

    private boolean save(WordCountMap wordCountMap) {
        FileWordCountDto fileWordCountDto = new FileWordCountDto();
        fileWordCountDto.setFileUid(wordCountMap.getFileUid());
        HashMap<String, Integer> wordCounts = wordCountMap.getWordCountMap();
        List<WordCountDto> wordCountDtoList = new ArrayList<>(wordCounts.size());
        wordCounts.forEach((word, count) -> {
            WordCountDto dto = new WordCountDto();
            dto.setWord(word);
            dto.setCount(count);
            wordCountDtoList.add(dto);
        });
        fileWordCountDto.setWordCounts(wordCountDtoList);
        List<String> list = wordCountDaoWrapper.saveWordCount(fileWordCountDto);
        if (list.size() < wordCountDtoList.size()) {
            log.error("[wordCountDaoWrapper.saveWordCount] 调用失败");
            return false;
        }
        return true;
    }
}
