package com.mycompany.personalfinancetrackerctu;

//import com.mycompany.personalfinancetrackerctu.Expense;
import java.io.IOException;
import java.util.List;

public interface ExpenseDAO {
    void add(Expense expense) throws IOException;
    void delete(int id) throws IOException;
    List<Expense> findAll() throws IOException;
    List<Expense> findByMonth(int month, int year) throws IOException;
}