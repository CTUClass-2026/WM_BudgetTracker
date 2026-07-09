package com.mycompany.personalfinancetrackerctu;

/*
 * Imported project classes/files: none.
 */

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Creates the small application icon shown in the window title and dialogs.
 * This helper is used by the presentation layer whenever a UI window needs branding.
 */
public class IconFactory {
    // Builds a simple branded icon that can be displayed in the main window and dialogs.
    public static BufferedImage createAppIcon() {
        BufferedImage icon = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = icon.createGraphics();
        g.setColor(new Color(0, 102, 204));
        g.fillRect(0, 0, 32, 32);
        g.setColor(Color.WHITE);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        g.drawString("$", 8, 24);
        g.dispose();
        return icon;
    }
}
