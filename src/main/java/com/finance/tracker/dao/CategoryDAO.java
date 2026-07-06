package com.finance.tracker.dao;

import com.finance.tracker.model.Category;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface CategoryDAO {
    void add(Category category) throws IOException;
    void delete(int id) throws IOException;
    List<Category> findAll() throws IOException;
    Optional<Category> findById(int id) throws IOException;
}
