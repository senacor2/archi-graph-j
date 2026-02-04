package com.senacor.archigraph.model;

import com.senacor.archigraph.render.Rectangle;
import com.senacor.archigraph.render.RenderModel;
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

    @Test
    public void testCoordinateOf() {
        var appMatrix = new AppMatrix(5, 5);
        appMatrix.setOrigin(10 * RenderModel.COL_WIDTH, 10 * RenderModel.ROW_HEIGHT);
        assertEquals(new Coordinate(0, 0),
                Coordinate.of(appMatrix,
                        Rectangle.builder()
                                .x(10 * RenderModel.COL_WIDTH + RenderModel.SPACING)
                                .y(10 * RenderModel.ROW_HEIGHT + RenderModel.SPACING)
                                .build()));
        assertEquals(new Coordinate(1, 1),
                Coordinate.of(appMatrix,
                        Rectangle.builder()
                                .x(11 * RenderModel.COL_WIDTH + RenderModel.SPACING)
                                .y(11 * RenderModel.ROW_HEIGHT + RenderModel.SPACING)
                                .build()));
    }
}
