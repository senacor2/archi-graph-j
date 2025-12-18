package com.senacor.archigraph.render;

import com.senacor.archigraph.model.Application;

import java.awt.*;

class AppFormatter {

    static final Color SEA_GREEN = Color.decode("#378C96");
    static final Color LAWN_GREEN = Color.decode("#38761D");
    static final Color PINK = Color.decode("#EA9999");

    void format(Application app, Rectangle rect) {
        if (app.getAttribute(Application.REPLACE_TNR).equals("Yes")) {
            rect.setBackground(PINK);
            rect.setFontcolor(Color.BLACK);
        } else if (!app.getAttribute(Application.MARKET).equals("central")) {
            // Local application
            rect.setBackground(SEA_GREEN);
            rect.setFontcolor(Color.WHITE);
        } else if (app.getAttribute(Application.TARGET).equals("2026")) {
            // New application
            rect.setBackground(LAWN_GREEN);
            rect.setFontcolor(Color.WHITE);
        } else {
            // existing application
            rect.setBackground(Color.BLACK);
            rect.setFontcolor(Color.WHITE);
        }
        rect.setBordercolor(Color.BLACK);
        rect.setFontSize(12);
        rect.setText(app.getName());
        rect.setRounded(true);
    }

    void formatProxy(Application app, Rectangle rect) {
        rect.setText(app.getName());
        rect.setFontSize(12);
        rect.setBackground(Color.WHITE);
        rect.setFontcolor(Color.BLACK);
        rect.setBordercolor(Color.BLACK);
        rect.setRounded(true);
    }
}
