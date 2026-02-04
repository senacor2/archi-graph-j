package com.senacor.archigraph.model;

import lombok.extern.slf4j.Slf4j;


/**
 * Base class for layouts that map applications to coordinates.
 * A layout always uses a coordinate system with 0,0 being the top left. It is the component's task
 * to project it relative to its own coordinates.
 */
@Slf4j
public abstract class AbstractLayout {

    protected final Component component;

    public AbstractLayout(Component comp) {
        component = comp;
    }

    /**
     * Transfer the app layout created in this app into the appMatrix.
     * @param appMatrix apps and coordinates go here.
     */
    abstract void fillInto(AppMatrix appMatrix);

    protected record AppCoordinate(Application app, Coordinate coord) {
    }
}
