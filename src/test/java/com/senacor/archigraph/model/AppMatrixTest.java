package com.senacor.archigraph.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AppMatrixTest {

    @Test
    void testPutAndGet() {
        var fixture = new AppMatrix(3, 3);
        var app = new Application("A1", "App1", "Comp1", "", "", "");
        fixture.put(2, 2, app);
        assertEquals(app, fixture.get(2, 2));
        assertEquals(new Coordinate(2, 2), fixture.getAppCoordinate(app));
    }

    @Test
    void testMerge() {
        // fixture
        var inner = new AppMatrix(3, 3);
        Application app1 = new Application("A1", "App1", "C1", "", "", "");
        inner.put(1, 0, app1);
        Application app2 = new Application("A2", "App2", "C1", "", "", "");
        inner.put(2, 2, app2);
        var outer = new AppMatrix(6, 6);
        Application app3 = new Application("A3", "App3", "C2", "", "", "");
        outer.put(1, 0, app3);
        Application app4 = new Application("A4", "App4", "C2", "", "", "");
        outer.put(1, 2, app4);
        // test
        outer.merge(inner, 3, 0);
        System.out.println(outer.dump());
        // verify
        assertEquals(new Coordinate(1, 0), outer.getAppCoordinate(app3));
        assertEquals(new Coordinate(1, 2), outer.getAppCoordinate(app4));
        assertEquals(new Coordinate(4, 0), outer.getAppCoordinate(app1));
        assertEquals(new Coordinate(5, 2), outer.getAppCoordinate(app2));
    }

    @Test
    void testUsedCellIterator() {
        // fixture
        var fixture = new AppMatrix(3, 4);
        var coord = new Coordinate[] {
                new Coordinate(0, 0),
                new Coordinate(1, 2),
                new Coordinate(2, 3)
        };
        fixture.put(coord[0], new Application("A1", "App1", "C1", "", "", ""));
        fixture.put(coord[1], new Application("A2", "App2", "C1", "", "", ""));
        fixture.put(coord[2], new Application("A3", "App3", "C1", "", "", ""));
        // verify
        int i = 0;
        for (var c : fixture.usedCoordinates()) {
            assertEquals(coord[i], c);
            i++;
        }
        assertEquals(3, i);
    }

    @Test
    void testEmptyCellsOneEmptyHor() {
        var fixture = new AppMatrix(3, 3);
        var c1 = new Coordinate(0, 0);
        var c2 = new Coordinate(0, 2);
        fixture.put(c1, new Application("A1", "A1", "C1"));
        fixture.put(c2, new Application("A2", "A2", "C1"));
        assertTrue(fixture.allCellsEmptyHor(c1, c2));
    }

    @Test
    void testEmptyCellsOneEmptyVert() {
        var fixture = new AppMatrix(3, 3);
        var c1 = new Coordinate(0, 0);
        var c2 = new Coordinate(2, 0);
        fixture.put(c1, new Application("A1", "A1", "C1"));
        fixture.put(c2, new Application("A2", "A2", "C1"));
        assertTrue(fixture.allCellsEmptyVert(c1, c2));
    }

    @Test
    void testEmptyCellsTwoEmptyHor() {
        var fixture = new AppMatrix(3, 4);
        var c1 = new Coordinate(0, 0);
        var c2 = new Coordinate(0, 3);
        fixture.put(c1, new Application("A1", "A1", "C1"));
        fixture.put(c2, new Application("A2", "A2", "C1"));
        assertTrue(fixture.allCellsEmptyHor(c1, c2));
    }

    @Test
    void testEmptyCellsTwoEmptyVert() {
        var fixture = new AppMatrix(4, 3);
        var c1 = new Coordinate(0, 0);
        var c2 = new Coordinate(3, 0);
        fixture.put(c1, new Application("A1", "A1", "C1"));
        fixture.put(c2, new Application("A2", "A2", "C1"));
        assertTrue(fixture.allCellsEmptyVert(c1, c2));
    }

    @Test
    void testThreeCellsInALine() {
        var fixture = new AppMatrix(3, 3);
        var c1 = new Coordinate(0, 0);
        var c2 = new Coordinate(0, 1);
        var c3 = new Coordinate(0, 2);
        fixture.put(c1, new Application("A1", "A1", "C1"));
        fixture.put(c2, new Application("A2", "A2", "C1"));
        fixture.put(c3, new Application("A3", "A3", "C1"));
        assertFalse(fixture.allCellsEmptyHor(c1, c3));
    }

    @Test
    void testThreeCellsInALineOfFive() {
        var fixture = new AppMatrix(3, 5);
        var c1 = new Coordinate(0, 0);
        var c2 = new Coordinate(0, 2);
        var c3 = new Coordinate(0, 4);
        fixture.put(c1, new Application("A1", "A1", "C1"));
        fixture.put(c2, new Application("A2", "A2", "C1"));
        fixture.put(c3, new Application("A3", "A3", "C1"));
        assertFalse(fixture.allCellsEmptyHor(c1, c3));
    }

    @Test
    void testThreeCellsInARow() {
        var fixture = new AppMatrix(3, 3);
        var c1 = new Coordinate(0, 0);
        var c2 = new Coordinate(1, 0);
        var c3 = new Coordinate(2, 0);
        fixture.put(c1, new Application("A1", "A1", "C1"));
        fixture.put(c2, new Application("A2", "A2", "C1"));
        fixture.put(c3, new Application("A3", "A3", "C1"));
        assertFalse(fixture.allCellsEmptyVert(c1, c3));
    }

    @Test
    void testCellsRightToLeft() {
        var fixture = new AppMatrix(3, 3);
        var c1 = new Coordinate(0, 1);
        var c2 = new Coordinate(1, 0);
        var c3 = new Coordinate(0, 2);
        var c4 = new Coordinate(1, 2);
        var c5 = new Coordinate(1, 1);
        var c6 = new Coordinate(0, 0);
        fixture.put(c1, new Application("A1", "A1", "C1"));
        fixture.put(c2, new Application("A2", "A2", "C1"));
        fixture.put(c3, new Application("A3", "A3", "C1"));
        fixture.put(c4, new Application("A4", "A4", "C1"));
        fixture.put(c5, new Application("A5", "A5", "C1"));
        fixture.put(c6, new Application("A6", "A6", "C1"));
        assertFalse(fixture.allCellsEmptyHor(c3, c6));
    }


}
