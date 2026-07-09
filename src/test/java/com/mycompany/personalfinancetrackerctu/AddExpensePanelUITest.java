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
}
