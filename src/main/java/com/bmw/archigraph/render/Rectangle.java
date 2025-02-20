package com.bmw.archigraph.render;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.awt.*;

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
}
