package com.ngulik.kotakpos_admin.entity;

import com.ngulik.kotakpos_admin.enums.PaymentMethod;
import com.ngulik.kotakpos_admin.enums.TransactionsStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id", nullable = false)
    private Sale sale;

    @Column(nullable = false,precision = 15, scale = 2)
    private BigDecimal amount;

    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 100)
    private PaymentMethod paymentMethod;

    @Column(name = "payment_reference", length = 255, unique = true)
    private String paymentReference;

    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 100)
    private TransactionsStatus status;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;
}
