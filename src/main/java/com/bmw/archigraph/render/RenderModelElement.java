package com.bmw.archigraph.render;

import com.bmw.archigraph.draw.DrawModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@AllArgsConstructor
public abstract class RenderModelElement {

    String id;

    public abstract void draw(DrawModel drawModel);

}
