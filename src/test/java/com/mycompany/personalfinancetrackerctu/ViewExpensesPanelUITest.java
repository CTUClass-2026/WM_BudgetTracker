package com.mycompany.personalfinancetrackerctu;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.SwingUtilities;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ViewExpensesPanelUITest {
    private Path tempDir;
    private ExpenseService service;

    @BeforeEach
    public void setup() throws IOException {
        tempDir = Files.createTempDirectory("ui_view_");
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
    public void testDeleteViaUi() throws Exception {
        service.addExpense(new Expense("2026-07-06","A",1.0,"a"));
        service.addExpense(new Expense("2025-01-01","B",2.0,"b"));
        ViewExpensesPanel panel = new ViewExpensesPanel(service);

        SwingUtilities.invokeAndWait(() -> {
            panel.refresh();
            panel.selectRow(0);
            panel.clickDelete();
        });

        List<Expense> all = service.getAllExpenses();
        assertEquals(1, all.size());
    }
}
