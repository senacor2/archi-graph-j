package com.bmw.archigraph.draw;

import com.bmw.archigraph.model.Application;
import com.bmw.archigraph.model.Component;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.stream.Stream;


/**
 * Base class for layouts that map applications to coordinates.
 * A layout always uses a coordinate system with 0,0 being the top left. It is the component's task
 * to project it relative to its own coordinates.
 */
@Slf4j
public abstract class AbstractLayout {

    protected final Component component;
    protected Map<Application, Coordinate> layout;

    public AbstractLayout(Component comp) {
        component = comp;
    }

    /**
     * Returns the row column position of an app as defined by the layout.
     *
     * @param app An Application.
     * @return The coordinate of the app. Will return <code>null</code> if the layout does not contain the app.
     */
    public Coordinate getAppCoordinate(Application app) {
        return layout.get(app);
    }

    public Stream<Map.Entry<Application, Coordinate>> stream() {
        return layout.entrySet().stream();
    }

    protected record AppCoordinate(Application app, Coordinate coord) {
    }
}
