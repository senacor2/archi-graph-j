package com.senacor.archigraph.model;

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
        int rows = comp.getHeight() + 2 * comp.getProxyAreaSize();
        int columns = comp.getWidth() + 2 * comp.getProxyAreaSize();
        layout = new HashMap<>(rows * columns);
        proxyBoxCoords = new LinkedList<>();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                if (isInProxyArea(rows, columns, comp.getProxyAreaSize(), r, c)) {
                    proxyBoxCoords.add(new Coordinate(r, c));
                }
            }
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
        if (result == null) {
            log.error("Could not find an empty proxy cell");
            throw new IllegalArgumentException("No empty proxy cell");
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

    /**
     * Check if the given coordinate is in the proxyArea or in the compArea.
     * @param maxRows Number of rows of the proxyArea.
     * @param maxColumns Number of columns of the proxyArea.
     * @param proxyAreaSize the thickness of the proxyArea
     * @param row the row to check
     * @param column the column to check
     * @return true if row/column are outside the component area.
     */
    private static boolean isInProxyArea(int maxRows, int maxColumns, int proxyAreaSize, int row, int column) {
        return row < proxyAreaSize || row >= maxRows - proxyAreaSize ||
                column < proxyAreaSize || column >= maxColumns - proxyAreaSize;
    }
}
