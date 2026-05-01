package com.kalana.customer_management_system.service;

import com.kalana.customer_management_system.entity.Customer;
import com.kalana.customer_management_system.repositry.CustomerRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelImportService {

    private static final int BATCH_SIZE = 500;

    public int importCustomers(
            MultipartFile file,
            CustomerRepository customerRepository
    ) throws IOException {

        int successCount = 0;
        int skippedCount = 0;
        int rowNumber = 0;

        System.out.println("=== BULK IMPORT STARTED ===");
        System.out.println("File: " + file.getOriginalFilename());
        System.out.println("Size: " + file.getSize() + " bytes");

        InputStream inputStream = file.getInputStream();

        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

            // Use a data formatter - this converts ANY cell type to string
            DataFormatter dataFormatter = new DataFormatter();

            System.out.println("Total rows: " + sheet.getPhysicalNumberOfRows());

            List<Customer> batch = new ArrayList<>();

            for (Row row : sheet) {
                rowNumber++;

                // Skip header row
                if (rowNumber == 1) {
                    System.out.println("Skipping header row");
                    continue;
                }

                // Skip empty rows
                if (isRowEmpty(row, dataFormatter)) {
                    System.out.println("Row " + rowNumber + ": Empty, skipping");
                    continue;
                }

                try {
                    Customer customer = mapRowToCustomer(row, rowNumber, dataFormatter);

                    if (customer == null) {
                        skippedCount++;
                        continue;
                    }

                    // Skip duplicate NIC
                    if (customerRepository.existsByNicNumber(customer.getNicNumber())) {
                        System.out.println("Row " + rowNumber
                                + ": Duplicate NIC skipped - "
                                + customer.getNicNumber());
                        skippedCount++;
                        continue;
                    }

                    batch.add(customer);

                    // Save in batches
                    if (batch.size() >= BATCH_SIZE) {
                        customerRepository.saveAll(batch);
                        successCount += batch.size();
                        System.out.println("Batch saved. Total so far: " + successCount);
                        batch.clear();
                    }

                } catch (Exception e) {
                    System.out.println("Row " + rowNumber + " ERROR: " + e.getMessage());
                    skippedCount++;
                }
            }

            // Save remaining records
            if (!batch.isEmpty()) {
                customerRepository.saveAll(batch);
                successCount += batch.size();
                System.out.println("Final batch saved: " + batch.size());
            }

        } catch (Exception e) {
            System.out.println("IMPORT FAILED: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Failed to process file: " + e.getMessage());
        }

        System.out.println("=== IMPORT COMPLETE ===");
        System.out.println("Success: " + successCount);
        System.out.println("Skipped: " + skippedCount);

        return successCount;
    }

    // ===================================================
    // Map a single Excel row to Customer entity
    // ===================================================
    private Customer mapRowToCustomer(
            Row row,
            int rowNumber,
            DataFormatter formatter
    ) {
        try {
            Customer customer = new Customer();

            // ===== Column A (0): NAME =====
            String name = getStringValue(row.getCell(0), formatter);
            if (name == null || name.trim().isEmpty()) {
                System.out.println("Row " + rowNumber + ": Name is empty");
                return null;
            }
            customer.setName(name.trim());

            // ===== Column B (1): DATE OF BIRTH =====
            LocalDate dob = parseDateFromCell(row.getCell(1), rowNumber, formatter);
            if (dob == null) {
                System.out.println("Row " + rowNumber + ": Date is invalid");
                return null;
            }
            customer.setDateOfBirth(dob);

            // ===== Column C (2): NIC NUMBER =====
            String nic = getStringValue(row.getCell(2), formatter);
            if (nic == null || nic.trim().isEmpty()) {
                System.out.println("Row " + rowNumber + ": NIC is empty");
                return null;
            }
            customer.setNicNumber(nic.trim());

            System.out.println("Row " + rowNumber
                    + ": OK -> Name=" + name
                    + " | DOB=" + dob
                    + " | NIC=" + nic);

            return customer;

        } catch (Exception e) {
            System.out.println("Row " + rowNumber + ": Error - " + e.getMessage());
            return null;
        }
    }

    // ===================================================
    // Get ANY cell value as String
    // DataFormatter handles ALL cell types automatically
    // ===================================================
    private String getStringValue(Cell cell, DataFormatter formatter) {
        if (cell == null) return null;

        // DataFormatter converts everything to string correctly
        // Numbers, Dates, Text, Formulas - all handled
        String value = formatter.formatCellValue(cell).trim();

        // If DataFormatter returns empty, try manual conversion
        if (value.isEmpty()) {
            switch (cell.getCellType()) {
                case NUMERIC:
                    // Handle large numbers (NIC as number)
                    double numVal = cell.getNumericCellValue();
                    // Use long to avoid scientific notation like 1.99E11
                    return String.valueOf((long) numVal);
                case STRING:
                    return cell.getStringCellValue().trim();
                case BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue());
                case FORMULA:
                    try {
                        return cell.getStringCellValue().trim();
                    } catch (Exception e) {
                        return String.valueOf((long) cell.getNumericCellValue());
                    }
                default:
                    return null;
            }
        }

        return value;
    }

    // ===================================================
    // Parse date from cell - handles all formats
    // ===================================================
    private LocalDate parseDateFromCell(
            Cell cell,
            int rowNumber,
            DataFormatter formatter
    ) {
        if (cell == null) return null;

        try {
            // Case 1: Excel date (numeric formatted as date)
            if (cell.getCellType() == CellType.NUMERIC
                    && DateUtil.isCellDateFormatted(cell)) {
                LocalDate date = cell.getLocalDateTimeCellValue().toLocalDate();
                System.out.println("Row " + rowNumber
                        + ": Date from Excel format: " + date);
                return date;
            }

            // Case 2: String date - try multiple formats
            String dateStr = getStringValue(cell, formatter);
            if (dateStr != null && !dateStr.isEmpty()) {
                return parseStringToDate(dateStr, rowNumber);
            }

            // Case 3: Numeric (Excel serial number)
            if (cell.getCellType() == CellType.NUMERIC) {
                try {
                    LocalDate date = DateUtil
                            .getLocalDateTime(cell.getNumericCellValue())
                            .toLocalDate();
                    System.out.println("Row " + rowNumber
                            + ": Date from serial: " + date);
                    return date;
                } catch (Exception e) {
                    System.out.println("Row " + rowNumber
                            + ": Cannot parse serial date");
                }
            }

        } catch (Exception e) {
            System.out.println("Row " + rowNumber
                    + ": Date error - " + e.getMessage());
        }

        return null;
    }

    // ===================================================
    // Try multiple date string formats
    // ===================================================
    private LocalDate parseStringToDate(String dateStr, int rowNumber) {
        // Clean up the date string
        dateStr = dateStr.trim();

        String[] formats = {
                "yyyy-MM-dd",
                "dd/MM/yyyy",
                "MM/dd/yyyy",
                "dd-MM-yyyy",
                "yyyy/MM/dd",
                "dd.MM.yyyy",
                "d/M/yyyy",
                "M/d/yyyy",
                "yyyy.MM.dd"
        };

        for (String format : formats) {
            try {
                LocalDate date = LocalDate.parse(
                        dateStr,
                        DateTimeFormatter.ofPattern(format)
                );
                System.out.println("Row " + rowNumber
                        + ": Date '" + dateStr
                        + "' parsed with: " + format);
                return date;
            } catch (DateTimeParseException e) {
                // Try next format
            }
        }

        System.out.println("Row " + rowNumber
                + ": Cannot parse date: '" + dateStr + "'");
        return null;
    }

    // ===================================================
    // Check if row is empty
    // ===================================================
    private boolean isRowEmpty(Row row, DataFormatter formatter) {
        if (row == null) return true;

        for (Cell cell : row) {
            if (cell != null) {
                String val = getStringValue(cell, formatter);
                if (val != null && !val.trim().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }
}