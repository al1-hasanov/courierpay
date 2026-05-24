package com.alihasanov.courierpay.service;

import com.alihasanov.courierpay.entity.Transaction;
import com.alihasanov.courierpay.enums.TransactionType;
import com.alihasanov.courierpay.exception.InternalServerException;
import com.alihasanov.courierpay.repository.TransactionRepository;
import java.io.ByteArrayOutputStream;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import static com.alihasanov.courierpay.exception.CourierPayErrorResponse.EXPORT_TRANSACTIONS_FAILED;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final TransactionRepository transactionRepository;

    public byte[] exportTransactions(Long courierId, TransactionType type, Instant createdFrom, Instant createdTo) {
        try (var workbook = new XSSFWorkbook(); var out = new ByteArrayOutputStream()) {
            var sheet = workbook.createSheet("Transactions");
            var header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Courier ID");
            header.createCell(2).setCellValue("Type");
            header.createCell(3).setCellValue("Amount");
            header.createCell(4).setCellValue("Created At");
            var transactions = transactionRepository.findAll(
                    buildSpecification(courierId, type, createdFrom, createdTo),
                    Sort.by(Sort.Direction.DESC, "createdAt")
            );
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
            throw new InternalServerException(EXPORT_TRANSACTIONS_FAILED, e);
        }
    }

    private Specification<Transaction> buildSpecification(Long courierId, TransactionType type, Instant createdFrom, Instant createdTo) {
        return (root, query, criteriaBuilder) -> {
            var predicate = criteriaBuilder.conjunction();
            if (courierId != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("courier").get("id"), courierId));
            }
            if (type != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("type"), type));
            }
            if (createdFrom != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.<Instant>get("createdAt"), createdFrom));
            }
            if (createdTo != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThanOrEqualTo(root.<Instant>get("createdAt"), createdTo));
            }
            return predicate;
        };
    }
}
