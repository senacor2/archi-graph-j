package com.bmw.archigraph.draw;

import com.bmw.archigraph.model.Application;
import com.bmw.archigraph.model.Component;
import lombok.Getter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ProxyBoxLayout extends AbstractLayout {

    private final int rows;
    private final int columns;
    @Getter
    private final List<Coordinate> proxyBoxCoords;

    public ProxyBoxLayout(Component comp) {
        super(comp);
        rows = comp.getHeight() + 2;
        columns = comp.getWidth() + 2;
        layout = new HashMap<>(rows * columns);
        add(comp.getLayout(), 1, 1);
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

    public Coordinate findNearestEmptyCell(Coordinate coord) {
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
