package com.ngulik.kotakpos_admin.repository;

import com.ngulik.kotakpos_admin.entity.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    @Query("SELECT s FROM Supplier s WHERE " +
            "(:name IS NULL OR s.name LIKE %:name%) AND " +
            "(:phone IS NULL OR s.phone LIKE %:phone%) AND " +
            "(:email IS NULL OR s.email LIKE %:email%)")
    Page<Supplier> search(
            @Param("name") String name,
            @Param("phone") String phone,
            @Param("email") String email,
            Pageable pageable
    );
}
