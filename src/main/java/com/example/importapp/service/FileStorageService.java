package com.example.importapp.service;

import com.example.importapp.entity.ImportBatchStatus;
import com.example.importapp.repository.ImportBatchStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final ImportBatchStatusRepository batchStatusRepository;
    private final String storageDir;

    @Autowired
    public FileStorageService(ImportBatchStatusRepository batchStatusRepository,
                              @org.springframework.beans.factory.annotation.Value("${app.import.storage-dir:/var/imports}") String storageDir) {
        this.batchStatusRepository = batchStatusRepository;
        this.storageDir = storageDir;
        File dir = new File(storageDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public ImportBatchStatus saveUploadedFile(org.springframework.web.multipart.MultipartFile file, String serverName, String userName) throws IOException {
        String originalName = file.getOriginalFilename();
        String safeName = UUID.randomUUID() + "_" + originalName;
        Path target = Paths.get(storageDir, safeName);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        ImportBatchStatus batch = new ImportBatchStatus(originalName, target.toString(), file.getSize(), serverName, userName);
        batch.setStatus("READY");
        return batchStatusRepository.save(batch);
    }

    public void deleteFile(String filePath) {
        if (filePath != null && !filePath.trim().isEmpty()) {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
        }
    }
}
