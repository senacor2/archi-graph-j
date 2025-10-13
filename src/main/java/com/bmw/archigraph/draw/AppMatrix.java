package com.bmw.archigraph.draw;

import com.bmw.archigraph.model.Application;

import java.util.Iterator;

public class AppMatrix {

    private final Application[][] apps;
    private final int rows;
    private final int columns;

    public AppMatrix(final int rows, final int columns) {
        apps = new Application[rows][columns];
        this.rows = rows;
        this.columns = columns;
    }

    public Application get(final Coordinate coord) {
        return apps[coord.row()][coord.col()];
    }

    public Application get(int row, int col) {
        return apps[row][col];
    }

    public Coordinate getAppCoordinate(final Application app) {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                if (apps[r][c] == app) {
                    return new Coordinate(r, c);
                }
            }
        }
        return null;
    }

    public void put(final Coordinate coord, final Application app) {
        put(coord.row(), coord.col(), app);
    }

    public void put(final int row, final int col, final Application app){
        if (apps[row][col] != null && apps[row][col] != app) {
            throw new IllegalArgumentException(String.format("Error putting %s. Slot is occupied by %s",
                    app.getId(), apps[row][col].getId()));
        }
        apps[row][col] = app;
    }

    /**
     * Merges another appmatrix into this. This matrix is updated to contain the apps at the positions
     * identified by the offset.
     * @param other Another app matrix.
     * @param rowOffset The other matrix will be placed at rowOffset from the top of this matrix.
     * @param colOffset The other matrix will be placed at colOffset from the left of this matrix.
     */
    public void merge(final AppMatrix other, int rowOffset, int colOffset) {
        for (int r = 0; r < other.rows; r++) {
            for (int c = 0; c < other.columns; c++) {
                put(r + rowOffset, c + colOffset, other.get(r, c));
            }
        }
    }

    public boolean allCellsEmptyHor(Coordinate src, Coordinate dst) {
        var fromCol = Math.min(src.col(), dst.col()) + 1;
        var toCol = Math.max(src.col(), dst.col());
        for (int col = fromCol; col < toCol; col++) {
            if (apps[src.row()][col] != null) return false;
        }
        return true;
    }

    public boolean allCellsEmptyVert(Coordinate src, Coordinate dst) {
        var fromRow = Math.min(src.row(), dst.row()) + 1;
        var toRow = Math.max(src.row(), dst.row());
        for (int row = fromRow; row < toRow; row++) {
            if (apps[row][src.col()] != null) return false;
        }
        return true;
    }

    public Iterable<Coordinate> usedCoordinates() {
        return () -> new Iterator<>() {
            private int currentIndex = -1;

            @Override
            public boolean hasNext() {
                return findNextUsedCell(currentIndex+1) < rows * columns;
            }

            @Override
            public Coordinate next() {
                currentIndex = findNextUsedCell(currentIndex+1);
                return Coordinate.fromIndex(columns, currentIndex);
            }

            private int findNextUsedCell(int startAt) {
                while (startAt < rows * columns && get(Coordinate.fromIndex(columns, startAt)) == null) {
                    startAt++;
                }
                return startAt;
            }
        };
    }

    public String dump() {
        StringBuilder sb = new StringBuilder(rows * (columns+2));
        for (int r = 0; r < rows; r++) {
            sb.append(r % 10);
            for (int c = 0; c < columns; c++) {
                if (apps[r][c] != null) {
                    sb.append('X');
                } else {
                    sb.append('.');
                }
            }
            sb.append("-\n");
        }
        return sb.toString();
    }

}
