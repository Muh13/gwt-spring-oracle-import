package com.example.importapp.shared;

import java.io.Serializable;
import java.util.Date;

public class ImportUploadResponse implements Serializable {
    private Long id;
    private String fileName;
    private Long fileSize;
    private String serverName;
    private String userName;
    private String status;
    private long refreshMs;

    public ImportUploadResponse() {
    }

    public ImportUploadResponse(Long id, String fileName, Long fileSize, String serverName, String userName, String status, long refreshMs) {
        this.id = id;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.serverName = serverName;
        this.userName = userName;
        this.status = status;
        this.refreshMs = refreshMs;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getRefreshMs() {
        return refreshMs;
    }

    public void setRefreshMs(long refreshMs) {
        this.refreshMs = refreshMs;
    }
}
