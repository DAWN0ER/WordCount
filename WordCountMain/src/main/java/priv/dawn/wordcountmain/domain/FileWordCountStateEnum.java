package priv.dawn.wordcountmain.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum FileWordCountStateEnum {

    START_SUCCESS(250,"成功开始分词统计"),

    START_FAIL(450,"启动分词统计服务失败"),

    ON_PROCESS(550,"该文件正在分词统计处理中"),
    ALREADY_FINISHED(560,"该文件已经完成分词统计"),
    HAVE_NOT_PROCESSED(570,"该文件还未开始分词统计");

    private final int state;
    private final String Massage;


    FileWordCountStateEnum(int state, String massage) {
        this.state = state;
        Massage = massage;
    }
}
