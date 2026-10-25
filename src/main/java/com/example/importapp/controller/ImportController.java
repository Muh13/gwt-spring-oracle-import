package com.example.importapp.controller;

import com.example.importapp.entity.ImportBatchStatus;
import com.example.importapp.service.ExcelImportService;
import com.example.importapp.service.FileStorageService;
import com.example.importapp.service.ImportLockService;
import com.example.importapp.service.ProgressTracker;
import com.example.importapp.shared.ImportStatusDto;
import com.example.importapp.shared.ImportUploadResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.InetAddress;
import java.util.Date;
import java.util.Optional;

@RestController
public class ImportController {

    private static final Logger log = LoggerFactory.getLogger(ImportController.class);

    private final FileStorageService fileStorageService;
    private final ExcelImportService excelImportService;
    private final ImportLockService importLockService;
    private final ProgressTracker progressTracker;

    @Value("${app.import.refresh-ms:2000}")
    private long refreshMs;

    @Autowired
    public ImportController(FileStorageService fileStorageService,
                           ExcelImportService excelImportService,
                           ImportLockService importLockService,
                           ProgressTracker progressTracker) {
        this.fileStorageService = fileStorageService;
        this.excelImportService = excelImportService;
        this.importLockService = importLockService;
        this.progressTracker = progressTracker;
    }

    @PostMapping("/api/import/upload")
    public ResponseEntity<ImportUploadResponse> upload(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            String serverName = InetAddress.getLocalHost().getHostName();
            String userName = System.getProperty("user.name", "unknown");

            ImportBatchStatus saved = fileStorageService.saveUploadedFile(file, serverName, userName);
            return ResponseEntity.ok(new ImportUploadResponse(saved.getId(), saved.getFileName(), saved.getFileSize(), serverName, userName, saved.getStatus(), refreshMs));
        } catch (Exception e) {
            log.error("Upload failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/api/import/{id}/approve")
    public ResponseEntity<ImportStatusDto> approve(@PathVariable Long id) {
        Optional<ImportBatchStatus> status = importLockService.getStatus(id);
        if (!status.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        ImportBatchStatus batchStatus = status.get();
        batchStatus.setStatus("APPROVED");
        batchStatus.setUpdatedAt(new Date());
        importLockService.saveStatus(batchStatus);
        return ResponseEntity.ok(toDto(batchStatus));
    }

    @PostMapping("/api/import/{id}/cancel")
    public ResponseEntity<ImportStatusDto> cancel(@PathVariable Long id) {
        Optional<ImportBatchStatus> status = importLockService.getStatus(id);
        if (!status.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        ImportBatchStatus batchStatus = status.get();
        batchStatus.setStatus("CANCELLED");
        batchStatus.setUpdatedAt(new Date());
        importLockService.saveStatus(batchStatus);
        return ResponseEntity.ok(toDto(batchStatus));
    }

    @PostMapping("/api/import/{id}/run")
    public ResponseEntity<ImportStatusDto> run(@PathVariable Long id) {
        Optional<ImportBatchStatus> status = importLockService.getStatus(id);
        if (!status.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        ImportBatchStatus batchStatus = status.get();
        String serverName = batchStatus.getServerName();
        String owner = batchStatus.getUserName();
        boolean acquired = importLockService.acquire(serverName, owner);
        if (!acquired) {
            batchStatus.setStatus("FAILED");
            batchStatus.setErrorMessage("Another import is already running on this lock");
            importLockService.saveStatus(batchStatus);
            return ResponseEntity.status(HttpStatus.LOCKED).body(toDto(batchStatus));
        }

        try {
            File sourceFile = new File(batchStatus.getFilePath());
            excelImportService.processFile(id, sourceFile);
            return ResponseEntity.ok(toDto(importLockService.getStatus(id).orElse(batchStatus)));
        } catch (Exception e) {
            log.error("Import processing failed for id={}:", id, e);
            batchStatus.setStatus("FAILED");
            batchStatus.setErrorMessage(e.getMessage());
            batchStatus.setUpdatedAt(new Date());
            importLockService.saveStatus(batchStatus);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(toDto(batchStatus));
        } finally {
            importLockService.release(owner, serverName);
        }
    }

    @GetMapping("/api/import/{id}/status")
    public ResponseEntity<ImportStatusDto> status(@PathVariable Long id) {
        Optional<ImportBatchStatus> status = importLockService.getStatus(id);
        if (!status.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(toDto(status.get()));
    }

    @GetMapping("/api/import/lock")
    public ResponseEntity<String> lockState() {
        return ResponseEntity.ok(importLockService.currentOwner().orElse("UNLOCKED"));
    }

    private ImportStatusDto toDto(ImportBatchStatus batchStatus) {
        ImportStatusDto dto = new ImportStatusDto();
        dto.setId(batchStatus.getId());
        dto.setFileName(batchStatus.getFileName());
        dto.setFileSize(batchStatus.getFileSize());
        dto.setStatus(batchStatus.getStatus());
        dto.setServerName(batchStatus.getServerName());
        dto.setUserName(batchStatus.getUserName());
        dto.setLockOwner(batchStatus.getLockOwner());
        dto.setRowsRead(batchStatus.getRowsRead());
        dto.setRowsInserted(batchStatus.getRowsInserted());
        dto.setStartedAt(batchStatus.getStartedAt());
        dto.setEndedAt(batchStatus.getEndedAt());
        dto.setErrorMessage(batchStatus.getErrorMessage());
        dto.setRefreshMs(refreshMs);
        return dto;
    }
}
