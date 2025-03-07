package com.bmw.archigraph.render;

import com.bmw.archigraph.model.Application;
import com.bmw.archigraph.model.Component;
import com.bmw.archigraph.model.Model;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RenderModelTest {

    @Test
    void testAddElement() {
        var fixture = new RenderModel();
        var rect = Rectangle.builder().build();
        fixture.add(rect);
        assertEquals(1, fixture.getElements().size());
        assertThat(fixture.getElements()).containsExactly(rect);
    }

    @Test
    void testSimpleModel() {
        var model = Model.builder()
                .name("System 1")
                .build();
        var c1 = new Component("COMP-1", 0, 0, 3, 3, 1);
        var c2 = new Component("COMP-2", 4, 0, 2, 3, 1);
        model.setL1Components(List.of(c1, c2));
        model.setApplications(List.of());
        model.setInformationFlows(List.of());
        var fixture = new RenderModel();
        var result = fixture.render(model);
        assertThat(result.getElements())
                .containsExactlyInAnyOrder(
                        Rectangle.builder()
                                .id("ROOT")
                                .x(0)
                                .y(0)
                                .w(1920)
                                .h(100)
                                .text("System 1")
                                .fontSize(28)
                                .background(new Color(0, 0, 156))
                                .foreground(Color.WHITE)
                                .build(),
                        Rectangle.builder()
                                .id("COMP-1_head")
                                .x(0)
                                .y(200)
                                .w(960)
                                .h(100)
                                .text("COMP-1")
                                .fontSize(24)
                                .background(new Color(0, 0, 110))
                                .foreground(Color.WHITE)
                                .build(),
                        Rectangle.builder()
                                .id("COMP-1_body")
                                .x(0)
                                .y(200)
                                .w(960)
                                .h(700)
                                .background(Color.WHITE)
                                .build(),
                        Rectangle.builder()
                                .id("COMP-2_head")
                                .text("COMP-2")
                                .x(1280)
                                .y(200)
                                .w(640)
                                .h(100)
                                .fontSize(24)
                                .background(new Color(0, 0, 110))
                                .foreground(Color.WHITE)
                                .build(),
                        Rectangle.builder()
                                .id("COMP-2_body")
                                .x(1280)
                                .y(200)
                                .w(640)
                                .h(700)
                                .background(Color.WHITE)
                                .build()
                );
    }

    @Test
    void testModelWithApps() {
        var model = Model.builder()
                .name("System 1")
                .build();
        var c1 = new Component("COMP-1", 0, 0, 3, 3, 1);
        model.setL1Components(List.of(c1));
        var a1 = new Application("App-1", "Application 1", "COMP-1", "", "", "");
        var a2 = new Application("App-2", "Application 2", "COMP-1", "", "", "");
        model.setApplications(List.of(a1, a2));
        model.setInformationFlows(List.of());
        var fixture = new RenderModel();
        var result = fixture.render(model);
        assertThat(result.getElements())
                .containsExactlyInAnyOrder(
                        Rectangle.builder()
                                .id("ROOT")
                                .text("System 1")
                                .x(0)
                                .y(0)
                                .w(960)
                                .h(100)
                                .background(new Color(0, 0, 156))
                                .foreground(Color.WHITE)
                                .fontSize(28)
                                .build(),
                        Rectangle.builder()
                                .id("COMP-1_head")
                                .text("COMP-1")
                                .x(0)
                                .y(200)
                                .w(960)
                                .h(100)
                                .background(new Color(0, 0, 110))
                                .foreground(Color.WHITE)
                                .fontSize(24)
                                .build(),
                        Rectangle.builder()
                                .id("COMP-1_body")
                                .x(0)
                                .y(200)
                                .w(960)
                                .h(700)
                                .background(Color.WHITE)
                                .build(),
                        Rectangle.builder()
                                .id("App-1")
                                .text("Application 1")
                                .rounded(true)
                                .x(40)
                                .y(340)
                                .w(240)
                                .h(120)
                                .background(Color.WHITE)
                                .foreground(Color.BLACK)
                                .fontSize(12)
                                .build(),
                        Rectangle.builder()
                                .id("App-2")
                                .text("Application 2")
                                .rounded(true)
                                .x(360)
                                .y(340)
                                .w(240)
                                .h(120)
                                .background(Color.WHITE)
                                .foreground(Color.BLACK)
                                .fontSize(12)
                                .build()
                );
    }

    @Test
    void testModelWithSubComponents() {
        // fixture
        var comp1 = new Component("COMP-1", 0, 0, 4, 3, 1);
        var comp2 = new Component("COMP-11", 0, 0, 3, 2, 2);
        comp1.setComponents(List.of(comp2));
        var model = Model.builder().name("System 1").build();
        model.setL1Components(List.of(comp1));
        // test
        var renderModel = new RenderModel();
        var result = renderModel.render(model);
        // verify
        assertThat(result.getElements())
                .containsExactlyInAnyOrder(
                        Rectangle.builder()
                                .id("ROOT")
                                .text("System 1")
                                .x(0)
                                .y(0)
                                .w(1280)
                                .h(100)
                                .foreground(Color.WHITE)
                                .background(new Color(0, 0, 156))
                                .fontSize(28)
                                .build(),
                        Rectangle.builder()
                                .id("COMP-1_head")
                                .text("COMP-1")
                                .x(0)
                                .y(200)
                                .w(1280)
                                .h(100)
                                .foreground(Color.WHITE)
                                .background(new Color(0, 0, 110))
                                .fontSize(24)
                                .build(),
                        Rectangle.builder()
                                .id("COMP-1_body")
                                .x(0)
                                .y(200)
                                .w(1280)
                                .h(700)
                                .background(Color.WHITE)
                                .build(),
                        Rectangle.builder()
                                .id("COMP-11_head")
                                .text("COMP-11")
                                .x(160)
                                .y(300)
                                .w(960)
                                .h(100)
                                .foreground(Color.WHITE)
                                .background(new Color(0, 0, 110))
                                .fontSize(20)
                                .build(),
                        Rectangle.builder()
                                .id("COMP-11_body")
                                .x(160)
                                .y(300)
                                .w(960)
                                .h(500)
                                .background(Color.WHITE)
                                .build()
                );
    }

}
