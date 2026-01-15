package com.senacor.archigraph.model;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class ProxyBoxLayoutTest {

    @Test
    void testCreateProxyBox() {
        // fixture
        var comp = new Component("Comp-1", 4, 8, 2, 2, 1);
        var compLayout = new ComponentLayout(comp);
        compLayout.layout();
        // test
        var proxyBoxLayout = new ProxyBoxLayout(comp);
        // verify
        assertEquals(4 + 4 + 2 + 2, proxyBoxLayout.getProxyBoxCoords().size());
        assertThat(proxyBoxLayout.getProxyBoxCoords())
                .containsExactlyInAnyOrder(
                        new Coordinate(0, 0),
                        new Coordinate(0, 1),
                        new Coordinate(0, 2),
                        new Coordinate(0, 3),
                        new Coordinate(1, 0),
                        new Coordinate(1, 3),
                        new Coordinate(2, 0),
                        new Coordinate(2, 3),
                        new Coordinate(3, 0),
                        new Coordinate(3, 1),
                        new Coordinate(3, 2),
                        new Coordinate(3, 3));
    }

    @Test
    void testCreateProxyBox2() {
        // fixture
        var comp = new Component("Comp-1", 4, 8, 2, 2, 1);
        comp.setProxyAreaSize(2);
        var compLayout = new ComponentLayout(comp);
        compLayout.layout();
        // test
        var proxyBoxLayout = new ProxyBoxLayout(comp);
        // verify
        assertEquals(4 * 6 + 2 * 4, proxyBoxLayout.getProxyBoxCoords().size());
        assertThat(proxyBoxLayout.getProxyBoxCoords())
                .containsExactlyInAnyOrder(
                        new Coordinate(0, 0),
                        new Coordinate(0, 1),
                        new Coordinate(0, 2),
                        new Coordinate(0, 3),
                        new Coordinate(0, 4),
                        new Coordinate(0, 5),
                        new Coordinate(1, 0),
                        new Coordinate(1, 1),
                        new Coordinate(1, 2),
                        new Coordinate(1, 3),
                        new Coordinate(1, 4),
                        new Coordinate(1, 5),
                        new Coordinate(2, 0),
                        new Coordinate(2, 1),
                        new Coordinate(2, 4),
                        new Coordinate(2, 5),
                        new Coordinate(3, 0),
                        new Coordinate(3, 1),
                        new Coordinate(3, 4),
                        new Coordinate(3, 5),
                        new Coordinate(4, 0),
                        new Coordinate(4, 1),
                        new Coordinate(4, 2),
                        new Coordinate(4, 3),
                        new Coordinate(4, 4),
                        new Coordinate(4, 5),
                        new Coordinate(5, 0),
                        new Coordinate(5, 1),
                        new Coordinate(5, 2),
                        new Coordinate(5, 3),
                        new Coordinate(5, 4),
                        new Coordinate(5, 5));
    }

    @ParameterizedTest
    @CsvSource(
            {"left top,2,2,1,1,0,1", "right top,2,2,1,2,0,2", "left bottom,2,2,2,1,2,0", "right bottom,2,2,2,2,2,3",
                    "left mid,4,4,2,2,0,2", "right mid,4,4,2,3,0,3", "left middle,4,4,3,2,3,0", "right middle,4,4,3,3,3,5",
                    "left center,4,8,2,4,0,4", "right center,4,8,2,5,0,5"
            })
    void testFindEmptyCell(String test, int height, int width, int appRow, int appCol, int proxyRow, int proxyCol) {
        // fixture
        var appCoord = new Coordinate(appRow, appCol);
        var comp = new Component("Comp-1", 4, 8, width, height, 1);
        var compLayout = new ComponentLayout(comp);
        compLayout.layout();
        var proxyBoxLayout = new ProxyBoxLayout(comp);
        // test
        var result = proxyBoxLayout.findNearestEmptyCell(appCoord);
        // verify
        assertEquals(new Coordinate(proxyRow, proxyCol), result, test);
        assertThat(proxyBoxLayout.getProxyBoxCoords())
                .doesNotContain(result);
    }
}

