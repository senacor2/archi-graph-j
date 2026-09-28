package com.senacor.archigraph.layout;

import com.senacor.archigraph.model.Component;

import java.util.HashMap;

public class EmptyComponentLayout extends AbstractComponentLayout {

    public EmptyComponentLayout(Component component) {
        super(component);
    }

    public void layout() {
        layout = new HashMap<>();
    }

    public int getQuality() {
        return 0;
    }
}
