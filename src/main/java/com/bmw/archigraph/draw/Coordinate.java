package com.bmw.archigraph.draw;

/**
 * Row/column address inside the drawing grid.
 * @param row
 * @param col
 */
public record Coordinate(
        int row,
        int col
) implements Comparable<Coordinate>{

    static Coordinate fromIndex(final int columns, final int index, final int rowOffset, final int colOffset) {
        return new Coordinate(index / columns + rowOffset, index % columns + colOffset);
    }

    /**
     * Compares two coordinates where order is defined as the smaller being higher up
     * and further left in the grid
     * @param other the object to be compared.
     * @return -1 if this object is higher than the other, 1 if opposite and
     * if both are in the same row, -1 if this object is more left than the other and
     * 1 if opposite and 0 if both coordinates are equal.
     */
    public int compareTo(Coordinate other) {
        if (row < other.row) return -1;
        else if (row > other.row) return 1;
        else return Integer.compare(col, other.col);
    }

    /**
     * Returns the euclidian distance between two coordinates
     * @param other the other coordinate
     * @return the distance between this coordinate and the other.
     */
    public double distance(Coordinate other) {
        return Math.sqrt((row - other.row) * (row - other.row) + (col - other.col) * (col - other.col));
    }

    public Coordinate oneBelow() {
        return new Coordinate(row+1, col);
    }

    public Coordinate oneRight() {
        return new Coordinate(row, col+1);
    }
}
