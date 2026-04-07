package com.ngulik.kotakpos_admin.repository;

import com.ngulik.kotakpos_admin.entity.StockMovement;
import com.ngulik.kotakpos_admin.enums.ReferenceType;
import com.ngulik.kotakpos_admin.enums.StockMovementType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    List<StockMovement> findByProductId(Long productId);

    @Query("SELECT sm FROM StockMovement sm WHERE " +
            "(:type IS NULL OR sm.type = :type) AND " +
            "(:referenceType IS NULL OR sm.referenceType = :referenceType) AND " +
            "(:referenceId IS NULL OR sm.referenceId = :referenceId) AND " +
            "(:productId IS NULL OR sm.product.id = :productId)")
    Page<StockMovement> search(
            @Param("type") StockMovementType type,
            @Param("referenceType") ReferenceType referenceType,
            @Param("referenceId") Long referenceId,
            @Param("productId") Long productId,
            Pageable pageable
    );
}
