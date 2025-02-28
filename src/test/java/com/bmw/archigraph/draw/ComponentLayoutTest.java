package com.bmw.archigraph.draw;

import com.bmw.archigraph.model.Application;
import com.bmw.archigraph.model.Component;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class ComponentLayoutTest {

    /**
     * Helper function to increase readability and save typing.
     */
    private Coordinate nc(int row, int col) {
        return new Coordinate(row, col);
    }

    @Test
    void testAppPositionsInComponentWithOneApp() {
        var cl = new ComponentLayout(null);
        assertEquals(
                List.of(List.of(nc(0, 0)),
                        List.of(nc(0, 1)),
                        List.of(nc(1, 0)),
                        List.of(nc(1, 1))),
                cl.appPositionsInComponent(2, 2, 1));
    }

    @Test
    void testAppPositionsInComponentWithTwoApps() {
        var cl = new ComponentLayout(null);
        assertThat(cl.appPositionsInComponent(2, 2, 2))
                .hasSameElementsAs(List.of(
                        List.of(nc(0, 0), nc(0, 1)),
                        List.of(nc(0, 1), nc(0, 0)),
                        List.of(nc(0, 0), nc(1, 0)),
                        List.of(nc(1, 0), nc(0, 0)),
                        List.of(nc(0, 0), nc(1, 1)),
                        List.of(nc(1, 1), nc(0, 0)),
                        List.of(nc(0, 1), nc(1, 0)),
                        List.of(nc(1, 0), nc(0, 1)),
                        List.of(nc(0, 1), nc(1, 1)),
                        List.of(nc(1, 1), nc(0, 1)),
                        List.of(nc(1, 0), nc(1, 1)),
                        List.of(nc(1, 1), nc(1, 0))));
    }

    @Test
    void testAppPositionsInComponentWithThreeApps() {
        var cl = new ComponentLayout(null);
        assertThat(cl.appPositionsInComponent(2, 2, 3))
                .hasSameElementsAs(List.of(
                        List.of(nc(0, 0), nc(0, 1), nc(1, 0)),
                        List.of(nc(0, 0), nc(1, 0), nc(0, 1)),
                        List.of(nc(0, 1), nc(0, 0), nc(1, 0)),
                        List.of(nc(0, 1), nc(1, 0), nc(0, 0)),
                        List.of(nc(1, 0), nc(0, 0), nc(0, 1)),
                        List.of(nc(1, 0), nc(0, 1), nc(0, 0)),
                        List.of(nc(0, 0), nc(0, 1), nc(1, 1)),
                        List.of(nc(0, 0), nc(1, 1), nc(0, 1)),
                        List.of(nc(0, 1), nc(0, 0), nc(1, 1)),
                        List.of(nc(0, 1), nc(1, 1), nc(0, 0)),
                        List.of(nc(1, 1), nc(0, 0), nc(0, 1)),
                        List.of(nc(1, 1), nc(0, 1), nc(0, 0)),
                        List.of(nc(0, 0), nc(1, 0), nc(1, 1)),
                        List.of(nc(0, 0), nc(1, 1), nc(1, 0)),
                        List.of(nc(1, 0), nc(0, 0), nc(1, 1)),
                        List.of(nc(1, 0), nc(1, 1), nc(0, 0)),
                        List.of(nc(1, 1), nc(0, 0), nc(1, 0)),
                        List.of(nc(1, 1), nc(1, 0), nc(0, 0)),
                        List.of(nc(0, 1), nc(1, 0), nc(1, 1)),
                        List.of(nc(0, 1), nc(1, 1), nc(1, 0)),
                        List.of(nc(1, 0), nc(0, 1), nc(1, 1)),
                        List.of(nc(1, 0), nc(1, 1), nc(0, 1)),
                        List.of(nc(1, 1), nc(0, 1), nc(1, 0)),
                        List.of(nc(1, 1), nc(1, 0), nc(0, 1))
                ));
    }

    @Test
    void testAppPositionsIllegalCall() {
        var cl = new ComponentLayout(null);
        var exception = assertThrows(IllegalArgumentException.class,
                () -> cl.appPositionsInComponent(2, 2, 5));
        assertEquals("Number of apps (5) exceeds grid size (2 x 2)",
                exception.getMessage());
    }

    @Test
    void testLinesIntersect() {
        var cl = new ComponentLayout(null);
        assertTrue(cl.linesIntersect(nc(0, 0), nc(1, 1), nc(1, 0), nc(0, 1)));
        assertTrue(cl.linesIntersect(nc(1, 0), nc(0, 1), nc(0, 0), nc(1, 1)));
    }

    @Test
    void testLinesDoNotIntersect() {
        var cl = new ComponentLayout(null);
        assertFalse(cl.linesIntersect(nc(0, 0), nc(0, 1), nc(1, 0), nc(1, 1)));
        assertFalse(cl.linesIntersect(nc(1, 0), nc(1, 1), nc(0, 0), nc(0, 1)));
        assertFalse(cl.linesIntersect(nc(0, 0), nc(1, 0), nc(0, 1), nc(1, 1)));
        assertFalse(cl.linesIntersect(nc(0, 1), nc(1, 1), nc(0, 0), nc(1, 0)));
    }

    @Test
    void testLinesWithSameEndpoint() {
        var cl = new ComponentLayout(null);
        assertFalse(cl.linesIntersect(nc(0,1), nc(1, 1), nc(0, 0), nc(1, 1)));
    }

    @Test
    void testLinesIntersectWithSixApps() {
        var cl = new ComponentLayout(null);
        assertFalse(cl.linesIntersect(nc(2, 2), nc(1, 2), nc(0, 2), nc(2, 1)));
        assertFalse(cl.linesIntersect(nc(0, 2), nc(2, 1), nc(0, 1), nc(1, 1)));
        assertFalse(cl.linesIntersect(nc(2, 2), nc(1, 2), nc(0, 1), nc(1, 1)));
    }
}
