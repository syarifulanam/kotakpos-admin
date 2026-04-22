package com.ngulik.kotakpos_admin.service;

import com.ngulik.kotakpos_admin.controller.request.SaleRequest;
import com.ngulik.kotakpos_admin.controller.response.SaleResponse;
import com.ngulik.kotakpos_admin.entity.*;
import com.ngulik.kotakpos_admin.enums.ReferenceType;
import com.ngulik.kotakpos_admin.enums.StockMovementType;
import com.ngulik.kotakpos_admin.enums.TransactionsStatus;
import com.ngulik.kotakpos_admin.exception.error.BadRequestException;
import com.ngulik.kotakpos_admin.exception.error.ResourceNotFoundException;
import com.ngulik.kotakpos_admin.mapper.SaleMapper;
import com.ngulik.kotakpos_admin.repository.*;
import com.ngulik.kotakpos_admin.util.StringHelper;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleService {

    private final SaleRepository saleRepository;
    private final SaleMapper saleMapper;
    private final CustomerRepository customerRepository;
    private final UserService userService;
    private final ProductRepository productRepository;
    private final StockMovementRepository stockMovementRepository;
    private final PaymentRepository paymentRepository;

    @Transactional(readOnly = true)
    public Page<SaleResponse> getSales(LocalDateTime startDate, LocalDateTime endDate, String invoiceNumber,
                                       TransactionsStatus status, Pageable pageable) {
        Page<Sale> salePage = saleRepository.searchSales(startDate, endDate, invoiceNumber, status, pageable);
        return salePage.map(saleMapper::toSaleResponse);
    }

    @Transactional(readOnly = true)
    public SaleResponse getSaleById(Long id) {
        return saleRepository.findById(id).map(saleMapper::toSaleResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found"));
    }

    @Transactional
    public SaleResponse createSale(SaleRequest request) {
        //NOTE: 1. Find Customer
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customers not found"));

        //NOTE: 2. Prepare Sale Object
        long latestId = saleRepository.findLatestId();

        Sale sale = new Sale();
        sale.setCustomer(customer);
        sale.setInvoiceNumber(StringHelper.generateSaleNumber(latestId));
        sale.setNote(request.getNote());
        sale.setStatus(TransactionsStatus.PENDING);

        //NOTE: Set discount
        sale.setDiscountAmount(request.getDiscountAmount());

        //NOTE: Get user creator
        User currentUser = userService.getCurrentUserLogin();
        sale.setCreatedBy(currentUser);

        //NOTE: 3. Process Sale Items
        BigDecimal subTotalItems = BigDecimal.ZERO;
        List<SaleItem> saleItems = new ArrayList<>();
        for (var itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + itemRequest.getProductId()));

            //NOTE: Check stock
            if (product.getStock() < itemRequest.getQuantity()) {
                throw new BadRequestException("Insufficient stock for product: " + product.getName());
            }

            SaleItem saleItem = new SaleItem();
            saleItem.setSale(sale);
            saleItem.setProduct(product);
            saleItem.setQuantity(itemRequest.getQuantity());
            saleItem.setSellPrice(product.getSellPrice());
            BigDecimal subtotal = product.getSellPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            saleItem.setSubtotal(subtotal);
            saleItems.add(saleItem);

            subTotalItems = subTotalItems.add(subtotal);

            //NOTE: Update stock
            long oldStock = product.getStock();
            product.setStock(oldStock - itemRequest.getQuantity());
            productRepository.save(product);

            //NOTE: Create Stock Movement Record
            StockMovement stockMovement = new StockMovement();
            stockMovement.setProduct(product);
            stockMovement.setType(StockMovementType.OUT);
            stockMovement.setQuantity(itemRequest.getQuantity());
            stockMovement.setReferenceType(ReferenceType.SALE);
            //NOTE: We will set referenceId after the sale is saved
            stockMovement.setNote("Sale - Invoice " + sale.getInvoiceNumber());
            stockMovement.setCreatedBy(currentUser);
            stockMovementRepository.save(stockMovement);
        }

        sale.setSaleItems(saleItems);

        if (request.getSubtotal().compareTo(subTotalItems) != 0) {
            throw new BadRequestException("Subtotal does not match with expected amount is " + subTotalItems);
        }
        sale.setSubtotal(request.getSubtotal());

        if (request.getTaxRate().compareTo(new BigDecimal("0.11")) != 0 ) {
            throw new BadRequestException("Tax rate must be 11%");
        }
        sale.setTaxRate(request.getTaxRate());

        BigDecimal expectedTaxAmount = request.getDpp().multiply(request.getTaxRate());
        if (request.getTaxAmount().compareTo(expectedTaxAmount) != 0) {
           throw new BadRequestException("Tax amount does not match with expected amount is " + expectedTaxAmount);
        }
        sale.setTaxAmount(request.getTaxAmount());

        BigDecimal expectedDpp = request.getSubtotal().subtract(request.getDiscountAmount());
        if (request.getDpp().compareTo(expectedDpp) != 0) {
            throw new BadRequestException("DPP amount does not match with expected amount is " + expectedDpp);
        }
        sale.setDpp(request.getDpp());

        //NOTE: Use total amount from request
        BigDecimal totalAmount =  request.getDpp().add(request.getTaxAmount());
        sale.setTotalAmount(totalAmount);

        // NOTE: 4. Validate payment
        if (request.getPayment().getAmount().compareTo(totalAmount) != 0) {
            throw new RuntimeException("Payment amount does not match total amount is " + totalAmount);
        }

        //NOTE: 5. Save sale (while cascade to saleItems)
        Sale savedSale = saleRepository.save(sale);

        //NOTE: Update referenceId for all stock movements related to this sale
        stockMovementRepository.findAll()
                .stream()
                .filter(sm -> sm.getReferenceType() == ReferenceType.SALE && sm.getReferenceId() == null)
                .forEach(sm -> {
                    sm.setReferenceId(savedSale.getId());
                    stockMovementRepository.save(sm);
                });

        //NOTE: 6. Create and save Payment
        Payment payment = new Payment();
        payment.setSale(savedSale);
        payment.setAmount(request.getPayment().getAmount());
        payment.setPaymentMethod(request.getPayment().getPaymentMethod());
        payment.setPaymentReference(request.getPayment().getPaymentReference());
        payment.setStatus(TransactionsStatus.COMPLETED);
        payment.setPaidAt(LocalDateTime.now());
        paymentRepository.save(payment);

        //NOTE: 7. Finalize Sale Status
        savedSale.setStatus(TransactionsStatus.COMPLETED);
        Sale finalSale = saleRepository.save(savedSale);

        //NOTE: 8. Return DTO
        return saleMapper.toSaleResponse(finalSale);
    }
}
