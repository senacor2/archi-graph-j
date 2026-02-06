package com.senacor.archigraph.render;

import com.senacor.archigraph.draw.DrawModel;
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
    private Color fontcolor;
    private Color bordercolor;
    private String fillStyle;

    private int fontSize;

    private String text;

    private boolean rounded;

    /**
     * Only present for proxies. Contains the id of the rectangle that this object is a proxy for.
     * <code>null</code> otherwise.
     */
    private String originalId;

    @Override
    public void draw(DrawModel drawModel) {
        drawModel.draw(this);
    }

    public boolean isProxy() {
        return originalId != null;
    }
}
