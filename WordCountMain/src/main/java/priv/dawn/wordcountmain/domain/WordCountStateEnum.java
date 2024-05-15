package priv.dawn.wordcountmain.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum WordCountStateEnum {

    START_SUCCESS(250,"成功开始分词统计"),
    START_FAIL(450,"创建任务失败"),

    FILE_NOT_FOUNT(440,"找不到文件");

    private final int state;
    private final String Massage;

    WordCountStateEnum(int state, String massage) {
        this.state = state;
        Massage = massage;
    }


}
