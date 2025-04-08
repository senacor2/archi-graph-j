package com.bmw.archigraph.draw;

import com.bmw.archigraph.render.Line;
import com.bmw.archigraph.render.Rectangle;
import com.bmw.archigraph.render.RenderModel;
import java.io.IOException;

public interface DrawModel {

    DrawModel draw(RenderModel renderModel);

    void draw(Rectangle rect);

    void draw(Line line);

    void write(String filename) throws IOException;
}
