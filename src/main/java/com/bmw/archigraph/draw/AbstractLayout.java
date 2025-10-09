package com.bmw.archigraph.draw;

import com.bmw.archigraph.model.Application;
import com.bmw.archigraph.model.Component;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
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

    public Set<Coordinate> getUsedCells() {
        return layout.values().stream()
                .map(c -> new Coordinate(c, component.getAppRowOffset(), component.getAppColOffset()))
                .collect(Collectors.toSet());
    }

    public String dumpLayout() {
        var buf = new StringBuilder();
        var usedCells = getUsedCells();
        for (int r = 0; r < component.getHeight(); r++) {
            buf.append(r % 10);
            for (int c = 0; c < component.getWidth(); c++) {
                if (usedCells.contains(new Coordinate(r, c))) {
                    buf.append('X');
                } else {
                    buf.append('.');
                }
            }
            buf.append("-\n");
        }
        return buf.toString();
    }

    /**
     * Add the layout of a subcomponent to this layout with the offset specified.
     * Note again, that layouts know nothing about where in the component the apps are drawn.
     * The offsets given must provide this information.
     *
     * @param subCompLayout layout of a subcomponent
     * @param row           vertical offset of the subcomponents app area
     * @param col           horizontal offset of the subcomponent app area
     */
    public void add(AbstractLayout subCompLayout, int row, int col) {
        log.debug("Adding layout for {} to {}", subCompLayout.getComponent().getName(), component.getName());
        log.trace("Before add layout = \n{}", dumpLayout());
        log.trace("Adding layout at {}/{} = \n{}", row, col, subCompLayout.dumpLayout());
        layout.putAll(subCompLayout.layout.entrySet().stream()
                .map(e ->
                        new AppCoordinate(e.getKey(),
                                new Coordinate(e.getValue().row() + row,
                                        e.getValue().col() + col)))
                .collect(Collectors.toMap(AppCoordinate::app, AppCoordinate::coord)));
        log.trace("Resulting layout = \n{}", dumpLayout());
    }

    protected Component getComponent() {
        return component;
    }

    public Stream<Map.Entry<Application, Coordinate>> stream() {
        return layout.entrySet().stream();
    }

    protected record AppCoordinate(Application app, Coordinate coord) {
    }
}
