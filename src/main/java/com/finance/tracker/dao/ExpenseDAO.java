package com.finance.tracker.dao;

import com.finance.tracker.model.Expense;
import java.io.IOException;
import java.util.List;

/**
 * Data access contract for expense persistence.
 */
public interface ExpenseDAO {
    void add(Expense expense) throws IOException;
    void delete(int id) throws IOException;
    List<Expense> findAll() throws IOException;
    List<Expense> findByMonth(int month, int year) throws IOException;
}
