package com.senacor.archigraph.render;

import com.senacor.archigraph.model.Component;

import java.awt.*;

public class ComponentFormatter {

    private static final Color BG_COLOR_COMP_HEAD = new Color(0, 0, 110);
    private static final Color FG_COLOR_COMP_HEAD = Color.WHITE;
    private static final Color BG_COLOR_COMP_BODY = Color.WHITE;
    private static final Color FG_COLOR_COMP_BODY = Color.WHITE;



    /**
     * Returns the color of the component header which is brighter with each level.
     * @param level Level of the component
     * @return Background color for the component header.
     */
    static Color headerBgColor(int level) {
        Color result = BG_COLOR_COMP_HEAD;
        for (int i = level; i > 1; i--){
            result = result.brighter();
        }
        return result;
    }

    public void formatHead(Component comp, Rectangle rect) {
        rect.setBackground(headerBgColor(comp.getLevel()));
        rect.setFontcolor(FG_COLOR_COMP_HEAD);
        rect.setBordercolor(Color.BLACK);
        rect.setFontSize(switch (comp.getLevel()) {
            case 1 -> 48;
            case 2 -> 40;
            case 3 -> 32;
            default -> 24;
        });
    }

    public void formatBody(Component comp, Rectangle rect) {
        rect.setBackground(BG_COLOR_COMP_BODY);
        rect.setBordercolor(FG_COLOR_COMP_BODY);
    }
}
