package com.ngulik.kotakpos_admin.service;

import com.ngulik.kotakpos_admin.entity.Purchase;
import com.ngulik.kotakpos_admin.entity.StockMovement;
import com.ngulik.kotakpos_admin.enums.ReferenceType;
import com.ngulik.kotakpos_admin.enums.StockMovementType;
import com.ngulik.kotakpos_admin.repository.StockMovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockMovementService {

    private final StockMovementRepository stockMovementRepository;

    public Page<StockMovement> getStockMovements(StockMovementType type, ReferenceType referenceType, Long referenceId, Long productId, Pageable pageable) {
        return stockMovementRepository.search(type, referenceType, referenceId, productId, pageable);
    }
}
