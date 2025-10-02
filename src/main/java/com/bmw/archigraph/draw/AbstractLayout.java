package com.bmw.archigraph.draw;

import com.bmw.archigraph.model.Application;
import com.bmw.archigraph.model.Component;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractLayout {

    protected final Component component;
    protected Map<Application, Coordinate> layout;

    public AbstractLayout(Component comp) {
        component = comp;
    }

    /**
     * Returns the row column position of an app as defined by the layout.
     * @param app An Application.
     * @return The coordinate of the app. Will return <code>null</code> if the layout does not contain the app.
     */
    public Coordinate getAppCoordinate(Application app) {
        return layout.get(app);
    }

    public Set<Coordinate> getUsedCells() {
        return new HashSet<>(layout.values());
    }

    public String dumpLayout() {
        var buf = new StringBuilder();
        var usedCells = getUsedCells();
        for (int r = 0; r < component.getHeight(); r++) {
            buf.append('-');
            for (int c = 0; c < component.getWidth(); c++) {
                if (usedCells.contains(new Coordinate(r, c))) {
                    buf.append('X');
                } else {
                    buf.append(' ');
                }
            }
            buf.append("-\n");
        }
        return buf.toString();
    }

    public void add(Coordinate coord, Application a) {
        if (layout == null) layout = new HashMap<>();
        layout.put(a, coord);
    }

    /**
     * Add the layout of a subcomponent to this layout with the offset specified.
     *
     * @param subCompLayout layout of a subcomponent
     * @param row     vertical offset of the subcomponent
     * @param col     horizontal offset of the subcomponent
     */
    public void add(AbstractLayout subCompLayout, int row, int col) {
        log.debug("Adding layout for {} to {}", subCompLayout.getComponent().getName(), component.getName());
        log.trace("Before add layout = \n{}", dumpLayout());
        log.trace("Adding layout at {}/{} = \n{}", row, col, subCompLayout.dumpLayout());
        layout.putAll(subCompLayout.layout.entrySet().stream()
                .map(e ->
                        new AppCoordinate(e.getKey(),
                                new Coordinate(e.getValue().row() + row, e.getValue().col() + col)))
                .collect(Collectors.toMap(AppCoordinate::app, AppCoordinate::coord)));
        log.trace("Resulting layout = \n{}", dumpLayout());
    }

    protected Component getComponent() {
        return component;
    }

    protected Application getApplicationAt(Coordinate coord) {
        return layout.entrySet().stream()
                .filter(e -> e.getValue().equals(coord))
                .findFirst()
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    protected record AppCoordinate(Application app, Coordinate coord) {}
}
