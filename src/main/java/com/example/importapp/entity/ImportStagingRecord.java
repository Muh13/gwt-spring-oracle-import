package com.example.importapp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "IMPORT_STAGING")
public class ImportStagingRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "BATCH_ID", nullable = false)
    private Long batchId;

    @Column(name = "SHEET_NAME")
    private String sheetName;

    @Column(name = "ROW_NUMBER")
    private Long rowNumber;

    @Lob
    @Column(name = "RAW_ROW")
    private String rawRow;

    @Column(name = "CREATED_AT")
    private Date createdAt = new Date();

    public ImportStagingRecord() {
    }

    public ImportStagingRecord(Long batchId, String sheetName, Long rowNumber, String rawRow) {
        this.batchId = batchId;
        this.sheetName = sheetName;
        this.rowNumber = rowNumber;
        this.rawRow = rawRow;
        this.createdAt = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public Long getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(Long rowNumber) {
        this.rowNumber = rowNumber;
    }

    public String getRawRow() {
        return rawRow;
    }

    public void setRawRow(String rawRow) {
        this.rawRow = rawRow;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
