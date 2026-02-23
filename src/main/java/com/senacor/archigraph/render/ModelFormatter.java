package com.senacor.archigraph.render;

import com.senacor.archigraph.model.Model;

import java.awt.*;

public class ModelFormatter {

    static final Color BG_COLOR_MODEL = new Color(0, 0, 156);
    static final Color FG_COLOR_MODEL = Color.WHITE;

    public void formatHead(Model model, Rectangle rect) {
        rect.setBackground(BG_COLOR_MODEL);
        rect.setFontcolor(FG_COLOR_MODEL);
        rect.setBordercolor(Color.BLACK);
        rect.setFontSize(96);
        rect.setFontStyle(FontStyle.BOLD);
    }
}
