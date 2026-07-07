package com.finance.tracker.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.finance.tracker.dao.CategoryDAO;
import com.finance.tracker.dao.impl.CategoryFileDAO;
import com.finance.tracker.model.Category;

public class CategoryService {
    private final CategoryDAO categoryDAO = new CategoryFileDAO();

    // Public method for adding a category
    public void addCategory(String name, String colorHex) throws IOException {
        // If the category name is empty or null
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Category name is required.");
        }

        categoryDAO.add(new Category(0, name, colorHex));
    }

    // Public method for deleting a category
    public void deleteCategory(int id) throws IOException {
        categoryDAO.delete(id);
    }

    // Public method for listing all the categories
    public List<Category> getAllCategories() throws IOException {
        return categoryDAO.findAll();
    }

    // Public method for finding a category by ID
    public Optional<Category> findById(int id) throws IOException {
        return categoryDAO.findById(id);
    }
}
