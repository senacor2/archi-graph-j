package com.senacor.archigraph.render;

import com.senacor.archigraph.model.Application;

import java.awt.*;

class AppFormatter {

    static final Color SEA_GREEN = Color.decode("#378C96");
    static final Color LAWN_GREEN = Color.decode("#38761D");
    static final Color PINK = Color.decode("#EA9999");
    static final int FONT_SIZE = 12;

    void format(Application app, Rectangle rect) {
        if (app.getAttribute("replacedByTnr").equals("Yes")) {
            rect.setBackground(PINK);
            rect.setFontcolor(Color.BLACK);
        } else if (!app.getAttribute("market").equals("central")) {
            // Local application
            rect.setBackground(SEA_GREEN);
            rect.setFontcolor(Color.WHITE);
        } else if (app.getAttribute("status").equals("2026")) {
            // New application
            rect.setBackground(LAWN_GREEN);
            rect.setFontcolor(Color.WHITE);
        } else {
            // existing application
            rect.setBackground(Color.BLACK);
            rect.setFontcolor(Color.WHITE);
        }
        rect.setBordercolor(Color.BLACK);
        rect.setFontSize(FONT_SIZE);
        rect.setText(app.getName());
        rect.setRounded(true);
    }

    void formatProxy(Application app, Rectangle rect) {
        rect.setText(app.getName());
        rect.setFontSize(FONT_SIZE);
        rect.setBackground(Color.WHITE);
        rect.setFontcolor(Color.BLACK);
        rect.setBordercolor(Color.BLACK);
        rect.setRounded(true);
    }

    Rectangle[] getSamplesForLegend() {
        return new Rectangle[] {
                Rectangle.builder()
                        .background(PINK)
                        .fontcolor(Color.BLACK)
                        .fontSize(FONT_SIZE)
                        .rounded(true)
                        .text("Retired by TNR")
                        .build(),
                Rectangle.builder()
                        .background(SEA_GREEN)
                        .fontcolor(Color.WHITE)
                        .fontSize(FONT_SIZE)
                        .rounded(true)
                        .text("Local application")
                        .build(),
                Rectangle.builder()
                        .background(LAWN_GREEN)
                        .fontcolor(Color.WHITE)
                        .fontSize(FONT_SIZE)
                        .rounded(true)
                        .text("New central application")
                        .build(),
                Rectangle.builder()
                        .background(Color.BLACK)
                        .fontcolor(Color.WHITE)
                        .fontSize(FONT_SIZE)
                        .rounded(true)
                        .text("Existing central application")
                        .build(),
                Rectangle.builder()
                        .background(Color.WHITE)
                        .fontcolor(Color.BLACK)
                        .fontSize(FONT_SIZE)
                        .rounded(true)
                        .text("Integration across domains")
                        .build()
        };
    }
}
