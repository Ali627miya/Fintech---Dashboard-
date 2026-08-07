package com.Ali.fintech_dashboard.controller;

import com.Ali.fintech_dashboard.entity.Category;
import com.Ali.fintech_dashboard.repository.CategoryRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CategoryController {

    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/api/categories")
    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }
}
