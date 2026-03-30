package com.ngulik.kotakpos_admin.repository;

import com.ngulik.kotakpos_admin.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    @Query("SELECT c FROM Customer c WHERE " +
            "(:name IS NULL OR c.name LIKE CONCAT('%', :name, '%')) AND " +
            "(:phone IS NULL OR c.phone LIKE CONCAT('%', :phone, '%')) AND " +
            "(:email IS NULL OR c.email LIKE CONCAT('%', :email, '%'))")
    Page<Customer> search(
            @Param("name") String name,
            @Param("phone") String phone,
            @Param("email") String email,
            Pageable pageable
    );

}
