package com.example.importapp.repository;

import com.example.importapp.entity.ImportBatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImportBatchStatusRepository extends JpaRepository<ImportBatchStatus, Long> {
}
