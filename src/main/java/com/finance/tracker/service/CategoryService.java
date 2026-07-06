package com.finance.tracker.service;

import com.finance.tracker.dao.CategoryDAO;
import com.finance.tracker.dao.impl.CategoryFileDAO;
import com.finance.tracker.model.Category;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class CategoryService {
    private final CategoryDAO categoryDAO = new CategoryFileDAO();

    public void addCategory(String name, String colorHex) throws IOException {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Category name is required.");
        }
        categoryDAO.add(new Category(0, name, colorHex));
    }

    public void deleteCategory(int id) throws IOException {
        categoryDAO.delete(id);
    }

    public List<Category> getAllCategories() throws IOException {
        return categoryDAO.findAll();
    }

    public Optional<Category> findById(int id) throws IOException {
        return categoryDAO.findById(id);
    }
}
