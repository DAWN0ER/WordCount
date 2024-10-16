package priv.dawn.wordcount.pojo.enums;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: Dawn Yang
 * @Since: 2024/10/16/20:12
 */

public enum FileInfoStatusEnums {

    INIT(1,"文件初始化"),
    STORED(2,"文件已被储存"),
    DAMAGED(3,"文件有分区损坏")
    ;

    private final Integer status;
    private final String description;

    FileInfoStatusEnums(Integer status, String description){
        this.status = status;
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }
}
