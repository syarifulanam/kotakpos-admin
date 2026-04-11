package com.ngulik.kotakpos_admin.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "sales_return_items")
public class SalesReturnItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sales_return_id", nullable = false)
    private SalesReturn salesReturn;

    @ManyToOne
    @JoinColumn(name = "product_by", nullable = false)
    private Product product;

    private Long quantity;

    private BigDecimal price;

    private BigDecimal subtotal;

    private String reason;

    private boolean restocked;
}
