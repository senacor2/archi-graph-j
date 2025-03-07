package com.bmw.archigraph.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ModelTest {

    @Test
    void testSetComponentsNoNesting() {
        var components = List.of(
                new Component("COMP-1", 0, 0, 5, 3, 1),
                new Component("COMP-2", 7, 0, 4, 3, 1));
        var model = new Model();
        model.components(components);
        assertEquals(2, model.getComponentMap().size());
        assertEquals(components.getFirst(), model.getComponentMap().get("COMP-1"));
        assertEquals(components.get(1), model.getComponentMap().get("COMP-2"));
    }

    @Test
    void testSetComponentsNested() {
        var components = List.of(
                new Component("COMP-1", 0, 0, 5, 3, 1),
                new Component("COMP-2", 7, 0, 4, 3, 1));
        components.getFirst().setComponents(List.of(
                new Component("COMP-11", 0, 1, 2, 2, 2),
                new Component("COMP-12", 3, 1, 2, 2, 2)));
        var model = new Model();
        model.components(components);
        assertEquals(4, model.getComponentMap().size());
        assertEquals(components.getFirst(), model.getComponentMap().get("COMP-1"));
        assertEquals(components.getLast(), model.getComponentMap().get("COMP-2"));
        assertEquals(components.getFirst().getComponents().getFirst(), model.getComponentMap().get("COMP-11"));
        assertEquals(components.getFirst().getComponents().getLast(), model.getComponentMap().get("COMP-12"));
        assertEquals(components.getFirst(), components.getFirst().getL1Component());
        assertEquals(components.getFirst(), model.getComponentMap().get("COMP-11").getL1Component());
        assertEquals(components.getFirst(), model.getComponentMap().get("COMP-12").getL1Component());
        assertEquals(components.getLast(), components.getLast().getL1Component());
    }

    @Test
    void testSetApplications() {
        var components = List.of(
                new Component("COMP-1", 0, 0, 5, 3, 1),
                new Component("COMP-2", 7, 0, 4, 3, 1));
        var applications = List.of(
                new Application("APP-1", "Application 1", "COMP-1", "", "", ""),
                new Application("APP-2", "Application 2", "COMP-2", "", "", ""));
        var model = new Model();
        model.components(components);
        model.applications(applications);
        assertEquals(2, model.getApplicationMap().size());
        assertEquals(applications.getFirst(), model.getApplicationMap().get("APP-1"));
        assertEquals(components.getFirst(), model.getApplicationMap().get("APP-1").getComponent());
        assertEquals(applications.getLast(), model.getApplicationMap().get("APP-2"));
        assertEquals(components.getLast(), model.getApplicationMap().get("APP-2").getComponent());
    }

    @Test
    void testSetInformationFlows() {
        var c1 = new Component("COMP-1", 0, 0, 5, 3, 1);
        var c2 = new Component("COMP-2", 7, 0, 4, 3, 1);
        var components = List.of(c1, c2);
        var a11 = new Application("APP-11", "Application 11", "COMP-1", "", "", "");
        var a12 = new Application("APP-12", "Application 12", "COMP-1", "", "", "");
        var a21 = new Application("APP-21", "Application 21", "COMP-2", "", "", "");
        var applications = List.of(a11, a12, a21);
        var if1112 = new InformationFlow("IF-1112", "APP-11", "APP-12", "BO", Direction.ONE_WAY);
        var if1121 = new InformationFlow("IF-1121", "APP-11", "APP-21", "BO", Direction.ONE_WAY);
        var informationFlows = List.of(if1121, if1112);
        var model = new Model();
        model.components(components);
        model.applications(applications);
        model.informationFlows(informationFlows);
        assertEquals(2, model.getInformationFlowMap().size());
        assertThat(model.getInformationFlowMap().values()).containsExactlyInAnyOrder(if1112, if1121);
        assertEquals(a11, model.getInformationFlowMap().get("IF-1112").getSource());
        assertEquals(a12, model.getInformationFlowMap().get("IF-1112").getDestination());
        assertEquals(a21, model.getInformationFlowMap().get("IF-1121").getDestination());
        assertThat(c1.getInternalInformationFlows()).containsExactly(if1112);
        assertThat(c2.getInternalInformationFlows()).isEmpty();
        assertThat(c1.getCrossCompInformationFlows()).containsExactly(if1121);
        assertThat(c2.getCrossCompInformationFlows()).containsExactly(if1121);
    }
}
