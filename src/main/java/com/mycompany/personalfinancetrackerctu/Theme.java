package com.mycompany.personalfinancetrackerctu;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JButton;

public final class Theme {
    public static final Color APP_BG = new Color(242, 245, 249);
    public static final Color PANEL_BG = new Color(248, 250, 253);
    public static final Color CARD_BG = new Color(255, 255, 255);
    public static final Color PLATINUM_BG = new Color(229, 228, 226);
    public static final Color BEIGE_BG = new Color(245, 245, 220);
    public static final Color ACCENT = new Color(125, 140, 141);
    public static final Color TEXT_PRIMARY = new Color(34, 43, 56);
    public static final Color BUTTON_PRIMARY_BG = new Color(125, 140, 141);
    public static final Color BUTTON_PRIMARY_TEXT = new Color(255, 255, 255);
    public static final Color BUTTON_SECONDARY_BG = new Color(231, 237, 245);
    public static final Color BUTTON_SECONDARY_TEXT = new Color(34, 43, 56);
    public static final Color BUTTON_SECONDARY_BORDER = new Color(186, 199, 216);
    public static final Color TAB_BG = new Color(233, 238, 242);
    public static final Color TAB_SELECTED_BG = new Color(185, 198, 201);

    private Theme() {
    }

    public static void stylePrimaryButton(JButton button) {
        button.setBackground(BUTTON_PRIMARY_BG);
        button.setForeground(BUTTON_PRIMARY_TEXT);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
    }

    public static void styleSecondaryButton(JButton button) {
        button.setBackground(BUTTON_SECONDARY_BG);
        button.setForeground(BUTTON_SECONDARY_TEXT);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BUTTON_SECONDARY_BORDER),
                BorderFactory.createEmptyBorder(5, 13, 5, 13)
        ));
    }
}