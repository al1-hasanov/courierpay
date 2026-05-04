package com.alihasanov.courierpay.service;

import com.alihasanov.courierpay.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final TransactionRepository transactionRepository;

    public byte[] exportTransactions() {
        try (var workbook = new XSSFWorkbook(); var out = new ByteArrayOutputStream()) {
            var sheet = workbook.createSheet("Transactions");
            var header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Courier ID");
            header.createCell(2).setCellValue("Type");
            header.createCell(3).setCellValue("Amount");
            header.createCell(4).setCellValue("Created At");
            var transactions = transactionRepository.findAll();
            for (int i = 0; i < transactions.size(); i++) {
                var t = transactions.get(i);
                var row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(t.getId());
                row.createCell(1).setCellValue(t.getCourier().getId());
                row.createCell(2).setCellValue(t.getType().name());
                row.createCell(3).setCellValue(t.getAmount().doubleValue());
                row.createCell(4).setCellValue(t.getCreatedAt().toString());
            }
            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Could not export transactions", e);
        }
    }
}
