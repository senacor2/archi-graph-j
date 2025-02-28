package com.bmw.archigraph.draw;

public record Coordinate(
        int row,
        int col
) {
    static Coordinate fromIndex(final int columns, final int index) {
        return new Coordinate(index / columns, index % columns);
    }

    public int getRow() { return row; }

    public int getCol() { return col; }
}
