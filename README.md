# Excel Import Application

This project is a single-module Maven application that demonstrates a GWT UI backed by Spring Boot, Oracle, Hibernate 5, Apache POI 5.2.x SAX parsing, and distributed lock logic using Oracle timestamps.

## Requirements
- Java 8
- Maven 3.x
- Oracle 12c+ with a reachable database
- GWT 2.8.2
- Apache POI 5.2.x

## Features
- Upload .xlsx files and save them to a shared directory
- Show file name, file size, and current server user lock owner
- Approve/cancel operation
- Process all Excel sheets using multiple worker threads with bounded queue and batch inserts into staging table
- Show progress updates every 2 seconds
- Use Oracle distributed lock with `SYSTIMESTAMP` lease handling
- Provide RTL Persian UI using GWT
- Store all runtime configuration in `application.properties`

## Build

```bash
mvn clean package
```

## Run

```bash
mvn spring-boot:run
```

Then open:

```text
http://localhost:8080/
```

## Database setup
Run the script below before starting the application:

```sql
src/main/resources/db/oracle/schema.sql
```

## Important notes
- Excel parsing uses SAX (`XSSFReader`, `XSSFSheetXMLHandler`) instead of `XSSFWorkbook`, `WorkbookFactory`, and `SXSSF`.
- The app avoids GWT-unsafe APIs and uses REST endpoints for data exchange.
- Because I cannot execute local JDK/Maven checks in this interface, I am not claiming an actual local build result here. Please validate locally before production use.
