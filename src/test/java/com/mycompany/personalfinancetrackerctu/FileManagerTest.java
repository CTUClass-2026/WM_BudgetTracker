package com.mycompany.personalfinancetrackerctu;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileManagerTest {
    private Path tempDir;
    private FileManager fm;

    @BeforeEach
    public void setup() throws IOException {
        tempDir = Files.createTempDirectory("fm_test_");
        Path testFile = tempDir.resolve("expenses.txt");
        fm = new FileManager(testFile);
    }

    @AfterEach
    public void teardown() throws IOException {
        if (tempDir != null && Files.exists(tempDir)) {
            Files.walk(tempDir)
                    .sorted((a, b) -> b.compareTo(a))
                    .forEach(p -> { try { Files.deleteIfExists(p); } catch (IOException ignore) {} });
        }
    }

    @Test
    public void testExpensesAreSortedDescendingOnLoad() throws IOException {
        Expense older = new Expense("2020-01-01","Old",10.0,"old");
        Expense newer = new Expense("2026-07-06","New",20.0,"new");
        fm.save(older);
        fm.save(newer);

        List<Expense> all = fm.loadAll();
        assertEquals(2, all.size());
        assertEquals("2026-07-06", all.get(0).getDate());
        assertEquals("2020-01-01", all.get(1).getDate());
    }

    @Test
    public void testDeleteOutOfRangeDoesNotThrowOrModify() throws IOException {
        fm.save(new Expense("2026-07-06","A",1.0,"a"));
        fm.save(new Expense("2025-01-01","B",2.0,"b"));
        List<Expense> before = fm.loadAll();
        assertEquals(2, before.size());

        // out-of-range delete
        fm.delete(99);

        List<Expense> after = fm.loadAll();
        assertEquals(2, after.size(), "Delete out of range should not remove items");
    }

    @Test
    public void testDeleteValidIndexRemovesItem() throws IOException {
        fm.save(new Expense("2026-07-06","A",1.0,"a"));
        fm.save(new Expense("2025-01-01","B",2.0,"b"));
        List<Expense> all = fm.loadAll();
        assertEquals(2, all.size());

        fm.delete(0);
        List<Expense> after = fm.loadAll();
        assertEquals(1, after.size());
    }
}
