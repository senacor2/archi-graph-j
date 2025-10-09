package com.bmw.archigraph.read;

import com.bmw.archigraph.draw.Area;
import com.bmw.archigraph.model.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReaderTest {

    public static final String COMP1_FILE = "data/comp1.json";
    public static final String COMP2_FILE = "data/comp2.json";
    public static final String APPS_FILE = "data/apps1.csv";
    public static final String FLOWS_FILE = "data/flows1.csv";

    private final Reader reader = new Reader(COMP1_FILE, APPS_FILE, FLOWS_FILE);

    @Test
    void testReadApplications() throws IOException {
        var apps = reader.readApplications();
        assertEquals(9, apps.size());
        assertEquals(new Application("app-1", "Application 1", "Component 1", "Linux", "AWS", "2020"),
                apps.getFirst());
        assertEquals(new Application("app-9", "Application 9", "Component 2", "Linux", "Azure", "2021"),
                apps.getLast());
    }

    @Test
    void testReadInformationFlows() throws IOException {
        var flows = reader.readInformationFlows();
        assertEquals(11, flows.size());
        assertEquals(new InformationFlow("if-12", "app-1", "app-2", "Business object 1",
                Direction.ONE_WAY), flows.getFirst());
        assertEquals(new InformationFlow("if-67", "app-6", "app-7", "Business object 11",
                Direction.ONE_WAY), flows.getLast());
    }

    @Test
    void testReadComponents() throws IOException {
        var model = new Model();
        reader.readComponentModel(model);
        var comps = model.getComponentMap().values();
        assertThat(comps).containsExactlyInAnyOrder(
                new Component("Component 1", 1, 1, 3, 2, 1),
                new Component("Component 2", 1, 5, 2, 2, 1));
    }

    @Test
    void testReadNestedComponents() throws IOException {
        var reader = new Reader(COMP2_FILE, null, null);
        var model = new Model();
        reader.readComponentModel(model);
        var componentMap = model.getComponentMap();
        assertThat(componentMap.keySet()).containsExactlyInAnyOrder(
                "Component 1", "Component 1-1", "Component 1-1-1", "Component 1-1-1-1", "Component 1-2"
        );
        assertThat(componentMap.get("Component 1").getAppArea()).isEqualTo(new Area(2, 2, 2, 4));
    }
}
