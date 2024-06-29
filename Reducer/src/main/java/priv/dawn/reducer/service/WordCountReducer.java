package priv.dawn.reducer.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import priv.dawn.kafkamessage.message.CustomMessage;
import priv.dawn.reducer.repository.SaveWordCountRepository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class WordCountReducer {

    @Autowired
    SaveWordCountRepository saveWordCountRepository;

    @KafkaListener(topics = "word_count")
    public void onMassage(List<ConsumerRecord<String, String>> recordList) {

        // 处理消息
        for (ConsumerRecord<String, String> record : recordList) {

            CustomMessage msg = CustomMessage.getFromJson(record.value());
            if (msg == null) {
                log.error("Message de json fail on " + record.key());
                continue;
            }

            List<Integer> l = Arrays.stream(record.key().split("-")).map(Integer::parseInt).collect(Collectors.toList());
            int fileUID = l.get(0), chunkId = l.get(1), partition = l.get(2), partitionNum = l.get(3);

//            // 处理每个 Message 进行计数统计
//            if (!fileMap.containsKey(fileUID))
//                fileMap.put(fileUID, new HashMap<>(512));
//            HashMap<String, Integer> wordCntMap = fileMap.get(fileUID);
//            msg.getWordCountList().forEach(wordCount -> {
//                String word = wordCount.getWord();
//                int count = wordCount.getCount();
//                if (!wordCntMap.containsKey(word))
//                    wordCntMap.put(word, count);
//                else {
//                    int tmp = wordCntMap.get(word);
//                    wordCntMap.put(word, count + tmp);
//                }
//            });
        }

//        // 消息录入
//        fileMap.forEach((fileUID, wcMap) -> {
//            try {
//                saveWordCountRepository.saveFromWordCountMap(fileUID, partition, wcMap);
//            } catch (Exception exception) {
//                log.error("Transactional redo for " + exception + " when process " + fileUID + "-" + partition);
//            }
//        });
    }

}
