package com.bmw.archigraph.draw;

import com.bmw.archigraph.render.RenderModel;
import com.bmw.archigraph.write.Writer;
import java.io.IOException;

public class DrawModel {

    public DrawModel draw(RenderModel renderModel) {
        return this;
    }

    public DrawModel write(String filename) throws IOException {
        new Writer(filename).write();
        return this;
    }
}
