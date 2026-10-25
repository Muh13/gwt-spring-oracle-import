package com.example.importapp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "IMPORT_LOCK")
public class ImportLock implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "LOCK_KEY")
    private String lockKey;

    @Column(name = "OWNER_NAME")
    private String ownerName;

    @Column(name = "SERVER_NAME")
    private String serverName;

    @Column(name = "LOCKED_AT")
    private Date lockedAt;

    @Column(name = "LEASE_UNTIL")
    private Date leaseUntil;

    @Column(name = "UPDATED_AT")
    private Date updatedAt;

    @Column(name = "CREATED_AT")
    private Date createdAt;

    public ImportLock() {
    }

    public String getLockKey() {
        return lockKey;
    }

    public void setLockKey(String lockKey) {
        this.lockKey = lockKey;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public Date getLockedAt() {
        return lockedAt;
    }

    public void setLockedAt(Date lockedAt) {
        this.lockedAt = lockedAt;
    }

    public Date getLeaseUntil() {
        return leaseUntil;
    }

    public void setLeaseUntil(Date leaseUntil) {
        this.leaseUntil = leaseUntil;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
