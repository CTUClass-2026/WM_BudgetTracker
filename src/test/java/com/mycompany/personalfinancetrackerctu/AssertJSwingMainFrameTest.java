package com.mycompany.personalfinancetrackerctu;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

public class AssertJSwingMainFrameTest {
    private FrameFixture window;
    private Path tempDir;

    @BeforeEach
    public void setUp() throws Exception {
        tempDir = Files.createTempDirectory("ajsw_");
        Path testFile = tempDir.resolve("expenses.txt");
        ExpenseService service = new ExpenseService(new FileManager(testFile));
        LoginManager.initForTests(tempDir);
        LoginManager.setCredentials("admin","password");
        LoginManager.setLoggedInForTests(true);

        MainFrame frame = GuiActionRunner.execute(() -> new MainFrame(service));
        window = new FrameFixture(frame);
        window.show();
    }

    @AfterEach
    public void tearDown() throws Exception {
        if (window != null) {
            window.cleanUp();
        }
        if (tempDir != null && Files.exists(tempDir)) {
            Files.walk(tempDir)
                    .sorted((a, b) -> b.compareTo(a))
                    .forEach(p -> {
                        try {
                            Files.deleteIfExists(p);
                        } catch (java.io.IOException ignore) {
                        }
                    });
        }
    }

    @Test
    public void smokeTestMainFrameVisible() {
        window.requireVisible();
        window.requireTitle("CTU Finance Tracker™ 2026");
    }
}
