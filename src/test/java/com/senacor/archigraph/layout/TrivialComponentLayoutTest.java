package com.senacor.archigraph.layout;

import com.senacor.archigraph.model.Application;
import com.senacor.archigraph.model.Coordinate;
import com.senacor.archigraph.model.L1Component;
import com.senacor.archigraph.model.Model;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TrivialComponentLayoutTest {

    private Coordinate nc(int row, int col) {
        return new Coordinate(row, col);
    }

    @Test
    void testFindAppPositionsWithoutFlows() {
        // fixture
        var comp = new L1Component("COMP-1", 0, 0, 2, 2, 1);
        var appA = new Application("APP-A", "A1", "COMP-1");
        var appB = new Application("APP-B", "A2", "COMP-1");
        var appC = new Application("APP-C", "A3", "COMP-1");
        var appD = new Application("APP-D", "A4", "COMP-1");
        var model = new Model();
        model.setL1Components(List.of(comp));
        model.setApplications(List.of(appA, appB, appC, appD));
        // test
        var cl = new TrivialComponentLayout(comp);
        cl.layout();
        // verify
        assertEquals(nc(0, 0), cl.getAppCoordinate(appA), "App-A");
        assertEquals(nc(0, 1), cl.getAppCoordinate(appB), "App-B");
        assertEquals(nc(1, 0), cl.getAppCoordinate(appC), "App-C");
        assertEquals(nc(1, 1), cl.getAppCoordinate(appD), "App-D");
        assertEquals(0, cl.getQuality());
    }


}
