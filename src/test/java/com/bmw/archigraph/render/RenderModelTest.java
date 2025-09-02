package com.bmw.archigraph.render;

import com.bmw.archigraph.draw.ComponentLayout;
import com.bmw.archigraph.draw.Coordinate;
import com.bmw.archigraph.model.*;
import com.bmw.archigraph.model.Component;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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
        var c1 = new Component("COMP-1", 1, 0, 3, 3, 1);
        var c2 = new Component("COMP-2", 1, 4, 2, 3, 1);
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
                                .w(2240)
                                .h(100)
                                .text("System 1")
                                .fontSize(48)
                                .background(new Color(0, 0, 156))
                                .foreground(Color.WHITE)
                                .build(),
                        Rectangle.builder()
                                .id("COMP-1_head")
                                .x(0)
                                .y(200)
                                .w(960)
                                .h(200)
                                .text("COMP-1")
                                .fontSize(48)
                                .background(new Color(0, 0, 110))
                                .foreground(Color.WHITE)
                                .build(),
                        Rectangle.builder()
                                .id("COMP-1_body")
                                .x(0)
                                .y(400)
                                .w(960)
                                .h(400)
                                .background(Color.WHITE)
                                .build(),
                        Rectangle.builder()
                                .id("COMP-2_head")
                                .text("COMP-2")
                                .x(1280)
                                .y(200)
                                .w(640)
                                .h(200)
                                .fontSize(48)
                                .background(new Color(0, 0, 110))
                                .foreground(Color.WHITE)
                                .build(),
                        Rectangle.builder()
                                .id("COMP-2_body")
                                .x(1280)
                                .y(400)
                                .w(640)
                                .h(400)
                                .background(Color.WHITE)
                                .build()
                );
    }

    @Test
    void testModelWithApps() {
        var model = Model.builder()
                .name("System 1")
                .build();
        var c1 = new Component("COMP-1", 1, 0, 3, 3, 1);
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
                                .w(1280)
                                .h(100)
                                .background(new Color(0, 0, 156))
                                .foreground(Color.WHITE)
                                .fontSize(48)
                                .build(),
                        Rectangle.builder()
                                .id("COMP-1_head")
                                .text("COMP-1")
                                .x(0)
                                .y(200)
                                .w(960)
                                .h(200)
                                .background(new Color(0, 0, 110))
                                .foreground(Color.WHITE)
                                .fontSize(48)
                                .build(),
                        Rectangle.builder()
                                .id("COMP-1_body")
                                .x(0)
                                .y(400)
                                .w(960)
                                .h(400)
                                .background(Color.WHITE)
                                .build(),
                        Rectangle.builder()
                                .id("App-1")
                                .text("Application 1")
                                .rounded(true)
                                .x(40)
                                .y(440)
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
                                .y(440)
                                .w(240)
                                .h(120)
                                .background(Color.WHITE)
                                .foreground(Color.BLACK)
                                .fontSize(12)
                                .build()
                );
        assertEquals(new Coordinate(0, 0), c1.getLayout().getAppCoordinate(a1));
        assertEquals(new Coordinate(0, 1), c1.getLayout().getAppCoordinate(a2));
    }

    @Test
    void testModelWithSubComponents() {
        // fixture
        var comp1 = new Component("COMP-1", 1, 0, 4, 3, 1);
        var comp2 = new Component("COMP-11", 0, 0, 3, 2, 2);
        var app1 = new Application("A1", "App-1", "COMP-11");
        var app2 = new Application("A2", "App-2", "COMP-11");
        comp1.setComponents(List.of(comp2));
        var model = Model.builder().name("System 1").build();
        model.setL1Components(List.of(comp1));
        model.setApplications(List.of(app1, app2));
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
                                .w(1600)
                                .h(100)
                                .foreground(Color.WHITE)
                                .background(new Color(0, 0, 156))
                                .fontSize(48)
                                .build(),
                        Rectangle.builder()
                                .id("COMP-1_head")
                                .text("COMP-1")
                                .x(0)
                                .y(200)
                                .w(1280)
                                .h(200)
                                .foreground(Color.WHITE)
                                .background(new Color(0, 0, 110))
                                .fontSize(48)
                                .build(),
                        Rectangle.builder()
                                .id("COMP-1_body")
                                .x(0)
                                .y(400)
                                .w(1280)
                                .h(400)
                                .background(Color.WHITE)
                                .build(),
                        Rectangle.builder()
                                .id("COMP-11_head")
                                .text("COMP-11")
                                .x(10)
                                .y(410)
                                .w(940)
                                .h(200)
                                .foreground(Color.WHITE)
                                .background(new Color(0, 0, 110))
                                .fontSize(40)
                                .build(),
                        Rectangle.builder()
                                .id("COMP-11_body")
                                .x(10)
                                .y(610)
                                .w(940)
                                .h(180)
                                .background(Color.WHITE)
                                .build(),
                        Rectangle.builder()
                                .id("A1")
                                .text("App-1")
                                .rounded(true)
                                .x(40)
                                .y(640)
                                .w(240)
                                .h(120)
                                .background(Color.WHITE)
                                .foreground(Color.BLACK)
                                .fontSize(12)
                                .build(),
                        Rectangle.builder()
                                .id("A2")
                                .text("App-2")
                                .rounded(true)
                                .x(360)
                                .y(640)
                                .w(240)
                                .h(120)
                                .background(Color.WHITE)
                                .foreground(Color.BLACK)
                                .fontSize(12)
                                .build()
                );
        assertEquals(new Coordinate(0, 0), comp1.getLayout().getAppCoordinate(app1));
        assertEquals(new Coordinate(0, 1), comp1.getLayout().getAppCoordinate(app2));
        assertEquals(new Coordinate(0, 0), comp2.getLayout().getAppCoordinate(app1));
        assertEquals(new Coordinate(0, 1), comp2.getLayout().getAppCoordinate(app2));
    }

    @Test
    void testModelWithInformationFlows() {
        // fixture
        var model = Model.builder()
                .name("System 1")
                .build();
        var app1 = new Application("A1", "A1", "C1", "", "", "");
        var app2 = new Application("A2", "A2", "C1", "", "", "");
        var comp1 = new Component("C1", 1, 0, 3, 3, 1);
        var if1 = new InformationFlow("IF12", "A1", "A2", "BO", Direction.ONE_WAY);
        model.setL1Components(List.of(comp1));
        model.setApplications(List.of(app1, app2));
        model.setInformationFlows(List.of(if1));
        // test
        var fixture = new RenderModel();
        var result = fixture.render(model);
        // verify
        var rectApp1 = Rectangle.builder()
                .id("A1")
                .text("A1")
                .rounded(true)
                .x(40)
                .y(440)
                .w(240)
                .h(120)
                .foreground(Color.BLACK)
                .background(Color.WHITE)
                .fontSize(12)
                .build();
        var rectApp2 = Rectangle.builder()
                .id("A2")
                .text("A2")
                .rounded(true)
                .x(360)
                .y(440)
                .w(240)
                .h(120)
                .foreground(Color.BLACK)
                .background(Color.WHITE)
                .fontSize(12)
                .build();
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
                                .fontSize(48)
                                .build(),
                        Rectangle.builder()
                                .id("C1_body")
                                .x(0)
                                .y(400)
                                .w(960)
                                .h(400)
                                .background(Color.WHITE)
                                .build(),
                        Rectangle.builder()
                                .id("C1_head")
                                .text("C1")
                                .x(0)
                                .y(200)
                                .w(960)
                                .h(200)
                                .foreground(Color.WHITE)
                                .background(new Color(0, 0, 110))
                                .fontSize(48)
                                .build(),
                        rectApp1,
                        rectApp2,
                        Line.builder()
                                .id("IF12")
                                .text("BO")
                                .start(rectApp1)
                                .end(rectApp2)
                                .anchors(new Point[0])
                                .build()
                );
    }

    @Test
    void testCoordOnAppTop() {
        var fixture = new RenderModel();
        assertEquals(new Point(530, 290),
                fixture.coordOnApp(1, 50, 50, new Coordinate(1, 1), RenderModel.Side.TOP));
    }

    @Test
    void testCoordOnAppBottom() {
        var fixture = new RenderModel();
        assertEquals(new Point(530, 410),
                fixture.coordOnApp(1, 50, 50, new Coordinate(1, 1), RenderModel.Side.BOTTOM));
    }

    @Test
    void testCoordOnAppLeft() {
        var fixture = new RenderModel();
        assertEquals(new Point(410, 350),
                fixture.coordOnApp(1, 50, 50, new Coordinate(1, 1), RenderModel.Side.LEFT));
    }

    @Test
    void testCoordOnAppRight() {
        var fixture = new RenderModel();
        assertEquals(new Point(650, 350),
                fixture.coordOnApp(1, 50, 50, new Coordinate(1, 1), RenderModel.Side.RIGHT));
    }

    @Test
    void testCoordOnAppTopOfBottomLeft() {
        var fixture = new RenderModel();
        assertEquals(new Point(210, 290),
                fixture.coordOnApp(1, 50, 50, new Coordinate(1, 0), RenderModel.Side.TOP));
    }

    @Test
    void testCoordOnAppLeftOfTopRight() {
        var fixture = new RenderModel();
        assertEquals(new Point(410, 150),
                fixture.coordOnApp(1, 50, 50, new Coordinate(0, 1), RenderModel.Side.LEFT));
    }

    @Test
    void testSideFromTo() {
        var fixture = new RenderModel();
        assertEquals(RenderModel.Side.LEFT, fixture.sideTo(true));
        assertEquals(RenderModel.Side.RIGHT, fixture.sideTo(false));
        assertEquals(RenderModel.Side.BOTTOM, fixture.sideFrom(true));
        assertEquals(RenderModel.Side.TOP, fixture.sideFrom(false));
    }

    @Test
    void testEmptyCellsOneEmptyHor() {
        var fixture = new RenderModel();
        var layout = new ComponentLayout(new Component("C1", 0, 0, 3, 3, 1));
        var c1 = new Coordinate(0, 0);
        var c2 = new Coordinate(0, 2);
        layout.add(c1, new Application("A1", "A1", "C1"));
        layout.add(c2, new Application("A2", "A2", "C1"));
        assertTrue(fixture.allCellsEmptyHor(layout, c1, c2));
    }

    @Test
    void testEmptyCellsOneEmptyVert() {
        var fixture = new RenderModel();
        var layout = new ComponentLayout(new Component("C1", 0, 0, 3, 3, 1));
        var c1 = new Coordinate(0, 0);
        var c2 = new Coordinate(2, 0);
        layout.add(c1, new Application("A1", "A1", "C1"));
        layout.add(c2, new Application("A2", "A2", "C1"));
        assertTrue(fixture.allCellsEmptyVert(layout, c1, c2));
    }

    @Test
    void testEmptyCellsTwoEmptyHor() {
        var fixture = new RenderModel();
        var layout = new ComponentLayout(new Component("C1", 0, 0, 3, 3, 1));
        var c1 = new Coordinate(0, 0);
        var c2 = new Coordinate(0, 3);
        layout.add(c1, new Application("A1", "A1", "C1"));
        layout.add(c2, new Application("A2", "A2", "C1"));
        assertTrue(fixture.allCellsEmptyHor(layout, c1, c2));
    }

    @Test
    void testEmptyCellsTwoEmptyVert() {
        var fixture = new RenderModel();
        var layout = new ComponentLayout(new Component("C1", 0, 0, 3, 3, 1));
        var c1 = new Coordinate(0, 0);
        var c2 = new Coordinate(3, 0);
        layout.add(c1, new Application("A1", "A1", "C1"));
        layout.add(c2, new Application("A2", "A2", "C1"));
        assertTrue(fixture.allCellsEmptyVert(layout, c1, c2));
    }

    @Test
    void testThreeCellsInALine() {
        var fixture = new RenderModel();
        var layout = new ComponentLayout(new Component("C1", 0, 0, 3, 3, 1));
        var c1 = new Coordinate(0, 0);
        var c2 = new Coordinate(0, 1);
        var c3 = new Coordinate(0, 2);
        layout.add(c1, new Application("A1", "A1", "C1"));
        layout.add(c2, new Application("A2", "A2", "C1"));
        layout.add(c3, new Application("A3", "A3", "C1"));
        assertFalse(fixture.allCellsEmptyHor(layout, c1, c3));
    }

    @Test
    void testThreeCellsInALineOfFive() {
        var fixture = new RenderModel();
        var layout = new ComponentLayout(new Component("C1", 0, 0, 3, 5, 1));
        var c1 = new Coordinate(0, 0);
        var c2 = new Coordinate(0, 2);
        var c3 = new Coordinate(0, 4);
        layout.add(c1, new Application("A1", "A1", "C1"));
        layout.add(c2, new Application("A2", "A2", "C1"));
        layout.add(c3, new Application("A3", "A3", "C1"));
        assertFalse(fixture.allCellsEmptyHor(layout, c1, c3));
    }

    @Test
    void testThreeCellsInARow() {
        var fixture = new RenderModel();
        var layout = new ComponentLayout(new Component("C1", 0, 0, 3, 3, 1));
        var c1 = new Coordinate(0, 0);
        var c2 = new Coordinate(1, 0);
        var c3 = new Coordinate(2, 0);
        layout.add(c1, new Application("A1", "A1", "C1"));
        layout.add(c2, new Application("A2", "A2", "C1"));
        layout.add(c3, new Application("A3", "A3", "C1"));
        assertFalse(fixture.allCellsEmptyVert(layout, c1, c3));
    }

    @Test
    void testCellsRightToLeft() {
        var fixture = new RenderModel();
        var layout = new ComponentLayout(new Component("C1", 0, 0, 3, 3, 1));
        var c1 = new Coordinate(0, 1);
        var c2 = new Coordinate(1, 0);
        var c3 = new Coordinate(0, 2);
        var c4 = new Coordinate(1, 2);
        var c5 = new Coordinate(1, 1);
        var c6 = new Coordinate(0, 0);
        layout.add(c1, new Application("A1", "A1", "C1"));
        layout.add(c2, new Application("A2", "A2", "C1"));
        layout.add(c3, new Application("A3", "A3", "C1"));
        layout.add(c4, new Application("A4", "A4", "C1"));
        layout.add(c5, new Application("A5", "A5", "C1"));
        layout.add(c6, new Application("A6", "A6", "C1"));
        assertFalse(fixture.allCellsEmptyHor(layout, c3, c6));
    }

    @Test
    void testGetAnchorTwoHorizSideBySide() {
        var fixture = new RenderModel();
        var layout = new ComponentLayout(new Component("C1", 0, 0, 3, 3, 1));
        var c1 = new Coordinate(0, 0);
        var c2 = new Coordinate(1, 0);
        Application a1 = new Application("A1", "A1", "C1");
        Application a2 = new Application("A2", "A2", "C1");
        layout.add(c1, a1);
        layout.add(c2, a2);
        assertThat(fixture.getAnchors(layout, 1, a1, a2, 100, 100))
                .isEmpty();
    }

    @Test
    void testGetAnchorTwoVertSideBySide() {
        var fixture = new RenderModel();
        var layout = new ComponentLayout(new Component("C1", 0, 0, 3, 3, 1));
        var c1 = new Coordinate(0, 0);
        var c2 = new Coordinate(0, 1);
        Application a1 = new Application("A1", "A1", "C1");
        Application a2 = new Application("A2", "A2", "C1");
        layout.add(c1, a1);
        layout.add(c2, a2);
        assertThat(fixture.getAnchors(layout, 1, a1, a2, 100, 100))
                .isEmpty();
    }

    @Test
    void testGetAnchorTwoSameRowWithGap() {
        var fixture = new RenderModel();
        var layout = new ComponentLayout(new Component("C1", 0, 0, 3, 3, 1));
        var c1 = new Coordinate(0, 0);
        var c2 = new Coordinate(0, 2);
        Application a1 = new Application("A1", "A1", "C1");
        Application a2 = new Application("A2", "A2", "C1");
        layout.add(c1, a1);
        layout.add(c2, a2);
        assertThat(fixture.getAnchors(layout, 1, a1, a2, 100, 100))
                .isEmpty();
    }

    @Test
    void testGetAnchorTwoVertWithGap() {
        var fixture = new RenderModel();
        var layout = new ComponentLayout(new Component("C1", 0, 0, 3, 3, 1));
        var c1 = new Coordinate(0, 0);
        var c2 = new Coordinate(2, 0);
        Application a1 = new Application("A1", "A1", "C1");
        Application a2 = new Application("A2", "A2", "C1");
        layout.add(c1, a1);
        layout.add(c2, a2);
        assertThat(fixture.getAnchors(layout, 1, a1, a2, 100, 100))
                .isEmpty();
    }

    @Test
    void testGetAnchorThreeInARow() {
        var fixture = new RenderModel();
        var layout = new ComponentLayout(new Component("C1", 0, 0, 3, 3, 1));
        var c1 = new Coordinate(0, 0);
        var c2 = new Coordinate(0, 1);
        var c3 = new Coordinate(0, 2);
        Application a1 = new Application("A1", "A1", "C1");
        Application a2 = new Application("A2", "A2", "C1");
        Application a3 = new Application("A3", "A3", "C1");
        layout.add(c1, a1);
        layout.add(c2, a2);
        layout.add(c3, a3);
        assertThat(fixture.getAnchors(layout, 1, a1, a3, 100, 100))
                .containsExactly(new Point(260, 120), new Point(900, 120));
    }

    @Test
    void testGetAnchorThreeInACol() {
        var fixture = new RenderModel();
        var layout = new ComponentLayout(new Component("C1", 0, 0, 3, 3, 1));
        var c1 = new Coordinate(0, 0);
        var c2 = new Coordinate(1, 0);
        var c3 = new Coordinate(2, 0);
        Application a1 = new Application("A1", "A1", "C1");
        Application a2 = new Application("A2", "A2", "C1");
        Application a3 = new Application("A3", "A3", "C1");
        layout.add(c1, a1);
        layout.add(c2, a2);
        layout.add(c3, a3);
        assertThat(fixture.getAnchors(layout, 1, a1, a3, 100, 100))
                .containsExactly(new Point(400, 200), new Point(400, 600));
    }

    @Test
    void testGetAnchorTwoOneRowColApartBottomLeftTopRight() {
        var fixture = new RenderModel();
        var layout = new ComponentLayout(new Component("C1", 0, 0, 3, 3, 1));
        var c1 = new Coordinate(1, 0);
        var c2 = new Coordinate(0, 1);
        Application a1 = new Application("A1", "A1", "C1");
        Application a2 = new Application("A2", "A2", "C1");
        layout.add(c1, a1);
        layout.add(c2, a2);
        assertThat(fixture.getAnchors(layout, 1, a1, a2, 100, 100))
                .containsExactly(new Point(260, 320),
                        new Point(440, 320),
                        new Point(440, 200));
    }

    @Test
    void testGetAnchorTwoOneRowColApartTopLeftBottomRight() {
        var fixture = new RenderModel();
        var layout = new ComponentLayout(new Component("C1", 0, 0, 3, 3, 1));
        var c1 = new Coordinate(0, 0);
        var c2 = new Coordinate(1, 1);
        Application a1 = new Application("A1", "A1", "C1");
        Application a2 = new Application("A2", "A2", "C1");
        layout.add(c1, a1);
        layout.add(c2, a2);
        assertThat(fixture.getAnchors(layout, 1, a1, a2, 100, 100))
                .containsExactly(new Point(260, 280),
                        new Point(440, 280),
                        new Point(440, 400));
    }

    @Test
    void testGetAnchorTwoByThreeBottomLeftToTopRight() {
        var fixture = new RenderModel();
        var layout = new ComponentLayout(new Component("C1", 1, 1, 3, 2, 1));
        var c1 = new Coordinate(1, 0);
        var c2 = new Coordinate(0, 2);
        var a1 = new Application("A1", "A1", "C1");
        var a2 = new Application("A2", "A2", "C1");
        layout.add(c1, a1);
        layout.add(c2, a2);
        assertThat(fixture.getAnchors(layout, 1, a1, a2, 320, 500))
                .containsExactly(new Point(480, 720),
                        new Point(980, 720),
                        new Point(980, 600));
    }

}
