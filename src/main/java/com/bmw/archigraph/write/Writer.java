package com.bmw.archigraph.write;

import java.io.FileWriter;
import java.io.IOException;

public class Writer {

    private final String filename;

    public Writer(String filename) {
        this.filename = filename;
    }

    public void write() throws IOException {
        try (java.io.Writer out = new FileWriter(filename)) {
            out.append("nur ein Test");
        }
    }
}
