package com.ngulik.kotakpos_admin.service;

import com.ngulik.kotakpos_admin.entity.Adjustment;
import com.ngulik.kotakpos_admin.entity.Product;
import com.ngulik.kotakpos_admin.entity.StockMovement;
import com.ngulik.kotakpos_admin.entity.User;
import com.ngulik.kotakpos_admin.enums.AdjustmentType;
import com.ngulik.kotakpos_admin.enums.StockMovementType;
import com.ngulik.kotakpos_admin.repository.AdjustmentRepository;
import com.ngulik.kotakpos_admin.repository.ProductRepository;
import com.ngulik.kotakpos_admin.repository.StockMovementRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import jakarta.persistence.criteria.Predicate;

@RequiredArgsConstructor
@Service
public class AdjustmentService {

    private final ProductRepository productRepository;
    private final UserService userService;
    private final AdjustmentRepository adjustmentRepository;
    private final StockMovementRepository stockMovementRepository;

    @Transactional
    public void createdAdjustment(Adjustment adjustment) {
        Product product = adjustment.getProduct();
        long quantity = adjustment.getQuantity();

        if (adjustment.getType() == AdjustmentType.ADJUSTMENT_IN) {
            product.setStock(product.getStock() + quantity);
        } else {
            product.setStock(product.getStock() - quantity);
        }

        productRepository.save(product);

        User currentUser = userService.getCurrentUserLogin();

        StockMovement stockMovement = new StockMovement();
        stockMovement.setProduct(product);
        stockMovement.setQuantity(quantity);
        stockMovement.setNote(adjustment.getNote());
        stockMovement.setCreatedBy(currentUser);

        if (adjustment.getType() == AdjustmentType.ADJUSTMENT_IN) {
            stockMovement.setType(StockMovementType.IN);
        } else {
            stockMovement.setType(StockMovementType.OUT);
        }

        adjustment.setCreatedBy(currentUser);
        adjustmentRepository.save(adjustment);

        stockMovementRepository.save(stockMovement);
    }

    public Page<Adjustment> getAdjustments(AdjustmentType type, Long productId, Pageable pageable) {
        Specification<Adjustment> spec = (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (type != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("type"), type));
            }

            if (productId != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("product").get("id"), productId));
            }

            return predicate;
        };

        return adjustmentRepository.findAll(spec, pageable);
    }
}
