package com.senacor.archigraph.render;

import com.senacor.archigraph.draw.DrawModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@AllArgsConstructor
public abstract class RenderModelElement {

    /**
     * Unique id of the element.
     * Used by the underlying graphics system to identify the object.
     */
    String id;

    /**
     * Name of the layer this object is assigned to. When null, the default layer is used.
     */
    String layer;

    public abstract void draw(DrawModel drawModel);

}
