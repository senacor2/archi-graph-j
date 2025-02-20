package com.bmw.archigraph.render;

import com.bmw.archigraph.draw.DrawModel;
import lombok.Data;

import java.util.List;

@Data
public class RenderModel {

    private List<RenderModelElement> elements;

    public RenderModel add(RenderModelElement element) {
        elements.add(element);
        return this;
    }

    public DrawModel draw() {
        return new DrawModel();
    }
}
