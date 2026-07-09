package com.mycompany.personalfinancetrackerctu;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class LoginDialog extends JDialog {
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private boolean authenticated;

    public LoginDialog(Component parent) {
        super(getOwnerWindow(parent), "CTU Finance Tracker™ 2026 - Login", ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setIconImage(IconFactory.createAppIcon());
        setSize(320, 210);
        setResizable(false);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(Theme.APP_BG);

        JLabel subtitle = new JLabel("CTU Finance Tracker™ 2026 Login");
        subtitle.setFont(subtitle.getFont().deriveFont(Font.BOLD));
        subtitle.setForeground(Theme.ACCENT);
        subtitle.setBorder(BorderFactory.createEmptyBorder(10, 15, 0, 15));
        add(subtitle, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        formPanel.setBackground(Theme.PANEL_BG);
        formPanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        formPanel.add(usernameField);
        formPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        formPanel.add(passwordField);
        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Theme.PANEL_BG);
        JButton forgotButton = new JButton("Admin: Forgot username/password");
        JButton loginButton = new JButton("Login");
        JButton cancelButton = new JButton("Cancel");
        Theme.styleSecondaryButton(forgotButton);
        Theme.styleSecondaryButton(cancelButton);
        Theme.stylePrimaryButton(loginButton);
        buttonPanel.add(forgotButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(loginButton);
        add(buttonPanel, BorderLayout.SOUTH);

        forgotButton.addActionListener(e -> {
            if (LoginManager.requireAdminAccess(this)) {
                LoginManager.forgotCredentials(this);
            }
        });
        loginButton.addActionListener(e -> attemptLogin());
        cancelButton.addActionListener(e -> {
            authenticated = false;
            dispose();
        });

        getRootPane().setDefaultButton(loginButton);
    }

    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (LoginManager.isValidCredential(username, password)) {
            authenticated = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
        }
    }

    public boolean showDialog() {
        setVisible(true);
        return authenticated;
    }

    private static Window getOwnerWindow(Component parent) {
        if (parent == null) {
            return null;
        }
        Window window = SwingUtilities.getWindowAncestor(parent);
        return window != null ? window : null;
    }
}
