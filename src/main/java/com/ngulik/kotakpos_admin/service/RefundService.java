package com.ngulik.kotakpos_admin.service;

import com.ngulik.kotakpos_admin.entity.*;
import com.ngulik.kotakpos_admin.enums.ReferenceType;
import com.ngulik.kotakpos_admin.enums.StockMovementType;
import com.ngulik.kotakpos_admin.enums.TransactionsStatus;
import com.ngulik.kotakpos_admin.exception.error.ResourceNotFoundException;
import com.ngulik.kotakpos_admin.repository.*;
import com.ngulik.kotakpos_admin.util.StringHelper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RefundService {

    private final SaleRepository saleRepository;
    private final SalesReturnRepository salesReturnRepository;
    private final RefundRepository refundRepository;
    private final StockMovementRepository stockMovementRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Transactional
    public void processFullRefund(Long saleId, String reason) {
        //NOTE: 1. Find the original sale and its payment
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found"));
        Payment payment = sale.getPayment();
        if (payment == null) {
            throw new ResourceNotFoundException("Payment not found for this sale");
        }

        //NOTE: For now, let's fetch the admin user as the processor
        User currentUser = userService.getCurrentUserLogin();

        //NOTE: 2. Create the SalesReturn event
        SalesReturn salesReturn = new SalesReturn();
        salesReturn.setReturnNumber(StringHelper.generateReturnNumber(salesReturnRepository.findLatestId()));
        salesReturn.setSale(sale);
        salesReturn.setReason(reason);
        salesReturn.setTotalRefundAmount(sale.getTotalAmount());
        salesReturn.setCreatedBy(currentUser);

        //NOTE: 3. Create SalesReturnItems and update stock
        List<SalesReturnItem> returnItems = new ArrayList<>();
        for (SaleItem originalItem : sale.getSaleItems()) {
            SalesReturnItem returnItem = new SalesReturnItem();
            returnItem.setSalesReturn(salesReturn);
            returnItem.setProduct(originalItem.getProduct());
            returnItem.setQuantity(originalItem.getQuantity());
            returnItem.setPrice(originalItem.getSellPrice());
            returnItem.setSubtotal(originalItem.getSubtotal());
            returnItem.setReason(reason); //TODO: Fill it
            returnItem.setRestocked(true); //Assuming all items are restocked
            returnItems.add(returnItem);

            //NOTE: Create stock movement for the returned item
            StockMovement stockMovement = new StockMovement();
            stockMovement.setProduct(originalItem.getProduct());
            stockMovement.setType(StockMovementType.IN);
            stockMovement.setQuantity(originalItem.getQuantity());
            stockMovement.setReferenceType(ReferenceType.SALES_RETURN);
            stockMovement.setNote("Return for Invoice " + sale.getInvoiceNumber());
            stockMovement.setCreatedBy(currentUser);
            stockMovementRepository.save(stockMovement);

            //NOTE: Add stock back to product
            Product product = originalItem.getProduct();
            product.setStock(product.getStock() + originalItem.getQuantity());
        }

        salesReturn.setItems(returnItems);
        salesReturn.setStatus(TransactionsStatus.REFUNDED);
        SalesReturn savedSalesReturn = salesReturnRepository.save(salesReturn);

        //NOTE: Update referenceId for stock movements
        stockMovementRepository.findAll().stream()
                .filter(sm -> sm.getReferenceType() == ReferenceType.SALES_RETURN && sm.getReferenceId() == null)
                .forEach(sm -> sm.setReferenceId(savedSalesReturn.getId()));

        //NOTE: 4. Create the Refund transaction
        Refund refund = new Refund();
        refund.setPayment(payment);
        refund.setSalesReturn(savedSalesReturn);
        refund.setAmount(sale.getTotalAmount());
        refund.setReason("Full refund for invoice " + sale.getInvoiceNumber());
        refund.setCreatedBy(currentUser);
        refundRepository.save(refund);
    }
}
