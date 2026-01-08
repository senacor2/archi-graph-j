package com.senacor.archigraph.render;

import com.senacor.archigraph.model.Component;

import java.awt.*;

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
    }

    private static int getFontSize(int level) {
        return switch (level) {
            case 1 -> 48;
            case 2 -> 40;
            case 3 -> 32;
            default -> 24;
        };
    }

    public void formatBody(Component comp, Rectangle rect) {
        rect.setBackground(BG_COLOR_COMP_BODY);
        rect.setBordercolor(FG_COLOR_COMP_BODY);
    }

    public Rectangle[] getSamplesForLegend() {
        return new Rectangle[] {
                Rectangle.builder()
                        .background(BG_COLOR_COMP_HEAD[0])
                        .fontcolor(FONT_COLOR_COMP_HEAD[0])
                        .fontSize(getFontSize(1))
                        .text("Domain")
                        .build(),
                Rectangle.builder()
                        .background(BG_COLOR_COMP_HEAD[1])
                        .fontcolor(FONT_COLOR_COMP_HEAD[1])
                        .fontSize(getFontSize(2))
                        .text("Product")
                        .build(),
                Rectangle.builder()
                        .background(BG_COLOR_COMP_HEAD[2])
                        .fontcolor(FONT_COLOR_COMP_HEAD[2])
                        .fontSize(getFontSize(3))
                        .text("Subproduct")
                        .build()
        };
    }
}
