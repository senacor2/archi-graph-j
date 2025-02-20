package com.bmw.archigraph.draw;

import com.bmw.archigraph.write.Writer;
import java.io.IOException;

public class DrawModel {

    public void write(String filename) throws IOException {
        new Writer(filename).write();
    }
}
