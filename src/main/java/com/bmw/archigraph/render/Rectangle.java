package com.bmw.archigraph.render;

import com.bmw.archigraph.draw.DrawModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.awt.*;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class Rectangle extends RenderModelElement {

    private int x;
    private int y;
    private int w;
    private int h;

    private Color background;
    private Color foreground;

    private int fontSize;

    private String text;

    private boolean rounded;

    @Override
    public void draw(DrawModel drawModel) {
        drawModel.draw(this);
    }
}
