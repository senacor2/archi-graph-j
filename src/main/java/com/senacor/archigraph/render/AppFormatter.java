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
    static final String BACKGROUND_COLOR = "backgroundColor";
    static final String FONT_COLOR = "fontColor";
    static final String BORDER_COLOR = "borderColor";
    static final String FILL_STYLE = "fillStyle";
    static final Map<String, String> DEFAULT_COLORS = Map.of(BACKGROUND_COLOR, "#000000",
            FONT_COLOR, "#FFFFFF", BORDER_COLOR, "#000000", FILL_STYLE, "solid");
    static final RuleBase DEFAULT_RULEBASE = new RuleBase(List.of("id", "market", "target", "replacedByTnr",
            "connectItStatus"), DEFAULT_COLORS);
    public static final String IS_PROXY = "isProxy";

    private RuleBase ruleBase = DEFAULT_RULEBASE;

    void format(Application app, Rectangle rect) {
        var result = ruleBase.evaluate(app, Map.of(IS_PROXY, "No")).orElse(DEFAULT_COLORS);
        var bgColor = Color.decode(result.get(BACKGROUND_COLOR));
        var fontColor = Color.decode(result.get(FONT_COLOR));
        var borderColor = Color.decode(result.get(BORDER_COLOR));
        var fillStyle = result.get(FILL_STYLE);
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
        var result = ruleBase.evaluate(app, Map.of(IS_PROXY, "Yes")).orElse(DEFAULT_COLORS);
        var bgColor = Color.decode(result.get(BACKGROUND_COLOR));
        var fontColor = Color.decode(result.get(FONT_COLOR));
        var borderColor = Color.decode(result.get(BORDER_COLOR));
        var fillStyle = result.get(FILL_STYLE);
        rect.setText(app.getName());
        rect.setFontSize(FONT_SIZE);
        rect.setBackground(bgColor);
        rect.setFontcolor(fontColor);
        rect.setBordercolor(borderColor);
        rect.setFillStyle(fillStyle);
        rect.setRounded(true);
    }

    List<Rectangle> getSamplesForLegend() {
        List<Rectangle> result = new LinkedList<>();
        ruleBase.getNamedResultMap().forEach((k, v) ->
                result.add(Rectangle.builder()
                        .background(Color.decode(v.get(BACKGROUND_COLOR)))
                        .fontcolor(Color.decode(v.get(FONT_COLOR)))
                        .bordercolor(Color.decode(v.get(BORDER_COLOR)))
                        .fillStyle(v.get(FILL_STYLE))
                        .fontSize(FONT_SIZE)
                        .rounded(true)
                        .text(k)
                        .build()));
        return result;
    }
}

