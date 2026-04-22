package com.ngulik.kotakpos_admin.repository;

import com.ngulik.kotakpos_admin.entity.Adjustment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AdjustmentRepository extends JpaRepository<Adjustment, Long>, JpaSpecificationExecutor<Adjustment> {

}
