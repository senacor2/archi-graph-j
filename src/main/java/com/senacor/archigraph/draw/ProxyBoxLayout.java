package com.senacor.archigraph.draw;

import com.senacor.archigraph.model.Application;
import com.senacor.archigraph.model.Component;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@Slf4j
public class ProxyBoxLayout extends AbstractLayout {

    @Getter
    private final List<Coordinate> proxyBoxCoords;

    public ProxyBoxLayout(Component comp) {
        super(comp);
        int rows = comp.getHeight() + 2;
        int columns = comp.getWidth() + 2;
        layout = new HashMap<>(rows * columns);
        proxyBoxCoords = new LinkedList<>();
        for (int c = 0; c < columns; c++) {
            proxyBoxCoords.add(new Coordinate(0, c));
            proxyBoxCoords.add(new Coordinate(rows - 1, c));
        }
        for (int r = 1; r < rows - 1; r++) {
            proxyBoxCoords.add(new Coordinate(r, 0));
            proxyBoxCoords.add(new Coordinate(r, columns - 1));
        }
    }

    /**
     * Find an empty position in the proxy box that is closest to <code>coord</code>.
     * @param coord Position of an app inside the component box.
     * @return Position for a proxy app.
     */
    public Coordinate findNearestEmptyCell(final Coordinate coord) {
        log.debug("Find nearest proxy for {}", coord);
        double minDist = 10000000000.0;
        Coordinate result = null;
        for (Coordinate candidate : proxyBoxCoords) {
            double newDist = coord.distance(candidate);
            if (newDist < minDist) {
                minDist = newDist;
                result = candidate;
            }
        }
        proxyBoxCoords.remove(result);
        log.debug("Found {}", result);
        return result;
    }

    /**
     * Allocate a cell to a proxy application.
     * @param proxy An Application.
     * @param proxyCoord The cell coordinate of app.
     */
    public void setProxyPosition(Application proxy, Coordinate proxyCoord) {
        layout.put(proxy, proxyCoord);
    }
}
