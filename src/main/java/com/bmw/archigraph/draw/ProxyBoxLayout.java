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

    /**
     * Find the nearest empty cell in the proxy box w.r.t. the local component.
     * The coordinate system belongs to the proxy box, i.e. the component box
     * is one unit smaller on each side.
     * @param coord Row column position of an app inside the component.
     * @return A row column position as close as possible to the component.
     */
    public Coordinate findNearestEmptyCellOld(Coordinate coord) {
        int newRow;
        int newColumn;
        if (coord.col() == 1) {
            newColumn = 0;
            newRow = coord.row();
        } else if (coord.col() == columns - 2) {
            newColumn = columns - 1;
            newRow = coord.row();
        } else if (coord.row() == 1) {
            newColumn = coord.col();
            newRow = 0;
        } else if (coord.row() == rows - 2) {
            newColumn = coord.col();
            newRow = rows - 1;
        } else if (coord.col() < columns / 2) {
            newColumn = 0;
            newRow = coord.row();
        } else if (coord.col() >= columns / 2) {
            newColumn = columns - 1;
            newRow = coord.row();
        } else {
            newColumn = 0;
            newRow = 0;
        }
        Coordinate newCoord = new Coordinate(newRow, newColumn);
        while (getApplicationAt(newCoord) != null) {
            if (newCoord.row() < rows) {
                newCoord = newCoord.oneBelow();
            } else {
                newCoord = newCoord.oneRight();
            }
        }
        return newCoord;
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
