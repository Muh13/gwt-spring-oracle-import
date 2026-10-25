package com.example.importapp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "IMPORT_BATCH_STATUS")
public class ImportBatchStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "FILE_NAME", nullable = false)
    private String fileName;

    @Column(name = "FILE_PATH", nullable = false)
    private String filePath;

    @Column(name = "FILE_SIZE")
    private Long fileSize;

    @Column(name = "STATUS")
    private String status = "READY";

    @Column(name = "SERVER_NAME")
    private String serverName;

    @Column(name = "USER_NAME")
    private String userName;

    @Column(name = "LOCK_OWNER")
    private String lockOwner;

    @Column(name = "ROWS_READ")
    private Long rowsRead = 0L;

    @Column(name = "ROWS_INSERTED")
    private Long rowsInserted = 0L;

    @Column(name = "STARTED_AT")
    private Date startedAt;

    @Column(name = "ENDED_AT")
    private Date endedAt;

    @Column(name = "ERROR_MESSAGE")
    private String errorMessage;

    @Column(name = "CREATED_AT")
    private Date createdAt = new Date();

    @Column(name = "UPDATED_AT")
    private Date updatedAt = new Date();

    public ImportBatchStatus() {
    }

    public ImportBatchStatus(String fileName, String filePath, Long fileSize, String serverName, String userName) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.serverName = serverName;
        this.userName = userName;
        this.status = "READY";
        this.createdAt = new Date();
        this.updatedAt = new Date();
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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getLockOwner() {
        return lockOwner;
    }

    public void setLockOwner(String lockOwner) {
        this.lockOwner = lockOwner;
    }

    public Long getRowsRead() {
        return rowsRead;
    }

    public void setRowsRead(Long rowsRead) {
        this.rowsRead = rowsRead;
    }

    public Long getRowsInserted() {
        return rowsInserted;
    }

    public void setRowsInserted(Long rowsInserted) {
        this.rowsInserted = rowsInserted;
    }

    public Date getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Date startedAt) {
        this.startedAt = startedAt;
    }

    public Date getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(Date endedAt) {
        this.endedAt = endedAt;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
