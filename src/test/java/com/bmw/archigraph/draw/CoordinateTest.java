package com.bmw.archigraph.draw;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CoordinateTest {

    @Test
    public void testIndexToCoord() {
        assertEquals(new Coordinate(0, 0), Coordinate.fromIndex(3, 0));
        assertEquals(new Coordinate(0, 1), Coordinate.fromIndex(3, 1));
        assertEquals(new Coordinate(0, 2), Coordinate.fromIndex(3, 2));
        assertEquals(new Coordinate(1, 0), Coordinate.fromIndex(3, 3));
        assertEquals(new Coordinate(1, 1), Coordinate.fromIndex(3, 4));
        assertEquals(new Coordinate(1, 2), Coordinate.fromIndex(3, 5));
        assertEquals(new Coordinate(2, 0), Coordinate.fromIndex(3, 6));
        assertEquals(new Coordinate(2, 1), Coordinate.fromIndex(3, 7));
        assertEquals(new Coordinate(2, 2), Coordinate.fromIndex(3, 8));
    }

    @Test
    public void testDistance() {
        assertEquals(0.0, new Coordinate(1, 1).distance(new Coordinate(1, 1)));
        assertEquals(1.0, new Coordinate(1, 1).distance(new Coordinate(2, 1)));
        assertEquals(Math.sqrt(2.0), new Coordinate(1, 1).distance(new Coordinate(2, 2)));
    }
}
