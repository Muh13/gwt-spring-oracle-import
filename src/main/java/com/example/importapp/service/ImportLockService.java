package com.example.importapp.service;

import com.example.importapp.entity.ImportBatchStatus;
import com.example.importapp.repository.ImportBatchStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
public class ImportLockService {

    private final JdbcTemplate jdbcTemplate;
    private final ImportBatchStatusRepository statusRepository;

    @Value("${app.import.lock-key:EXCEL_IMPORT_LOCK}")
    private String lockKey;

    @Value("${app.import.lease-seconds:30}")
    private int leaseSeconds;

    @Autowired
    public ImportLockService(JdbcTemplate jdbcTemplate, ImportBatchStatusRepository statusRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.statusRepository = statusRepository;
    }

    @Transactional
    public boolean acquire(String serverName, String ownerName) {
        String leaseSql = "UPDATE IMPORT_LOCK " +
                "SET OWNER_NAME = ?, SERVER_NAME = ?, LOCKED_AT = SYSTIMESTAMP, LEASE_UNTIL = SYSTIMESTAMP + (? / 86400), UPDATED_AT = SYSTIMESTAMP " +
                "WHERE LOCK_KEY = ? AND (LEASE_UNTIL IS NULL OR LEASE_UNTIL < SYSTIMESTAMP)";

        int affected = jdbcTemplate.update(leaseSql, ownerName, serverName, leaseSeconds, lockKey);
        if (affected > 0) {
            return true;
        }

        String insertSql = "INSERT INTO IMPORT_LOCK (LOCK_KEY, OWNER_NAME, SERVER_NAME, LOCKED_AT, LEASE_UNTIL, UPDATED_AT, CREATED_AT) " +
                "VALUES (?, ?, ?, SYSTIMESTAMP, SYSTIMESTAMP + (? / 86400), SYSTIMESTAMP, SYSTIMESTAMP)";
        try {
            int insertResult = jdbcTemplate.update(insertSql, lockKey, ownerName, serverName, leaseSeconds);
            return insertResult > 0;
        } catch (Exception ignored) {
            return false;
        }
    }

    @Transactional
    public void release(String ownerName, String serverName) {
        jdbcTemplate.update(
                "UPDATE IMPORT_LOCK SET OWNER_NAME = NULL, SERVER_NAME = NULL, LEASE_UNTIL = SYSTIMESTAMP, UPDATED_AT = SYSTIMESTAMP WHERE LOCK_KEY = ? AND OWNER_NAME = ? AND SERVER_NAME = ?",
                lockKey, ownerName, serverName
        );
    }

    @Transactional(readOnly = true)
    public Optional<String> currentOwner() {
        String sql = "SELECT OWNER_NAME FROM IMPORT_LOCK WHERE LOCK_KEY = ?";
        String owner = jdbcTemplate.queryForObject(sql, new Object[]{lockKey}, String.class);
        return Optional.ofNullable(owner);
    }

    @Transactional(readOnly = true)
    public Optional<String> currentServer() {
        String sql = "SELECT SERVER_NAME FROM IMPORT_LOCK WHERE LOCK_KEY = ?";
        String server = jdbcTemplate.queryForObject(sql, new Object[]{lockKey}, String.class);
        return Optional.ofNullable(server);
    }

    @Transactional(readOnly = true)
    public boolean isLockedByCurrentServer(String serverName) {
        String sql = "SELECT COUNT(*) FROM IMPORT_LOCK WHERE LOCK_KEY = ? AND SERVER_NAME = ? AND LEASE_UNTIL > SYSTIMESTAMP";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{lockKey, serverName}, Integer.class);
        return count != null && count > 0;
    }

    public ImportBatchStatus saveStatus(ImportBatchStatus status) {
        return statusRepository.save(status);
    }

    public Optional<ImportBatchStatus> getStatus(Long id) {
        return statusRepository.findById(id);
    }

    public Date currentLeaseUntil() {
        String sql = "SELECT LEASE_UNTIL FROM IMPORT_LOCK WHERE LOCK_KEY = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{lockKey}, java.sql.Timestamp.class);
    }
}
