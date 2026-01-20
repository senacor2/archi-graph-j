package com.senacor.archigraph.render;

import com.senacor.archigraph.model.Application;
import com.senacor.archigraph.rules.RuleBase;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
class AppFormatter {

    static final Color SEA_GREEN = Color.decode("#378C96");
    static final Color LAWN_GREEN = Color.decode("#38761D");
    static final Color PINK = Color.decode("#EA9999");
    static final int FONT_SIZE = 12;
    static final Map<String, String> DEFAULT_COLORS = Map.of("backgroundColor", "#000000",
            "fontColor", "#FFFFFF", "borderColor", "#000000", "fillStyle", "solid");
    static final RuleBase DEFAULT_RULEBASE = new RuleBase(List.of("id", "market", "target", "replacedByTnr",
            "connectItStatus"), DEFAULT_COLORS);

    private RuleBase ruleBase = DEFAULT_RULEBASE;

    void format(Application app, Rectangle rect) {
        var result = ruleBase.evaluate(app).orElse(DEFAULT_COLORS);
        var bgColor = Color.decode(result.get("backgroundColor"));
        var fontColor = Color.decode(result.get("fontColor"));
        var borderColor = Color.decode(result.get("borderColor"));
        var fillStyle = result.get("fillStyle");
        rect.setBackground(bgColor);
        rect.setFillStyle(fillStyle);
        rect.setFontcolor(fontColor);
        rect.setBordercolor(borderColor);
        rect.setFontSize(FONT_SIZE);
        rect.setText(app.getName());
        rect.setRounded(true);
    }

    void setRuleBase(RuleBase ruleBase) {
        log.debug("Setting rulebase {}", ruleBase);
        if (ruleBase != null) {
            this.ruleBase = ruleBase;
        }
    }

    void formatProxy(Application app, Rectangle rect) {
        rect.setText(app.getName());
        rect.setFontSize(FONT_SIZE);
        rect.setBackground(Color.WHITE);
        rect.setFontcolor(Color.BLACK);
        rect.setBordercolor(Color.BLACK);
        rect.setFontSize(FONT_SIZE);
        rect.setRounded(true);
    }

    List<Rectangle> getSamplesForLegend() {
        List<Rectangle> result = new LinkedList<>();
        ruleBase.getNamedResultMap().forEach((k, v) ->
                result.add(Rectangle.builder()
                        .background(Color.decode(v.get("backgroundColor")))
                        .fontcolor(Color.decode(v.get("fontColor")))
                        .bordercolor(Color.decode(v.get("borderColor")))
                        .fillStyle(v.get("fillStyle"))
                        .fontSize(FONT_SIZE)
                        .rounded(true)
                        .text(k)
                        .build()));
        result.add(Rectangle.builder()
                .background(Color.WHITE)
                .fontcolor(Color.BLACK)
                .bordercolor(Color.BLACK)
                .fontSize(FONT_SIZE)
                .rounded(true)
                .text("Integration across domains")
                .build());
        return result;
    }
}

