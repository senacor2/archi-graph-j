package com.bmw.archigraph.draw;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CoordinateTest {

    @Test
    public void testIndexToCoord() {
        assertEquals(new Coordinate(0, 0), Coordinate.fromIndex(3, 0, 0, 0));
        assertEquals(new Coordinate(0, 1), Coordinate.fromIndex(3, 1, 0, 0));
        assertEquals(new Coordinate(0, 2), Coordinate.fromIndex(3, 2, 0, 0));
        assertEquals(new Coordinate(1, 0), Coordinate.fromIndex(3, 3, 0, 0));
        assertEquals(new Coordinate(1, 1), Coordinate.fromIndex(3, 4, 0, 0));
        assertEquals(new Coordinate(1, 2), Coordinate.fromIndex(3, 5, 0, 0));
        assertEquals(new Coordinate(2, 0), Coordinate.fromIndex(3, 6, 0, 0));
        assertEquals(new Coordinate(2, 1), Coordinate.fromIndex(3, 7, 0, 0));
        assertEquals(new Coordinate(2, 2), Coordinate.fromIndex(3, 8, 0, 0));
    }

    @Test
    public void testIndexToCoordWithOffset() {
        assertEquals(new Coordinate(2, 3), Coordinate.fromIndex(3, 0, 2, 3));
        assertEquals(new Coordinate(2, 4), Coordinate.fromIndex(3, 1, 2, 3));
        assertEquals(new Coordinate(2, 5), Coordinate.fromIndex(3, 2, 2, 3));
        assertEquals(new Coordinate(3, 3), Coordinate.fromIndex(3, 3, 2, 3));
        assertEquals(new Coordinate(3, 4), Coordinate.fromIndex(3, 4, 2, 3));
        assertEquals(new Coordinate(3, 5), Coordinate.fromIndex(3, 5, 2, 3));
        assertEquals(new Coordinate(4, 3), Coordinate.fromIndex(3, 6, 2, 3));
        assertEquals(new Coordinate(4, 4), Coordinate.fromIndex(3, 7, 2, 3));
        assertEquals(new Coordinate(4, 5), Coordinate.fromIndex(3, 8, 2, 3));
    }

    @Test
    public void testDistance() {
        assertEquals(0.0, new Coordinate(1, 1).distance(new Coordinate(1, 1)));
        assertEquals(1.0, new Coordinate(1, 1).distance(new Coordinate(2, 1)));
        assertEquals(Math.sqrt(2.0), new Coordinate(1, 1).distance(new Coordinate(2, 2)));
    }
}
