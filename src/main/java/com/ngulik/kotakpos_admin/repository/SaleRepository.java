package com.ngulik.kotakpos_admin.repository;

import com.ngulik.kotakpos_admin.entity.Sale;
import com.ngulik.kotakpos_admin.enums.TransactionsStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    @Query("SELECT s FROM Sale s WHERE " +
            "(:startDate IS NULL OR s.createdAt >= :startDate) AND " +
            "(:endDate IS NULL OR s.createdAt <= :endDate) AND " +
            "(:invoiceNumber IS NULL OR s.invoiceNumber LIKE %:invoiceNumber%) AND " +
            "(:status IS NULL OR s.status = :status)")
    Page<Sale> searchSales(@Param("startDate") LocalDateTime startDate,
                           @Param("endDate") LocalDateTime endDate,
                           @Param("invoiceNumber") String invoiceNumber,
                           @Param("status") TransactionsStatus status,
                           Pageable pageable);

    @Query("SELECT COALESCE(MAX(p.id), 0) FROM Sale p")
    Long findLatestId();

    @Query("SELECT COALESCE(SUM(s.totalAmount), 0) FROM Sale s WHERE s.createdAt >= :date")
    BigDecimal findTodaysSales(@Param("date") LocalDateTime date);

    @Query("SELECT new map(cast(s.createdAt as date) as saleDate, sum(s.totalAmount) as total) " +
            "FROM Sale s WHERE s.createdAt >= :startDate " +
            "GROUP BY cast(s.createdAt as date)")
    List<Map<String, Object>> findLast7DaysSales(@Param("startDate") LocalDateTime startDate);

    long countByStatus(TransactionsStatus status);

    List<Sale> findByCreatedAtBetweenAndStatus(LocalDateTime startDate, LocalDateTime endDate, TransactionsStatus status);
}
