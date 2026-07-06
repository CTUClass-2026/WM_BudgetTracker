package com.finance.tracker.dao;

import com.finance.tracker.model.Budget;
import java.io.IOException;
import java.util.List;

public interface BudgetDAO {
    void addOrUpdate(Budget budget) throws IOException;
    void delete(int id) throws IOException;
    List<Budget> findAll() throws IOException;
}
