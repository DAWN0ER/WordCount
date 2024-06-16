package priv.dawn.reducer.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import priv.dawn.kafkamessage.message.CustomMessage;
import priv.dawn.reducer.repository.SaveWordCountRepository;

import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
public class WordCountReducer {

    @Autowired
    SaveWordCountRepository saveWordCountRepository;

    @KafkaListener(topics = "word_count")
    public void onMassage(List<ConsumerRecord<String, String>> recordList) {

        HashMap<Integer, HashMap<String, Integer>> fileMap = new HashMap<>(8); //同一批次应该不会超过6个fileUID
        int partition = recordList.get(0).partition();

        // 处理消息
        // 假如数据有误, 基本是做丢弃处理, 可靠性不高, 这只能算一个简单计数功能
        // TODO: 2024/6/16 这是完全没有考虑到消息重复和消息幂等的各种问题啊
        for (ConsumerRecord<String, String> record : recordList) {

            int fileUID = Integer.parseInt(record.key());
            CustomMessage msg = CustomMessage.getFromJson(record.value());
            if (msg == null) {
                log.error("Message de json fail on " + record);
                continue;
            }

            // 处理每个Message 进行计数统计
            if (!fileMap.containsKey(fileUID))
                fileMap.put(fileUID, new HashMap<>(512));
            HashMap<String, Integer> wordCntMap = fileMap.get(fileUID);
            msg.getWordCountList().forEach(wordCount -> {
                String word = wordCount.getWord();
                int count = wordCount.getCount();
                if (!wordCntMap.containsKey(word))
                    wordCntMap.put(word, count);
                else {
                    int tmp = wordCntMap.get(word);
                    wordCntMap.put(word, count + tmp);
                }
            });
        }

        // 消息录入
        // TODO: 2024/6/16 存在严重的不幂等问题，出现异常都没办法定位和 redo, 需要重新设计一下
        fileMap.forEach((fileUID, wcMap) -> {
            try {
                saveWordCountRepository.saveFromWordCountMap(fileUID, partition, wcMap);
            } catch (Exception exception) {
                log.error("Transactional redo for " + exception + " when process " + fileUID + "-" + partition);
            }
        });
    }

}
