package priv.dawn.workers.mq.service;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.common.PartitionInfo;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import priv.dawn.mq.domain.WordCountMessage;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: Dawn Yang
 * @Since: 2024/10/21/11:12
 */

@Slf4j
@Service
public class KafkaWrapperService {

    private final Gson gson = new Gson();

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    public int getPartitionNum(String topic) {
        try {
            List<PartitionInfo> list = kafkaTemplate.partitionsFor(topic);
            return list.size();
        } catch (Exception e) {
            log.error("[getPartitionNum] 获取 Topic:{} 分区信息失败", topic, e);
            return 0;
        }
    }

    public void send(String topic, WordCountMessage message, int partition) {
        if (Objects.isNull(message) || StringUtils.isBlank(topic)) {
            log.error("参数异常, topic:{}, message:{}, partition:{}", topic, gson.toJson(message), partition);
            return;
        }
        Long taskId = message.getTaskId();
        String key = String.valueOf(taskId);
        String data = gson.toJson(message);
        log.info("将发送消息到 Topic:{}, partition:{}, key:{},data:{}",topic,partition,key,data);
        // kafka 默认的分区策略和粘性策略和 key 有关, key 也和事务有关, 如果需要分布式事务的一致性, 可以考虑用这个 key
        // 默认采用 taskId 来作为 key
        try {
            kafkaTemplate.send(topic, partition, key, data).addCallback(
                    success -> {
                        if(Objects.isNull(success)) return;
                        String sendTopic = success.getRecordMetadata().topic();
                        int sendPartition = success.getRecordMetadata().partition();
                        long offset = success.getRecordMetadata().offset();
                        log.info("[SUCCESS] 成功发送消息 Topic:{}, partition:{}, offset:{}",sendTopic,sendPartition,offset);
                    },
                    fail -> {
                        log.error("[FAIL] 发送消息失败:{}",fail.getMessage());
                    }

            );
        } catch (Exception e) {
            log.error("[kafkaTemplate] 发送消息异常:", e);
        }
    }

}
