package com.ngulik.kotakpos_admin.repository;

import com.ngulik.kotakpos_admin.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
