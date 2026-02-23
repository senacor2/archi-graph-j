package com.senacor.archigraph.render;

public enum FontStyle {
    NORMAL(0),
    BOLD(1);

    public final int styleCode;

    FontStyle(int code) {
        styleCode = code;
    }
}
