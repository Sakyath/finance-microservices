package com.zorvyn.dashboard.repository;

import com.zorvyn.common.model.enums.TransactionType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public class DashboardRepository {

    @PersistenceContext
    private EntityManager em;

    public BigDecimal sumByType(TransactionType type) {
        String jpql = "SELECT COALESCE(SUM(r.amount), 0) FROM FinancialRecord r WHERE r.type = :type AND r.deleted = false";
        return (BigDecimal) em.createQuery(jpql)
                .setParameter("type", type)
                .getSingleResult();
    }

    public List<Object[]> sumByCategory() {
        return em.createQuery(
                "SELECT r.category, SUM(r.amount) FROM FinancialRecord r WHERE r.deleted = false GROUP BY r.category",
                Object[].class).getResultList();
    }

    public List<Object[]> sumByCategoryAndType(TransactionType type) {
        return em.createQuery(
                "SELECT r.category, SUM(r.amount) FROM FinancialRecord r WHERE r.type = :type AND r.deleted = false GROUP BY r.category",
                Object[].class).setParameter("type", type).getResultList();
    }

    public List<Object[]> monthlyTrends(LocalDate from) {
        return em.createQuery(
                "SELECT MONTH(r.date), YEAR(r.date), r.type, SUM(r.amount) FROM FinancialRecord r " +
                "WHERE r.deleted = false AND r.date >= :from " +
                "GROUP BY YEAR(r.date), MONTH(r.date), r.type ORDER BY YEAR(r.date), MONTH(r.date)",
                Object[].class).setParameter("from", from).getResultList();
    }

    public List<Object[]> recentActivity() {
        return em.createQuery(
                "SELECT r.id, r.amount, r.type, r.category, r.date, r.createdBy FROM FinancialRecord r " +
                "WHERE r.deleted = false ORDER BY r.createdAt DESC",
                Object[].class).setMaxResults(10).getResultList();
    }
}
