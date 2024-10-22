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

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

        for (ConsumerRecord<String, String> record : records) {

            WordCountMessage message;
            try {
                message = gson.fromJson(record.value(), WordCountMessage.class);
            } catch (JsonSyntaxException e) {
                log.error("解析消息失败:{}, topic:{}-partition:{}-offset{}",
                        record.value(),TOPIC,record.partition(),record.offset());
                continue;
            }
            // 判断 null
            if(Objects.isNull(message.getTaskId()) || Objects.isNull(message.getFileUid())
                    || Objects.isNull(message.getChunkId()) || CollectionUtils.isEmpty(message.getWordCounts())){
                log.error("解析失败,参数异常:taskId:{},fileUid:{},chunkId:{},wordCounts:{}",
                        message.getTaskId(),message.getFileUid(),message.getChunkId(),gson.toJson(message.getWordCounts()));
                continue;
            }

            long taskId = message.getTaskId();
            int fileUid = message.getFileUid();
            int chunkId = message.getChunkId();
            int partition = record.partition();
            List<WordCountEntry> wordCounts = message.getWordCounts();

            if(!insertMap.containsKey(taskId)){
                WordCountMap map = new WordCountMap();
                map.setFileUid(fileUid);
                map.setTaskId(taskId);
                map.setWordCountMap(new HashMap<>(256));
                insertMap.put(taskId,map);
            }
            insertMap.get(taskId).merge(wordCounts);
        }
        ack.acknowledge();

    }
}
