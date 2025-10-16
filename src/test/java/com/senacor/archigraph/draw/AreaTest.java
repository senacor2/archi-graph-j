package com.senacor.archigraph.draw;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AreaTest {

    @Test
    void testOverlap() {
        var a1 = new Area(4, 4, 2, 2);

        // same row - horizontal
        assertFalse(a1.overlap(new Area(4, 2, 2, 2)));
        assertTrue(a1.overlap(new Area(4, 3, 2, 2)));
        assertTrue(a1.overlap(new Area(4, 4, 2, 2)));
        assertTrue(a1.overlap(new Area(4, 5, 2, 2)));
        assertFalse(a1.overlap(new Area(4, 6, 2, 2)));

        // same col - vertical
        assertFalse(a1.overlap(new Area(2, 4, 2, 2)));
        assertTrue(a1.overlap(new Area(3, 4, 2, 2)));
        assertTrue(a1.overlap(new Area(5, 4, 2, 2)));
        assertFalse(a1.overlap(new Area(6, 4, 2, 2)));

        // test corners
        assertFalse(a1.overlap(new Area(2, 2, 2, 2)));
        assertFalse(a1.overlap(new Area(6, 2, 2, 2)));
        assertFalse(a1.overlap(new Area(6, 6, 2, 2)));
        assertFalse(a1.overlap(new Area(2, 6, 2, 2)));

        // test others
        assertFalse(a1.overlap(new Area(0, 0, 2, 2)));
        assertTrue(a1.overlap(new Area(0, 0, 10, 10)));
    }

    @Test
    void testMinDistance() {
        var a1 = new Area(4, 4, 2, 2);

        // same row - horizontal
        assertTrue(a1.hasMinDistance(new Area(4, 0, 2, 2), 2));
        assertFalse(a1.hasMinDistance(new Area(4, 1, 2, 2), 2));
        assertFalse(a1.hasMinDistance(new Area(4, 2, 2, 2), 2));
        assertFalse(a1.hasMinDistance(new Area(4, 3, 2, 2), 2));
        assertFalse(a1.hasMinDistance(new Area(4, 4, 2, 2), 2));
        assertFalse(a1.hasMinDistance(new Area(4, 5, 2, 2), 2));
        assertFalse(a1.hasMinDistance(new Area(4, 6, 2, 2), 2));
        assertFalse(a1.hasMinDistance(new Area(4, 7, 2, 2), 2));
        assertTrue(a1.hasMinDistance(new Area(4, 8, 2, 2), 2));

        // same col - vertical
        assertTrue(a1.hasMinDistance(new Area(0, 4, 2, 2), 2));
        assertFalse(a1.hasMinDistance(new Area(1, 4, 2, 2), 2));
        assertFalse(a1.hasMinDistance(new Area(2, 4, 2, 2), 2));
        assertFalse(a1.hasMinDistance(new Area(3, 4, 2, 2), 2));
        assertFalse(a1.hasMinDistance(new Area(4, 4, 2, 2), 2));
        assertFalse(a1.hasMinDistance(new Area(5, 4, 2, 2), 2));
        assertFalse(a1.hasMinDistance(new Area(6, 4, 2, 2), 2));
        assertFalse(a1.hasMinDistance(new Area(7, 4, 2, 2), 2));
        assertTrue(a1.hasMinDistance(new Area(8, 4, 2, 2), 2));

        // assert different row - horizontal
        assertTrue(a1.hasMinDistance(new Area(0, 0, 2, 2), 2));
        assertTrue(a1.hasMinDistance(new Area(0, 1, 2, 2), 2));
        assertTrue(a1.hasMinDistance(new Area(0, 2, 2, 2), 2));
        assertTrue(a1.hasMinDistance(new Area(0, 3, 2, 2), 2));
        assertTrue(a1.hasMinDistance(new Area(0, 4, 2, 2), 2));
        assertTrue(a1.hasMinDistance(new Area(0, 5, 2, 2), 2));
        assertTrue(a1.hasMinDistance(new Area(0, 6, 2, 2), 2));
        assertTrue(a1.hasMinDistance(new Area(0, 7, 2, 2), 2));
        assertTrue(a1.hasMinDistance(new Area(0, 8, 2, 2), 2));

        // other
        assertFalse(new Area(1, 35, 2, 3)
                .hasMinDistance(new Area(4, 35, 2, 3), 2));
    }

    @Test
    void testContains() {
        var a1 = new Area(4, 4, 4, 4);

        // test inside
        assertTrue(a1.contains(new Area(4, 4, 2, 2)));
        assertTrue(a1.contains(new Area(4, 5, 2, 2)));
        assertTrue(a1.contains(new Area(4, 6, 2, 2)));
        assertTrue(a1.contains(new Area(5, 4, 2, 2)));
        assertTrue(a1.contains(new Area(5, 5, 2, 2)));
        assertTrue(a1.contains(new Area(5, 6, 2, 2)));
        assertTrue(a1.contains(new Area(6, 4, 2, 2)));
        assertTrue(a1.contains(new Area(6, 5, 2, 2)));
        assertTrue(a1.contains(new Area(6, 6, 2, 2)));

        // assert on border
        assertFalse(a1.contains(new Area(3, 3, 2, 2)));
        assertFalse(a1.contains(new Area(3, 4, 2, 2)));
        assertFalse(a1.contains(new Area(3, 5, 2, 2)));
        assertFalse(a1.contains(new Area(3, 6, 2, 2)));
        assertFalse(a1.contains(new Area(7, 3, 2, 2)));
        assertFalse(a1.contains(new Area(4, 3, 2, 2)));
        assertFalse(a1.contains(new Area(4, 7, 2, 2)));
        assertFalse(a1.contains(new Area(5, 3, 2, 2)));
        assertFalse(a1.contains(new Area(6, 3, 2, 2)));
        assertFalse(a1.contains(new Area(6, 7, 2, 2)));
        assertFalse(a1.contains(new Area(7, 3, 2, 2)));
        assertFalse(a1.contains(new Area(7, 4, 2, 2)));
        assertFalse(a1.contains(new Area(7, 5, 2, 2)));
        assertFalse(a1.contains(new Area(7, 6, 2, 2)));

        // assert outside
        assertFalse(a1.contains(new Area(0, 0, 2, 2)));
        assertFalse(a1.contains(new Area(1, 1, 2, 2)));
        assertFalse(a1.contains(new Area(2, 2, 2, 2)));
    }
}
