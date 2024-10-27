package priv.dawn.wordcount.pojo.enums;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: Dawn Yang
 * @Since: 2024/10/27/17:05
 */
public enum WordCountTaskStatusEnums {
    NEW(1,"待完成新任务"),
    FINISHED(2,"任务已完成"),
    EXCEPTION(3,"任务异常")
    ;
    private final Integer status;
    private final String description;

    WordCountTaskStatusEnums(Integer status, String description){
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
