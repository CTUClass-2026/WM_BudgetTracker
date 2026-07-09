package com.mycompany.personalfinancetrackerctu;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class WelcomeDialog extends JDialog {
    private boolean continueToApp;

    public WelcomeDialog(Component parent) {
        super((java.awt.Window) null, "Welcome", ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setIconImage(IconFactory.createAppIcon());
        setSize(420, 280);
        setResizable(false);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(Theme.APP_BG);

        JPanel content = new JPanel();
        content.setBorder(BorderFactory.createEmptyBorder(18, 20, 8, 20));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Theme.PANEL_BG);

        JLabel title = new JLabel("Expense Tracker 2026");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        title.setForeground(Theme.ACCENT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(title);
        content.add(Box.createVerticalStrut(16));

        content.add(createNameLabel("Tshepo Makate"));
        content.add(Box.createVerticalStrut(6));
        content.add(createNameLabel("Lebohang Mokhema"));
        content.add(Box.createVerticalStrut(6));
        content.add(createNameLabel("Pierre Pienaar"));
        content.add(Box.createVerticalStrut(6));
        content.add(createNameLabel("Jakobus van Schalkwyk"));
        add(content, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttons.setBackground(Theme.PANEL_BG);
        JButton enterButton = new JButton("Enter");
        Theme.stylePrimaryButton(enterButton);
        buttons.add(enterButton);
        add(buttons, BorderLayout.SOUTH);

        enterButton.addActionListener(e -> {
            continueToApp = true;
            dispose();
        });

        getRootPane().setDefaultButton(enterButton);
    }

    public boolean showDialog() {
        setVisible(true);
        return continueToApp;
    }

    private JLabel createNameLabel(String name) {
        JLabel label = new JLabel(name);
        label.setForeground(Theme.TEXT_PRIMARY);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }
}