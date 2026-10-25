package com.example.importapp.repository;

import com.example.importapp.entity.ImportLock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImportLockRepository extends JpaRepository<ImportLock, String> {
}
