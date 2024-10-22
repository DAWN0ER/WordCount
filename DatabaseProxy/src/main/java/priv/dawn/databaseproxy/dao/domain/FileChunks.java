package priv.dawn.databaseproxy.dao.domain;

public class FileChunks {
    private Long id;

    private Integer fileUid;

    private Integer chunkId;

    private String context;

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

    public Integer getChunkId() {
        return chunkId;
    }

    public void setChunkId(Integer chunkId) {
        this.chunkId = chunkId;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context == null ? null : context.trim();
    }
}