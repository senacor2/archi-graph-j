package com.senacor.archigraph.render;

import com.senacor.archigraph.model.Component;

import java.util.List;
import java.awt.Color;

public class ComponentFormatter {

    static final Color[] BG_COLOR_COMP_HEAD = {
            new Color(0x0b, 0x53, 0x94),
            new Color(0x6f, 0xa8, 0xdc),
            new Color(0xcf, 0xe2, 0xf3),
            new Color(0xef, 0xf2, 0xf3)
    };
    static final Color[] FONT_COLOR_COMP_HEAD = {
            Color.WHITE,
            Color.BLACK,
            Color.BLACK,
            Color.BLACK
    };
    static final Color BG_COLOR_COMP_BODY = Color.WHITE;
    static final Color FG_COLOR_COMP_BODY = Color.BLACK;


    public void formatHead(Component comp, Rectangle rect) {
        rect.setBackground(BG_COLOR_COMP_HEAD[comp.getLevel()-1]);
        rect.setFontcolor(FONT_COLOR_COMP_HEAD[comp.getLevel()-1]);
        rect.setBordercolor(Color.BLACK);
        rect.setFontSize(getFontSize(comp.getLevel()));
        rect.setFontStyle(FontStyle.BOLD);
    }

    private static int getFontSize(int level) {
        return switch (level) {
            case 1 -> 60;
            case 2 -> 48;
            case 3 -> 40;
            default -> 32;
        };
    }

    public void formatBody(Component comp, Rectangle rect) {
        rect.setBackground(BG_COLOR_COMP_BODY);
        rect.setBordercolor(FG_COLOR_COMP_BODY);
    }

    public Rectangle[] getSamplesForLegend(List<String> componentNames) {
        var result = new Rectangle[componentNames.size()];
        for (int i = 0; i < componentNames.size(); i++) {
            result[i] = Rectangle.builder()
                    .id("_Legend_" + componentNames.get(i).replace(' ', '_'))
                    .background(BG_COLOR_COMP_HEAD[i])
                    .bordercolor(Color.BLACK)
                    .fontcolor(FONT_COLOR_COMP_HEAD[i])
                    .fontSize(getFontSize(i+1))
                    .text(componentNames.get(i))
                    .layer(null)
                    .build();
        }
        return result;
    }
}
