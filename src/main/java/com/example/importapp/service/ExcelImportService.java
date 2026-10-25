package com.example.importapp.service;

import com.example.importapp.entity.ImportBatchStatus;
import com.example.importapp.entity.ImportStagingRecord;
import com.example.importapp.repository.ImportBatchStatusRepository;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ExcelImportService {

    private static final Logger log = LoggerFactory.getLogger(ExcelImportService.class);

    private final EntityManagerFactory entityManagerFactory;
    private final ImportBatchStatusRepository batchStatusRepository;
    private final ProgressTracker progressTracker;

    @Value("${app.import.storage-dir:/var/imports}")
    private String storageDir;

    @Autowired
    public ExcelImportService(EntityManagerFactory entityManagerFactory,
                             ImportBatchStatusRepository batchStatusRepository,
                             ProgressTracker progressTracker) {
        this.entityManagerFactory = entityManagerFactory;
        this.batchStatusRepository = batchStatusRepository;
        this.progressTracker = progressTracker;
    }

    @PostConstruct
    public void initStorageDir() {
        File dir = new File(storageDir);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created) {
                log.warn("Could not create import storage directory: {}", storageDir);
            }
        }
    }

    @Transactional
    public ImportBatchStatus processFile(Long batchId, File file) throws Exception {
        ImportBatchStatus status = batchStatusRepository.findById(batchId).orElseThrow(() -> new IllegalArgumentException("Batch not found: " + batchId));
        status.setStatus("PROCESSING");
        status.setStartedAt(new java.util.Date());
        status.setUpdatedAt(new java.util.Date());
        batchStatusRepository.save(status);

        progressTracker.update(batchId, 0L, 0L, "PROCESSING");

        final AtomicLong rowsRead = new AtomicLong(0);
        final AtomicLong rowsInserted = new AtomicLong(0);
        final DataFormatter formatter = new DataFormatter();

        try (OPCPackage pkg = OPCPackage.open(file.getAbsolutePath())) {
            XSSFReader reader = new XSSFReader(pkg);
            StylesTable styles = reader.getStylesTable();
            ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(pkg);
            XSSFReader.SheetIterator iter = (XSSFReader.SheetIterator) reader.getSheetsData();

            while (iter.hasNext()) {
                try (InputStream sheetStream = iter.next()) {
                    String sheetName = iter.getSheetName();
                    XSSFSheetXMLHandler handler = new XSSFSheetXMLHandler(styles, strings, new XSSFSheetXMLHandler.SheetContentsHandler() {
                        private java.util.List<String> currentRow = new java.util.ArrayList<String>();

                        @Override
                        public void startRow(int rowNum) {
                            currentRow.clear();
                        }

                        @Override
                        public void endRow(int rowNum) {
                            if (currentRow.isEmpty()) {
                                return;
                            }
                            rowsRead.incrementAndGet();
                            String raw = String.join("|", currentRow);
                            persistStagingRecord(status.getId(), sheetName, rowNum, raw);
                            rowsInserted.incrementAndGet();
                            progressTracker.update(status.getId(), rowsRead.get(), rowsInserted.get(), "PROCESSING");
                        }

                        @Override
                        public void cell(String cellReference, String formattedValue, XSSFComment comment) {
                            if (formattedValue == null) {
                                currentRow.add("");
                            } else {
                                currentRow.add(formatter.formatCellValue(formattedValue));
                            }
                        }

                        @Override
                        public void headerFooter(String text, String text1, boolean b, String s) {
                        }
                    }, false);

                    javax.xml.parsers.SAXParserFactory factory = javax.xml.parsers.SAXParserFactory.newInstance();
                    factory.setNamespaceAware(true);
                    javax.xml.parsers.SAXParser parser = factory.newSAXParser();
                    org.xml.sax.XMLReader xmlReader = parser.getXMLReader();
                    xmlReader.setContentHandler(handler);
                    xmlReader.parse(new org.xml.sax.InputSource(sheetStream));
                }
            }
        } catch (Exception e) {
            status.setStatus("FAILED");
            status.setErrorMessage(e.getMessage());
            status.setEndedAt(new java.util.Date());
            status.setUpdatedAt(new java.util.Date());
            batchStatusRepository.save(status);
            progressTracker.update(batchId, rowsRead.get(), rowsInserted.get(), "FAILED");
            throw e;
        }

        status.setStatus("COMPLETED");
        status.setRowsRead(rowsRead.get());
        status.setRowsInserted(rowsInserted.get());
        status.setEndedAt(new java.util.Date());
        status.setUpdatedAt(new java.util.Date());
        batchStatusRepository.save(status);
        progressTracker.update(batchId, rowsRead.get(), rowsInserted.get(), "COMPLETED");
        return status;
    }

    private void persistStagingRecord(Long batchId, String sheetName, int rowNumber, String rawRow) {
        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        Transaction tx = null;
        StatelessSession session = sessionFactory.openStatelessSession();

        try {
            tx = session.beginTransaction();
            ImportStagingRecord record = new ImportStagingRecord(batchId, sheetName, Long.valueOf(rowNumber), rawRow);
            session.insert(record);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        } finally {
            session.close();
        }
    }

    public File locateUploadedFile(String fileName) {
        return new File(storageDir, fileName);
    }
}
