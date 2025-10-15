package com.senacor.archigraph.draw;

import com.senacor.archigraph.render.Line;
import com.senacor.archigraph.render.Rectangle;
import com.senacor.archigraph.render.RenderModel;
import java.io.IOException;

public interface DrawModel {

    DrawModel draw(RenderModel renderModel);

    void draw(Rectangle rect);

    void draw(Line line);

    void write(String filename) throws IOException;
}
