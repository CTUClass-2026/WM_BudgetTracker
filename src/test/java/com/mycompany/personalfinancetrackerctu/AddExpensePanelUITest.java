package com.mycompany.personalfinancetrackerctu;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.swing.SwingUtilities;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AddExpensePanelUITest {
    private Path tempDir;
    private ExpenseService service;

    @BeforeEach
    public void setup() throws IOException {
        tempDir = Files.createTempDirectory("ui_add_");
        Path testFile = tempDir.resolve("expenses.txt");
        service = new ExpenseService(new FileManager(testFile));
        LoginManager.initForTests(tempDir);
        LoginManager.setCredentials("admin","password");
        LoginManager.setLoggedInForTests(true);
    }

    @AfterEach
    public void teardown() throws IOException {
        if (tempDir != null && Files.exists(tempDir)) {
            Files.walk(tempDir).sorted((a,b)->b.compareTo(a)).forEach(p->{try{Files.deleteIfExists(p);}catch(IOException ignore){}});
        }
    }

    @Test
    public void testAddExpenseViaUi() throws Exception {
        AddExpensePanel panel = new AddExpensePanel(service);
        SwingUtilities.invokeAndWait(() -> {
            panel.setFields("2026-07-06","Cat", "15.5", "note");
            panel.clickSave();
        });

        List<Expense> all = service.getAllExpenses();
        assertEquals(1, all.size());
        assertEquals("CAT", all.get(0).getCategory());
    }

    @Test
    public void testBudgetAlertMessageShowsRemainingAmountAndPercentage() {
        AddExpensePanel panel = new AddExpensePanel(service);

        String remainingMessage = panel.buildBudgetAlertMessage("FOOD", 80.0, 100.0, "Rands");
        assertEquals("Budget alert: you have used 80.00% of your budget. You have R20.00 left.", remainingMessage);

        String overBudgetMessage = panel.buildBudgetAlertMessage("FOOD", 120.0, 100.0, "Dollars");
        assertEquals("Budget alert: you have used 120.00% of your budget. You are over budget by $20.00.", overBudgetMessage);
    }
}
