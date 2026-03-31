package com.ngulik.kotakpos_admin.repository;

import com.ngulik.kotakpos_admin.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c WHERE " +
            "(:query IS NULL OR " +
            "CAST(c.id AS string) = :query OR " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Category> search(@Param("query") String query, Pageable pageable);

    List<Category> findAllByOrderByIdDesc();
}
