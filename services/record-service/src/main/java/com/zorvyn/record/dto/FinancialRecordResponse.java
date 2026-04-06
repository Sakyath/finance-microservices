package com.zorvyn.record.dto;

import com.zorvyn.common.model.enums.TransactionType;
import com.zorvyn.record.entity.FinancialRecord;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class FinancialRecordResponse {
    private Long id;
    private BigDecimal amount;
    private TransactionType type;
    private String category;
    private LocalDate date;
    private String notes;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static FinancialRecordResponse from(FinancialRecord r) {
        FinancialRecordResponse dto = new FinancialRecordResponse();
        dto.id = r.getId();
        dto.amount = r.getAmount();
        dto.type = r.getType();
        dto.category = r.getCategory();
        dto.date = r.getDate();
        dto.notes = r.getNotes();
        dto.createdBy = r.getCreatedBy();
        dto.createdAt = r.getCreatedAt();
        dto.updatedAt = r.getUpdatedAt();
        return dto;
    }

    public Long getId() { return id; }
    public BigDecimal getAmount() { return amount; }
    public TransactionType getType() { return type; }
    public String getCategory() { return category; }
    public LocalDate getDate() { return date; }
    public String getNotes() { return notes; }
    public String getCreatedBy() { return createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
