package com.ngulik.kotakpos_admin.repository;

import com.ngulik.kotakpos_admin.entity.SalesReturn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SalesReturnRepository extends JpaRepository<SalesReturn, Long> {

    @Query("SELECT COALESCE(MAX(p.id), 0) FROM SalesReturn p")
    Long findLatestId();
}
