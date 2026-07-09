package com.mycompany.personalfinancetrackerctu;

import java.awt.Component;
import java.awt.GridLayout;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * Manages authentication and credential storage for the application.
 * This security helper is used by the presentation layer before sensitive actions are allowed.
 */
public class LoginManager {
    private static final String ADMIN_USERNAME = "admin";
    private static final String DEFAULT_USERNAME = "admin";
    private static final String DEFAULT_PASSWORD = "password";
    private static Path CREDENTIALS_FILE = Paths.get("data", "credentials.txt");
    private static Path CREDENTIALS_BACKUP_FILE = Paths.get("data", "credentials_backup.txt");
    private static final List<String> DEFAULT_BACKUP_CREDENTIALS = List.of(
            "user1,password1",
            "user2,password2",
            "user3,password3"
    );

    private static String currentUsername = DEFAULT_USERNAME;
    private static String currentPassword = DEFAULT_PASSWORD;
    private static boolean loggedIn;
    private static final Set<String> credentialBackup = new LinkedHashSet<>();
    private static boolean testMode = false;

    static {
        loadStoredCredentials();
    }

    /**
     * Initialize login manager to use a specific data directory (tests only).
     */
    public static void initForTests(Path directory) {
        CREDENTIALS_FILE = directory.resolve("credentials.txt");
        CREDENTIALS_BACKUP_FILE = directory.resolve("credentials_backup.txt");
        credentialBackup.clear();
        testMode = true;
        loadStoredCredentials();
    }

    public static boolean isTestMode() {
        return testMode;
    }

    // Non-UI helpers for tests and programmatic control
    public static void setCredentials(String username, String password) throws IOException {
        currentUsername = username;
        currentPassword = password;
        saveCurrentCredentials();
    }

    public static Set<String> getCredentialBackup() {
        return new LinkedHashSet<>(credentialBackup);
    }

    public static void addBackupEntry(String username, String password) throws IOException {
        backupCurrentCredentials(username, password);
    }

    public static void clearBackupHistoryForTests() throws IOException {
        credentialBackup.clear();
        credentialBackup.add(currentUsername + "," + currentPassword);
        Files.write(CREDENTIALS_BACKUP_FILE, credentialBackup, StandardCharsets.UTF_8);
    }

    public static void setLoggedInForTests(boolean value) {
        loggedIn = value;
    }

    public static boolean authenticate(Component parent) {
        if (loggedIn) {
            return true;
        }
        LoginDialog dialog = new LoginDialog(parent);
        if (dialog.showDialog()) {
            loggedIn = true;
        }
        return loggedIn;
    }

    public static boolean requireLogin(Component parent) {
        if (testMode) {
            loggedIn = true;
            return true;
        }
        loggedIn = false;
        return authenticate(parent);
    }

    public static boolean isLoggedIn() {
        return loggedIn;
    }

    public static boolean forgotCredentials(Component parent) {
        if (!isAdminUser()) {
            JOptionPane.showMessageDialog(parent,
                    "Access denied. Only the admin user may view or manage credentials.",
                    "Admin Required", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        StringBuilder message = new StringBuilder();
        message.append("Current username: ").append(currentUsername).append("\n");
        message.append("Current password: ").append(currentPassword).append("\n");
        if (!credentialBackup.isEmpty()) {
            message.append("\nPrevious credentials:\n");
            for (String entry : credentialBackup) {
                if (entry.equals(currentUsername + "," + currentPassword)) {
                    continue;
                }
                String[] parts = entry.split(",", 2);
                if (parts.length == 2) {
                    message.append("Username: ").append(parts[0]).append(", Password: ").append(parts[1]).append("\n");
                }
            }
        }

        Object[] options = {"Close", "Reset Credentials", "Restore Previous Credentials", "Show Only Usernames", "Clear Backup History"};
        int choice = JOptionPane.showOptionDialog(parent, message.toString(), "Forgot Credentials",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        if (choice == 1) {
            return resetCredentials(parent);
        }
        if (choice == 2) {
            return restorePreviousCredentials(parent);
        }
        if (choice == 3) {
            return showOnlyUsernames(parent);
        }
        if (choice == 4) {
            return clearBackupHistory(parent);
        }
        return true;
    }

    private static boolean showOnlyUsernames(Component parent) {
        List<String> usernames = new ArrayList<>();
        for (String entry : credentialBackup) {
            String[] parts = entry.split(",", 2);
            if (parts.length == 2 && !parts[0].equals(currentUsername)) {
                usernames.add(parts[0]);
            }
        }
        if (usernames.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "No previous usernames available.", "Show Usernames", JOptionPane.INFORMATION_MESSAGE);
            return true;
        }

        String[] usernameArray = usernames.toArray(String[]::new);
        String selectedUsername = (String) JOptionPane.showInputDialog(parent, "Select username to view details:", "Show Only Usernames",
                JOptionPane.PLAIN_MESSAGE, null, usernameArray, usernameArray[0]);
        if (selectedUsername == null || selectedUsername.trim().isEmpty()) {
            return true;
        }

        List<String> matchingEntries = new ArrayList<>();
        for (String entry : credentialBackup) {
            String[] parts = entry.split(",", 2);
            if (parts.length == 2 && parts[0].equals(selectedUsername)) {
                matchingEntries.add("Username: " + parts[0] + "    Password: " + parts[1]);
            }
        }
        if (matchingEntries.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "No details available for selected username.", "No Details", JOptionPane.INFORMATION_MESSAGE);
            return true;
        }

        String[] detailsArray = matchingEntries.toArray(String[]::new);
        String selectedDetail = (String) JOptionPane.showInputDialog(parent, "Select credentials to restore:", "Restore Credentials",
                JOptionPane.PLAIN_MESSAGE, null, detailsArray, detailsArray[0]);
        if (selectedDetail == null || selectedDetail.trim().isEmpty()) {
            return true;
        }

        String[] selectedParts = selectedDetail.replace("Username: ", "").replace("    Password: ", ",").split(",", 2);
        if (selectedParts.length == 2) {
            currentUsername = selectedParts[0];
            currentPassword = selectedParts[1];
            try {
                saveCurrentCredentials();
                JOptionPane.showMessageDialog(parent, "Credentials have been restored.", "Restore Complete", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(parent, "Unable to save restored credentials: " + ex.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return true;
    }

    private static boolean clearBackupHistory(Component parent) {
        int choice = JOptionPane.showConfirmDialog(parent,
                "This will remove all stored backup credentials except the current one. Continue?",
                "Clear Backup History", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) {
            return true;
        }
        credentialBackup.clear();
        credentialBackup.add(currentUsername + "," + currentPassword);
        try {
            Files.write(CREDENTIALS_BACKUP_FILE, credentialBackup, StandardCharsets.UTF_8);
            JOptionPane.showMessageDialog(parent, "Backup history cleared.", "Clear Complete", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(parent, "Unable to clear backup history: " + ex.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
        }
        return true;
    }

    private static boolean resetCredentials(Component parent) {
        String newUsername = JOptionPane.showInputDialog(parent, "Enter new username:", currentUsername);
        if (newUsername == null || newUsername.trim().isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Username reset canceled or invalid.", "Reset Canceled", JOptionPane.WARNING_MESSAGE);
            return true;
        }
        String newPassword = JOptionPane.showInputDialog(parent, "Enter new password:", currentPassword);
        if (newPassword == null || newPassword.trim().isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Password reset canceled or invalid.", "Reset Canceled", JOptionPane.WARNING_MESSAGE);
            return true;
        }
        currentUsername = newUsername.trim();
        currentPassword = newPassword.trim();
        try {
            saveCurrentCredentials();
            JOptionPane.showMessageDialog(parent, "Credentials have been reset successfully.", "Reset Complete", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(parent, "Unable to save new credentials: " + ex.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
        }
        return true;
    }

    private static boolean restorePreviousCredentials(Component parent) {
        List<String> labels = new ArrayList<>();
        List<String> entries = new ArrayList<>();
        for (String entry : credentialBackup) {
            if (entry.equals(currentUsername + "," + currentPassword)) {
                continue;
            }
            String[] parts = entry.split(",", 2);
            if (parts.length == 2) {
                labels.add("Username: " + parts[0] + "    Password: " + parts[1]);
                entries.add(entry);
            }
        }
        if (labels.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "No previous credentials available to restore.", "Restore Unavailable", JOptionPane.INFORMATION_MESSAGE);
            return true;
        }

        String[] labelsArray = labels.toArray(String[]::new);
        String selectedLabel = (String) JOptionPane.showInputDialog(parent, "Select credentials to restore:", "Restore Credentials",
                JOptionPane.PLAIN_MESSAGE, null, labelsArray, labelsArray[0]);
        if (selectedLabel == null || selectedLabel.trim().isEmpty()) {
            return true;
        }

        int selectedIndex = labels.indexOf(selectedLabel);
        if (selectedIndex < 0) {
            return true;
        }

        String[] parts = entries.get(selectedIndex).split(",", 2);
        if (parts.length == 2) {
            currentUsername = parts[0];
            currentPassword = parts[1];
            try {
                saveCurrentCredentials();
                JOptionPane.showMessageDialog(parent, "Previous credentials have been restored.", "Restore Complete", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(parent, "Unable to save restored credentials: " + ex.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return true;
    }

    public static boolean isValidCredential(String username, String password) {
        if (currentUsername.equals(username) && currentPassword.equals(password)) {
            return true;
        }
        return credentialBackup.contains(username + "," + password);
    }

    public static boolean isAdminUser() {
        return ADMIN_USERNAME.equals(currentUsername);
    }

    public static boolean requireAdminAccess(Component parent) {
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.add(new JLabel("Admin username:"));
        JTextField usernameField = new JTextField();
        panel.add(usernameField);
        panel.add(new JLabel("Admin password:"));
        JPasswordField passwordField = new JPasswordField();
        panel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(parent, panel,
                "Admin Authentication", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return false;
        }

        String enteredUsername = usernameField.getText().trim();
        String enteredPassword = new String(passwordField.getPassword());
        if (ADMIN_USERNAME.equals(enteredUsername) && currentPassword.equals(enteredPassword) && currentUsername.equals(ADMIN_USERNAME)) {
            return true;
        }

        JOptionPane.showMessageDialog(parent,
                "Invalid admin credentials. Access denied.",
                "Admin Authentication Failed", JOptionPane.ERROR_MESSAGE);
        return false;
    }

    public static boolean usernameMatches(String value) {
        return currentUsername.equals(value);
    }

    public static boolean passwordMatches(String value) {
        return currentPassword.equals(value);
    }

    private static void loadStoredCredentials() {
        try {
            ensureDataDirectory();
            if (Files.exists(CREDENTIALS_FILE)) {
                List<String> lines = Files.readAllLines(CREDENTIALS_FILE, StandardCharsets.UTF_8);
                if (!lines.isEmpty()) {
                    String[] parts = lines.get(0).trim().split(",", 2);
                    if (parts.length == 2) {
                        currentUsername = parts[0];
                        currentPassword = parts[1];
                    }
                }
            } else {
                saveCurrentCredentials();
            }
            loadBackupCredentials();
            boolean changed = false;
            for (String credential : DEFAULT_BACKUP_CREDENTIALS) {
                if (credentialBackup.add(credential)) {
                    changed = true;
                }
            }
            if (credentialBackup.add(currentUsername + "," + currentPassword)) {
                changed = true;
            }
            if (changed) {
                Files.write(CREDENTIALS_BACKUP_FILE, credentialBackup, StandardCharsets.UTF_8);
            }
        } catch (IOException ex) {
            // Keep defaults if loading fails
        }
    }

    private static void loadBackupCredentials() throws IOException {
        if (!Files.exists(CREDENTIALS_BACKUP_FILE)) {
            return;
        }
        List<String> lines = Files.readAllLines(CREDENTIALS_BACKUP_FILE, StandardCharsets.UTF_8);
        for (String line : lines) {
            if (line != null && !line.isBlank()) {
                credentialBackup.add(line.trim());
            }
        }
    }

    private static void saveCurrentCredentials() throws IOException {
        ensureDataDirectory();
        Files.write(CREDENTIALS_FILE, List.of(currentUsername + "," + currentPassword), StandardCharsets.UTF_8);
        backupCurrentCredentials(currentUsername, currentPassword);
    }

    private static void backupCurrentCredentials(String username, String password) throws IOException {
        String entry = username + "," + password;
        if (credentialBackup.add(entry)) {
            Files.write(CREDENTIALS_BACKUP_FILE, credentialBackup, StandardCharsets.UTF_8);
        }
    }

    private static void ensureDataDirectory() throws IOException {
        Path dir = CREDENTIALS_FILE.getParent();
        if (dir != null && Files.notExists(dir)) {
            Files.createDirectories(dir);
        }
    }
}
