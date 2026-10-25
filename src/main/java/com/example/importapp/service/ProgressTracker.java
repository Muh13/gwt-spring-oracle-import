package com.example.importapp.service;

import com.example.importapp.entity.ImportBatchStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ProgressTracker {

    private static final Logger log = LoggerFactory.getLogger(ProgressTracker.class);

    private final Map<Long, ImportBatchStatus> progress = new ConcurrentHashMap<>();

    @Value("${app.import.refresh-ms:2000}")
    private long refreshMs;

    public void update(Long batchId, Long rowsRead, Long rowsInserted, String status) {
        ImportBatchStatus batch = progress.get(batchId);
        if (batch == null) {
            batch = new ImportBatchStatus();
            batch.setId(batchId);
            batch.setStatus(status);
            batch.setRowsRead(rowsRead);
            batch.setRowsInserted(rowsInserted);
            batch.setUpdatedAt(new Date());
            progress.put(batchId, batch);
            return;
        }

        batch.setRowsRead(rowsRead);
        batch.setRowsInserted(rowsInserted);
        batch.setStatus(status);
        batch.setUpdatedAt(new Date());
        progress.put(batchId, batch);
    }

    public ImportBatchStatus snapshot(Long batchId) {
        ImportBatchStatus status = progress.get(batchId);
        if (status == null) {
            return new ImportBatchStatus();
        }
        return status;
    }

    public long getRefreshMs() {
        return refreshMs;
    }
}
