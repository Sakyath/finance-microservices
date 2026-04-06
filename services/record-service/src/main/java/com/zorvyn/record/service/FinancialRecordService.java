package com.zorvyn.record.service;

import com.zorvyn.common.exception.ResourceNotFoundException;
import com.zorvyn.common.model.enums.TransactionType;
import com.zorvyn.common.util.PageRequestUtil;
import com.zorvyn.record.dto.FinancialRecordRequest;
import com.zorvyn.record.dto.FinancialRecordResponse;
import com.zorvyn.record.entity.FinancialRecord;
import com.zorvyn.record.repository.FinancialRecordRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class FinancialRecordService {

    private final FinancialRecordRepository repository;

    public FinancialRecordService(FinancialRecordRepository repository) {
        this.repository = repository;
    }

    public FinancialRecordResponse create(FinancialRecordRequest request) {
        FinancialRecord record = new FinancialRecord();
        record.setAmount(request.getAmount());
        record.setType(request.getType());
        record.setCategory(request.getCategory());
        record.setDate(request.getDate());
        record.setNotes(request.getNotes());
        record.setCreatedBy(currentUsername());
        record.setDeleted(false);
        return FinancialRecordResponse.from(repository.save(record));
    }

    public FinancialRecordResponse getById(Long id) {
        return FinancialRecordResponse.from(findActive(id));
    }

    public Page<FinancialRecordResponse> getAll(int page, int size, TransactionType type,
                                                 String category, LocalDate from, LocalDate to) {
        Pageable pageable = PageRequest.of(PageRequestUtil.validPage(page), PageRequestUtil.validSize(size), Sort.by("date").descending());
        Page<FinancialRecord> records;

        if (type != null && category != null) {
            records = repository.findByTypeAndCategoryIgnoreCaseAndDeletedFalse(type, category, pageable);
        } else if (type != null) {
            records = repository.findByTypeAndDeletedFalse(type, pageable);
        } else if (category != null) {
            records = repository.findByCategoryIgnoreCaseAndDeletedFalse(category, pageable);
        } else if (from != null && to != null) {
            records = repository.findByDateBetweenAndDeletedFalse(from, to, pageable);
        } else {
            records = repository.findByDeletedFalse(pageable);
        }

        return records.map(FinancialRecordResponse::from);
    }

    public FinancialRecordResponse update(Long id, FinancialRecordRequest request) {
        FinancialRecord record = findActive(id);
        record.setAmount(request.getAmount());
        record.setType(request.getType());
        record.setCategory(request.getCategory());
        record.setDate(request.getDate());
        record.setNotes(request.getNotes());
        return FinancialRecordResponse.from(repository.save(record));
    }

    public void softDelete(Long id) {
        FinancialRecord record = findActive(id);
        record.setDeleted(true);
        repository.save(record);
    }

    private FinancialRecord findActive(Long id) {
        return repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found: " + id));
    }

    private String currentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
