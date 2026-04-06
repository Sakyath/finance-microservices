package com.zorvyn.dashboard.service;

import com.zorvyn.common.model.enums.TransactionType;
import com.zorvyn.common.util.DateUtil;
import com.zorvyn.dashboard.repository.DashboardRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final DashboardRepository repository;

    public DashboardService(DashboardRepository repository) {
        this.repository = repository;
    }

    public Map<String, Object> getSummary() {
        BigDecimal totalIncome = repository.sumByType(TransactionType.INCOME);
        BigDecimal totalExpense = repository.sumByType(TransactionType.EXPENSE);
        BigDecimal netBalance = totalIncome.subtract(totalExpense);

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("totalIncome", totalIncome);
        summary.put("totalExpenses", totalExpense);
        summary.put("netBalance", netBalance);
        summary.put("generatedAt", DateUtil.format(java.time.LocalDate.now()));
        return summary;
    }

    public Map<String, Object> getCategoryTotals() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("all", toMap(repository.sumByCategory()));
        result.put("income", toMap(repository.sumByCategoryAndType(TransactionType.INCOME)));
        result.put("expenses", toMap(repository.sumByCategoryAndType(TransactionType.EXPENSE)));
        return result;
    }

    public List<Map<String, Object>> getMonthlyTrends() {
        return repository.monthlyTrends(DateUtil.startOfMonth(6)).stream().map(row -> {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("month", row[0]);
            entry.put("year", row[1]);
            entry.put("type", row[2]);
            entry.put("total", row[3]);
            return entry;
        }).collect(Collectors.toList());
    }

    public List<Map<String, Object>> getRecentActivity() {
        return repository.recentActivity().stream().map(row -> {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("id", row[0]);
            entry.put("amount", row[1]);
            entry.put("type", row[2]);
            entry.put("category", row[3]);
            entry.put("date", row[4]);
            entry.put("createdBy", row[5]);
            return entry;
        }).collect(Collectors.toList());
    }

    private Map<String, BigDecimal> toMap(List<Object[]> rows) {
        Map<String, BigDecimal> map = new LinkedHashMap<>();
        for (Object[] row : rows) {
            map.put((String) row[0], (BigDecimal) row[1]);
        }
        return map;
    }
}
