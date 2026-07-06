package com.finance.tracker.dao.impl;

import com.finance.tracker.dao.CategoryDAO;
import com.finance.tracker.file.CsvUtil;
import com.finance.tracker.file.FileManager;
import com.finance.tracker.model.Category;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CategoryFileDAO implements CategoryDAO {
    @Override
    public void add(Category category) throws IOException {
        List<Category> categories = findAll();
        boolean exists = categories.stream().anyMatch(c -> c.getName().equalsIgnoreCase(category.getName()));
        if (exists) throw new IllegalArgumentException("Category already exists.");

        int nextId = categories.stream().mapToInt(Category::getId).max().orElse(0) + 1;
        category.setId(nextId);
        categories.add(category);
        saveAll(categories);
    }

    @Override
    public void delete(int id) throws IOException {
        List<Category> categories = findAll();
        categories.removeIf(c -> c.getId() == id);
        saveAll(categories);
    }

    @Override
    public List<Category> findAll() throws IOException {
        List<String> lines = Files.readAllLines(FileManager.CATEGORY_FILE);
        List<Category> categories = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            if (!lines.get(i).isBlank()) {
                List<String> p = CsvUtil.parseLine(lines.get(i));
                categories.add(new Category(Integer.parseInt(p.get(0)), p.get(1), p.get(2)));
            }
        }
        return categories;
    }

    @Override
    public Optional<Category> findById(int id) throws IOException {
        return findAll().stream().filter(c -> c.getId() == id).findFirst();
    }

    private void saveAll(List<Category> categories) throws IOException {
        StringBuilder sb = new StringBuilder("id,name,colorHex\n");
        for (Category c : categories) {
            sb.append(c.getId()).append(",")
              .append(CsvUtil.escape(c.getName())).append(",")
              .append(CsvUtil.escape(c.getColorHex())).append("\n");
        }
        Files.writeString(FileManager.CATEGORY_FILE, sb.toString());
    }
}
