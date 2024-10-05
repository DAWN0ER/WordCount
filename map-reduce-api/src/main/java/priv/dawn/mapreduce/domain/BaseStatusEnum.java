package priv.dawn.mapreduce.domain;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: Dawn Yang
 * @Since: 2024/10/05/17:12
 */
public enum BaseStatusEnum {
    SUCCESS(0),
    FAIL(-1),
    EXCEPTION(-2),
    ;

    public final int code;

    BaseStatusEnum(int code) {
        this.code = code;
    }
}
