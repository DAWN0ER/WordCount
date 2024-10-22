package priv.dawn.databaseproxy.dao.domain;

public class WordCount {
    private Long id;

    private Integer fileUid;

    private String word;

    private Integer cnt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getFileUid() {
        return fileUid;
    }

    public void setFileUid(Integer fileUid) {
        this.fileUid = fileUid;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word == null ? null : word.trim();
    }

    public Integer getCnt() {
        return cnt;
    }

    public void setCnt(Integer cnt) {
        this.cnt = cnt;
    }
}