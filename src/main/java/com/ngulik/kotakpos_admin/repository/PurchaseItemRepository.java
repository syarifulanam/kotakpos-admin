package com.ngulik.kotakpos_admin.repository;

import com.ngulik.kotakpos_admin.entity.PurchaseItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface PurchaseItemRepository extends JpaRepository<PurchaseItem, Long> {

    @Query("SELECT SUM(pi.subtotal) FROM PurchaseItem pi WHERE pi.purchase.id = :purchaseId")
    BigDecimal sumSubtotalByPurchaseId(@Param("purchaseId") Long purchaseId);

    Optional<PurchaseItem> findByProductId(long productId);

    Optional<PurchaseItem> findByIdAndPurchaseId(long purchaseItemId, long purchaseId);
}
