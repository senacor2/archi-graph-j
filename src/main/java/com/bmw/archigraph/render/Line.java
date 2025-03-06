package com.bmw.archigraph.render;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.awt.*;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class Line extends RenderModelElement {

    private Rectangle start;
    private Rectangle end;

    private Point[] anchors;

    private String text;
}
