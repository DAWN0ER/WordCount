package priv.dawn.reducer.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import priv.dawn.kafkamessage.message.CustomMessage;
import priv.dawn.reducer.mapper.MsgOrderMapper;
import priv.dawn.reducer.repository.SaveWordCountRepository;

import java.util.Arrays;

@Component
@Slf4j
public class WordCountReducer extends BaseConsumer<ConsumerRecord<String,String>> {

    @Autowired
    SaveWordCountRepository saveWordCountRepository;
    @Autowired
    MsgOrderMapper msgOrderMapper;


    @KafkaListener(topics = "word_count")
    public void onMassage(ConsumerRecord<String, String> record, Acknowledgment ack) {
        // 处理消息
        doConsumer(record);
        ack.acknowledge();
    }

    @Override
    protected boolean createOrder(ConsumerRecord<String, String> record) {
        try {
            int[] args = Arrays.stream(record.key().split("-"))
                    .mapToInt(Integer::parseInt)
                    .toArray();
            msgOrderMapper.saveNewOrder(args[0],args[1],args[2],args[3]);
            return true;
        } catch (DuplicateKeyException e) {
            return false;
        } catch (Exception e){
            log.error("Something wrong on key "+record.key());
            return false;
        }
    }

    @Override
    protected void consumeMessage(ConsumerRecord<String, String> record) {
        CustomMessage msg = CustomMessage.getFromJson(record.value());
        if (msg == null) {
            log.error("Deserialize json fail on " + record.key());
            return;
        }
        int[] args = Arrays.stream(record.key().split("-"))
                .mapToInt(Integer::parseInt)
                .toArray();
        int fileUID = args[0],partition=args[2];
        try {
            saveWordCountRepository.saveFromMsg(fileUID,partition,msg);
        } catch (Exception e) {
            log.error("Failure of data persistence on "+record.key());
        }
    }
}
