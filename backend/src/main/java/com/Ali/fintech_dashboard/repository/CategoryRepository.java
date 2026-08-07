package com.Ali.fintech_dashboard.repository;

import com.Ali.fintech_dashboard.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
