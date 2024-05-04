package priv.dawn.reducer.domain;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class WordCountReducer {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @KafkaListener(topics = "word_count")
    public void onMassage(ConsumerRecord<?, ?> record){
        logger.info(record.toString());
    }

}
