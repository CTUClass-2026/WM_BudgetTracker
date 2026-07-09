package com.mycompany.personalfinancetrackerctu;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class LoginManagerTest {
    private Path tempDir;

    @BeforeEach
    public void setup() throws IOException {
        tempDir = Files.createTempDirectory("login_test_");
        LoginManager.initForTests(tempDir);
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
    public void testSetAndValidateCredentials() throws IOException {
        LoginManager.setCredentials("testuser", "testpass");
        assertTrue(LoginManager.isValidCredential("testuser", "testpass"));
        assertTrue(LoginManager.usernameMatches("testuser"));
    }

    @Test
    public void testBackupAddAndClear() throws IOException {
        LoginManager.setCredentials("main", "mp");
        LoginManager.addBackupEntry("olduser", "oldpass");
        Set<String> backup = LoginManager.getCredentialBackup();
        assertTrue(backup.contains("olduser,oldpass"));
        // re-init to ensure file write/read works
        LoginManager.initForTests(tempDir);
        Set<String> backup2 = LoginManager.getCredentialBackup();
        assertTrue(backup2.contains("olduser,oldpass") || backup2.contains("main,mp"));

        // clear history
        LoginManager.clearBackupHistoryForTests();
        Set<String> cleared = LoginManager.getCredentialBackup();
        assertEquals(1, cleared.size());
        assertTrue(cleared.contains("main,mp"));
    }
}
