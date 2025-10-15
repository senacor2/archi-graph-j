package com.senacor.archigraph.render;

import com.senacor.archigraph.draw.DrawModel;
import lombok.Builder;
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

    @Builder.Default
    private boolean hasStartArrow = false;
    @Builder.Default
    private boolean hasEndArrow = true;

    public void draw(DrawModel model) {
        model.draw(this);
    }
}
