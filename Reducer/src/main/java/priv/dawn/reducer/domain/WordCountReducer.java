package priv.dawn.reducer.domain;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import priv.dawn.kafkamessage.message.CustomMessage;

import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
public class WordCountReducer {

    @KafkaListener(topics = "word_count")
    public void onMassage(List<ConsumerRecord<String, String>> recordList) {

        HashMap<Integer, HashMap<String, Integer>> fileMap = new HashMap<>(8); //同一批次应该不会超过6个fileUID

        for (ConsumerRecord<String, String> record : recordList) {

            int fileUID = Integer.parseInt(record.key());
            CustomMessage msg = CustomMessage.getFromJson(record.value());
            if (msg == null) {
                log.error("Message de json fail on " + record.toString());
                continue;
            }

            // 处理每个Message 进行计数统计
            if (!fileMap.containsKey(fileUID))
                fileMap.put(fileUID, new HashMap<String, Integer>(512));
            HashMap<String, Integer> wordCntMap = fileMap.get(fileUID);
            msg.getWordCountList().forEach(wordCount->{
                String word = wordCount.getWord();
                int count = wordCount.getCount();
                if(!wordCntMap.containsKey(word))
                    wordCntMap.put(word,count);
                else{
                    int tmp = wordCntMap.get(word);
                    wordCntMap.put(word,count+tmp);
                }
            });
        }



    }

}
