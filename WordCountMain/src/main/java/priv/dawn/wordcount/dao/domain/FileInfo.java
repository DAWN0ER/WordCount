package priv.dawn.wordcount.dao.domain;

import java.util.Date;

public class FileInfo {
    private Long id;

    private Integer fileUid;

    private String fileName;

    private Integer chunkNum;

    private Date createdTime;

    private Date updatedTime;

    private Byte status;

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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName == null ? null : fileName.trim();
    }

    public Integer getChunkNum() {
        return chunkNum;
    }

    public void setChunkNum(Integer chunkNum) {
        this.chunkNum = chunkNum;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }
}