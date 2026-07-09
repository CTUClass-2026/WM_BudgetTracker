package com.mycompany.personalfinancetrackerctu;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ExpenseServiceTest {
    private Path tempDir;
    private ExpenseService service;

    @BeforeEach
    public void setup() throws IOException {
        tempDir = Files.createTempDirectory("expenses_test_");
        Path testFile = tempDir.resolve("expenses.txt");
        FileManager fm = new FileManager(testFile);
        service = new ExpenseService(fm);
    }

    @AfterEach
    public void teardown() throws IOException {
        if (tempDir != null && Files.exists(tempDir)) {
            Files.walk(tempDir)
                    .sorted((a, b) -> b.compareTo(a))
                    .forEach(p -> {
                        try { Files.deleteIfExists(p); } catch (IOException ignore) {}
                    });
        }
    }

    @Test
    public void testAddAndGetAllExpenses() throws IOException {
        Expense e = new Expense("2026-07-06", "Test", 12.34, "desc");
        service.addExpense(e);
        List<Expense> all = service.getAllExpenses();
        assertEquals(1, all.size());
        Expense got = all.get(0);
        assertEquals(e.getDate(), got.getDate());
        assertEquals("TEST", got.getCategory());
        assertEquals(e.getAmount(), got.getAmount(), 0.0001);
        assertEquals(e.getDescription(), got.getDescription());
    }

    @Test
    public void testDeleteExpense() throws IOException {
        service.addExpense(new Expense("2026-07-06","A",1.0,"a"));
        service.addExpense(new Expense("2025-01-01","B",2.0,"b"));
        List<Expense> all = service.getAllExpenses();
        assertEquals(2, all.size());
        service.deleteExpense(0);
        List<Expense> afterwards = service.getAllExpenses();
        assertEquals(1, afterwards.size());
    }

    @Test
    public void testUpdateExpense() throws IOException {
        service.addExpense(new Expense("2026-07-06", "Original", 10.0, "old"));
        List<Expense> all = service.getAllExpenses();
        assertEquals(1, all.size());

        Expense updated = new Expense("2026-07-07", "Updated", 15.50, "new desc");
        service.updateExpense(0, updated);

        List<Expense> result = service.getAllExpenses();
        assertEquals(1, result.size());
        Expense got = result.get(0);
        assertEquals("2026-07-07", got.getDate());
        assertEquals("UPDATED", got.getCategory());
        assertEquals(15.50, got.getAmount(), 0.0001);
        assertEquals("NEW DESC", got.getDescription());
    }
}
