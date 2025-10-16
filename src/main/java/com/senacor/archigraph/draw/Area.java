package com.senacor.archigraph.draw;

public record Area(
        int row,
        int col,
        int width,
        int height
) {
    /**
     * Check if this area overlaps other. If they are directly adjacent this is not considered to overlap.
     * @param other An area to be checked.
     * @return true if the areas overlap and false otherwise.
     */
    public boolean overlap(Area other) {
        return !(col >= other.col + other.width || // this is right of other
                col + width <= other.col ||        // this is left of other
                row >= other.row + other.height || // this is below other
                row + height <= other.row);        // this is above other
    }

    /**
     * Check if this area is separated by at least minDistance cells from other.
     * @param other Another area to be checked.
     * @param minDistance the minimum distance between the two areas.
     * @return true if the minimum distance is respected and false otherwise.
     */
    public boolean hasMinDistance(Area other, int minDistance) {
        return col >= other.col + other.width + minDistance ||   // this is right of other
                col + width + minDistance <= other.col ||        // this is left of other
                row >= other.row + other.height + minDistance || // this is below other
                row + height + minDistance <= other.row;         // this is above other
    }

    /**
     * Check if this area fully contains other.
     * @param other the possibly contained area.
     * @return true if this are fully contains the other area.
     */
    public boolean contains(Area other) {
        return row <= other.row &&
                row + height >= other.row + other.height &&
                col <= other.col &&
                col + width >= other.col + other.width;
    }

    public Area shifted(int rowOffset, int colOffset) {
        return new Area(row + rowOffset, col + colOffset, width, height);
    }
}
