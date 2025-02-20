package com.bmw.archigraph.model;

import org.junit.jupiter.api.Test;

import java.util.List;

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
        var components = List.of(
                new Component("COMP-1", 0, 0, 5, 3, 1),
                new Component("COMP-2", 7, 0, 4, 3, 1));
        var applications = List.of(
                new Application("APP-1", "Application 1", "COMP-1", "", "", ""),
                new Application("APP-2", "Application 2", "COMP-2", "", "", ""));
        var informationFlows = List.of(
                new InformationFlow("IF-12", "APP-1", "APP-2", "BO", Direction.ONE_WAY));
        var model = new Model();
        model.components(components);
        model.applications(applications);
        model.informationFlows(informationFlows);
        assertEquals(1, model.getInformationFlowMap().size());
        assertEquals(informationFlows.getFirst(), model.getInformationFlowMap().get("IF-12"));
        assertEquals(applications.getFirst(), model.getInformationFlowMap().get("IF-12").getSource());
        assertEquals(applications.getLast(), model.getInformationFlowMap().get("IF-12").getDestination());
    }
}
