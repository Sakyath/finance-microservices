package com.zorvyn.record.repository;

import com.zorvyn.common.model.enums.TransactionType;
import com.zorvyn.record.entity.FinancialRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long> {

    Optional<FinancialRecord> findByIdAndDeletedFalse(Long id);
    Page<FinancialRecord> findByDeletedFalse(Pageable pageable);
    Page<FinancialRecord> findByTypeAndDeletedFalse(TransactionType type, Pageable pageable);
    Page<FinancialRecord> findByCategoryIgnoreCaseAndDeletedFalse(String category, Pageable pageable);
    Page<FinancialRecord> findByDateBetweenAndDeletedFalse(LocalDate from, LocalDate to, Pageable pageable);
    Page<FinancialRecord> findByTypeAndCategoryIgnoreCaseAndDeletedFalse(TransactionType type, String category, Pageable pageable);

    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM FinancialRecord r WHERE r.type = :type AND r.deleted = false")
    BigDecimal sumByType(@Param("type") TransactionType type);

    @Query("SELECT r.category, SUM(r.amount) FROM FinancialRecord r WHERE r.deleted = false GROUP BY r.category")
    List<Object[]> sumByCategory();

    @Query("SELECT r.category, SUM(r.amount) FROM FinancialRecord r WHERE r.type = :type AND r.deleted = false GROUP BY r.category")
    List<Object[]> sumByCategoryAndType(@Param("type") TransactionType type);

    @Query("SELECT MONTH(r.date), YEAR(r.date), r.type, SUM(r.amount) FROM FinancialRecord r " +
           "WHERE r.deleted = false AND r.date >= :from GROUP BY YEAR(r.date), MONTH(r.date), r.type ORDER BY YEAR(r.date), MONTH(r.date)")
    List<Object[]> monthlyTrends(@Param("from") LocalDate from);

    List<FinancialRecord> findTop10ByDeletedFalseOrderByCreatedAtDesc();
}
