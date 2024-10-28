package priv.dawn.workers.mq.enums;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: Dawn Yang
 * @Since: 2024/10/21/11:51
 */
public enum TopicEnums {
    WORD_COUNT("word_count"),
    ;

    private final String topic;

    TopicEnums(String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }
}
