package com.ngulik.kotakpos_admin.repository;

import com.ngulik.kotakpos_admin.entity.Purchase;
import com.ngulik.kotakpos_admin.enums.PurchaseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    @Query("SELECT p FROM Purchase p WHERE " +
            "(:keyword IS NULL OR " +
            "CAST(p.id AS string) LIKE CONCAT('%', :keyword, '%') OR " +
            "p.invoiceNumber LIKE CONCAT('%', :keyword, '%')) AND " +
            "(:status IS NULL OR p.status = :status)")
    Page<Purchase> findAll(
            @Param("keyword") String keyword,
            @Param("status") PurchaseStatus status,
            Pageable pageable
    );

    @Query("SELECT COALESCE(MAX(p.id), 0) FROM Purchase p")
    Long findLatestId();

    long countByStatus(PurchaseStatus status);
}
