package com.ngulik.kotakpos_admin.service;

import com.ngulik.kotakpos_admin.entity.*;
import com.ngulik.kotakpos_admin.enums.PurchaseStatus;
import com.ngulik.kotakpos_admin.enums.ReferenceType;
import com.ngulik.kotakpos_admin.enums.StockMovementType;
import com.ngulik.kotakpos_admin.exception.error.BadRequestException;
import com.ngulik.kotakpos_admin.exception.error.ResourceNotFoundException;
import com.ngulik.kotakpos_admin.repository.ProductRepository;
import com.ngulik.kotakpos_admin.repository.PurchaseItemRepository;
import com.ngulik.kotakpos_admin.repository.PurchaseRepository;
import com.ngulik.kotakpos_admin.repository.StockMovementRepository;
import com.ngulik.kotakpos_admin.util.PriceCalculatorHelper;
import com.ngulik.kotakpos_admin.util.StringHelper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final UserService userService;
    private final ProductService productService;
    private final PurchaseItemRepository purchaseItemRepository;
    private final ProductRepository productRepository;
    private final StockMovementRepository stockMovementRepository;

    public Page<Purchase> getAllPurchases(String keyword, PurchaseStatus status, Pageable pageable) {
        return purchaseRepository.findAll(keyword, status, pageable);
    }

    public Purchase preGenerateNewPurchase() {
        long latestId = purchaseRepository.findLatestId();

        Purchase purchase = new Purchase();
        purchase.setInvoiceNumber(StringHelper.generateInvoiceNumber(latestId));
        return purchase;
    }

    @Transactional
    public Purchase createPurchase(Purchase purchase) {
        purchase.setStatus(PurchaseStatus.DRAFT);
        purchase.setTotalAmount(BigDecimal.ZERO);
        purchase.setCreatedBy(userService.getCurrentUserLogin());
        return purchaseRepository.save(purchase);
    }

    public Purchase getPurchaseById(long id) {
        return purchaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not Found / Invalid purchase Id:" + id));
    }

    @Transactional
    public void addPurchaseItem(Long purchaseId, PurchaseItem request) {
        // NOTE: cari productId dari product
        long productId = request.getProduct().getId();

        Product product = productService.findProductById(request.getProduct().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Not Found / Invalid priduct Id: " + productId));

        // NOTE: inisiasi purchaseItem
        PurchaseItem purchaseItem = new PurchaseItem();

        // NOTE: untuk cari apakah ada item product tersebut di table purchaseItem
        Optional<PurchaseItem> existingPurchaseItem = purchaseItemRepository.findByProductId(productId);
        existingPurchaseItem.ifPresent(item -> purchaseItem.setId(item.getId()));

        // NOTE: jika ada maka tidak reset purchaseItem tersebut
        Purchase purchase = getPurchaseById(purchaseId);

        // NOTE: dapatin data purchase
        BigDecimal subtotal = PriceCalculatorHelper.calculateSubtotal(request.getCostPrice(), request.getQuantity());

        purchaseItem.setPurchase(purchase);
        purchaseItem.setProduct(product);
        purchaseItem.setQuantity(request.getQuantity());
        purchaseItem.setCostPrice(request.getCostPrice());
        purchaseItem.setSubtotal(subtotal);
        purchaseItemRepository.save(purchaseItem);

        purchase.setTotalAmount(purchaseItemRepository.sumSubtotalByPurchaseId(purchaseId));
        purchaseRepository.save(purchase);
    }

    @Transactional
    public void deletePurchaseItem(Long purchaseId, Long purchaseItemId) {
        Purchase purchase = getPurchaseById(purchaseId);
        if (!purchase.getStatus().equals(PurchaseStatus.DRAFT)) {
            throw new ResourceNotFoundException("Only draft purchases items can be deleted.");
        }

        Optional<PurchaseItem> purchaseItem = purchaseItemRepository.findByIdAndPurchaseId(purchaseItemId, purchaseId);
        if (purchaseItem.isEmpty()) {
            throw new ResourceNotFoundException("Invalid purchase item Id: " + purchaseItemId);
        }

        purchaseItemRepository.deleteById(purchaseItemId);

        BigDecimal sumSubtotalByPurchaseId = Optional.ofNullable(
                purchaseItemRepository.sumSubtotalByPurchaseId(purchaseId)).orElse(BigDecimal.ZERO);

        purchase.setTotalAmount(sumSubtotalByPurchaseId);
        purchaseRepository.save(purchase);
    }

    @Transactional
    public void submitPurchase(Long purchaseId) {
        Purchase purchase = getPurchaseById(purchaseId);
        if (purchase.getStatus() != PurchaseStatus.DRAFT) {
            throw new BadRequestException("Only DRAFT purchases can be submitted.");
        }

        for (PurchaseItem item : purchase.getPurchaseItems()) {
            // 1. Update stock movement
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            product.setCostPrice(item.getCostPrice());
            productRepository.save(product);

            // 2. Create stock movement
            StockMovement stockMovement = StockMovement.builder()
                    .product(product)
                    .quantity(item.getQuantity().longValue())
                    .type(StockMovementType.IN)
                    .referenceType(ReferenceType.PURCHASE)
                    .referenceId(purchase.getId())
                    .createdBy(userService.getCurrentUserLogin())
                    .build();
            stockMovementRepository.save(stockMovement);
        }

        purchase.setStatus(PurchaseStatus.COMPLETED);
        purchaseRepository.save(purchase);
    }

    @Transactional
    public void cancelPurchase(Long purchaseId, String reason) {
        Purchase purchase = getPurchaseById(purchaseId);
        if (purchase.getStatus() != PurchaseStatus.COMPLETED) {
            throw new BadRequestException("Only COMPLETED purchases can be canceled.");
        }

        User currentUserLogin = userService.getCurrentUserLogin();

        purchase.setStatus(PurchaseStatus.CANCELED);
        purchase.setCancelledAt(LocalDateTime.now());
        purchase.setCancelledBy(currentUserLogin);
        purchase.setCancelReason(reason);

        for (PurchaseItem item : purchase.getPurchaseItems()) {
            // 1. Rollback product stock
            Product product = item.getProduct();
            product.setStock(product.getStock() -item.getQuantity());
            productRepository.save(product);

            // 2. Create stock movement for the cancellation
            StockMovement stockMovement = StockMovement.builder()
                    .product(product)
                    .quantity(item.getQuantity())
                    .type(StockMovementType.OUT)
                    .note("Cancellation of purchase #" +purchase.getInvoiceNumber())
                    .referenceType(ReferenceType.PURCHASE)
                    .referenceId(purchase.getId())
                    .createdBy(currentUserLogin)
                    .build();
            stockMovementRepository.save(stockMovement);
        }

        purchaseRepository.save(purchase);
    }
}
