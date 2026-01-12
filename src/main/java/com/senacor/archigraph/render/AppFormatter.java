package com.senacor.archigraph.render;

import com.senacor.archigraph.model.Application;
import com.senacor.archigraph.rules.RuleBase;

import java.awt.*;
import java.io.IOException;
import java.util.Map;

class AppFormatter {

    static final Color SEA_GREEN = Color.decode("#378C96");
    static final Color LAWN_GREEN = Color.decode("#38761D");
    static final Color PINK = Color.decode("#EA9999");
    static final int FONT_SIZE = 12;
    static final Map<String, String> DEFAULT_COLORS = Map.of("backgroundColor", "#000000",
            "fontColor", "#FFFFFF", "borderColor", "#000000");

    private final RuleBase ruleBase = new RuleBase();

    public void loadRules(String ruleFileName) throws IOException {
        ruleBase.load(ruleFileName);
    }

    void format(Application app, Rectangle rect) {
        var result = ruleBase.evaluate(app).orElse(DEFAULT_COLORS);
        var bgColor = Color.decode(result.get("backgroundColor"));
        var fontColor = Color.decode(result.get("fontColor"));
        var borderColor = Color.decode(result.get("borderColor"));
        rect.setBackground(bgColor);
        rect.setFontcolor(fontColor);
        rect.setBordercolor(borderColor);
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
        return new Rectangle[]{
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
