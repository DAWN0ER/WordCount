package priv.dawn.reducer.domain;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import priv.dawn.kafkamessage.message.CustomMessage;

@Component
public class WordCountReducer {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @KafkaListener(topics = "word_count")
    public void onMassage(ConsumerRecord<String,String> record){
        logger.info(record.toString());
        CustomMessage msg = CustomMessage.getFromJson(record.value());
        if(msg!=null) logger.info(msg.toString());
        else logger.error("Message de json fail");
    }

}
