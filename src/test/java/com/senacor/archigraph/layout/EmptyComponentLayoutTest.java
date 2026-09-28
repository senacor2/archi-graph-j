package com.senacor.archigraph.layout;

import com.senacor.archigraph.model.Component;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EmptyComponentLayoutTest {

    @Test
    void testFindAppPositionsEmptyComponent() {
        var comp = new Component("COMP-1", 0, 0, 2, 2, 1);
        comp.layout();
        var cl = new EmptyComponentLayout(comp);
        cl.layout();
        assertEquals(0, cl.getQuality());
    }

}
