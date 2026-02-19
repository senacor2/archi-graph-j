package com.senacor.archigraph.render;

import com.senacor.archigraph.model.*;
import com.senacor.archigraph.model.Component;
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
                .componentNames(List.of("Level 1"))
                .build();
        var c1 = new L1Component("COMP-1", 1, 0, 3, 3, 1);
        var c2 = new L1Component("COMP-2", 1, 4, 2, 3, 1);
        model.setL1Components(List.of(c1, c2));
        model.setApplications(List.of());
        model.setInformationFlows(List.of());
        var fixture = new RenderModel();
        var result = fixture.render(model);
        assertThat(result.getElements())
                .contains(
                        Rectangle.builder()
                                .id("_graphHeading")
                                .x(0)
                                .y(0)
                                .w(2240)
                                .h(200)
                                .text("System 1")
                                .fontSize(96)
                                .background(new Color(0, 0, 156))
                                .fontcolor(Color.WHITE)
                                .bordercolor(Color.BLACK)
                                .build(),
                        Rectangle.builder()
                                .id("COMP-1_head")
                                .x(0)
                                .y(200)
                                .w(960)
                                .h(200)
                                .text("COMP-1")
                                .fontSize(48)
                                .background(new Color(11, 83, 148))
                                .bordercolor(Color.BLACK)
                                .fontcolor(Color.WHITE)
                                .build(),
                        Rectangle.builder()
                                .id("COMP-1_body")
                                .x(0)
                                .y(400)
                                .w(960)
                                .h(400)
                                .background(Color.WHITE)
                                .bordercolor(Color.BLACK)
                                .build(),
                        Rectangle.builder()
                                .id("COMP-2_head")
                                .text("COMP-2")
                                .x(1280)
                                .y(200)
                                .w(640)
                                .h(200)
                                .fontSize(48)
                                .background(new Color(11, 83, 148))
                                .bordercolor(Color.BLACK)
                                .fontcolor(Color.WHITE)
                                .build(),
                        Rectangle.builder()
                                .id("COMP-2_body")
                                .x(1280)
                                .y(400)
                                .w(640)
                                .h(400)
                                .background(Color.WHITE)
                                .bordercolor(Color.BLACK)
                                .build()
                );
    }

    @Test
    void testModelWithApps() {
        var model = Model.builder()
                .name("System 1")
                .componentNames(List.of("Level 1"))
                .build();
        var c1 = new L1Component("COMP-1", 1, 0, 3, 3, 1);
        model.setL1Components(List.of(c1));
        var a1 = new Application("App-1", "Application 1", "COMP-1");
        var a2 = new Application("App-2", "Application 2", "COMP-1");
        model.setApplications(List.of(a1, a2));
        model.setInformationFlows(List.of());
        var fixture = new RenderModel();
        var result = fixture.render(model);
        assertThat(result.getElements())
                .contains(
                        Rectangle.builder()
                                .id("_graphHeading")
                                .text("System 1")
                                .x(0)
                                .y(0)
                                .w(1280)
                                .h(200)
                                .background(new Color(0, 0, 156))
                                .fontcolor(Color.WHITE)
                                .bordercolor(Color.BLACK)
                                .fontSize(96)
                                .build(),
                        Rectangle.builder()
                                .id("COMP-1_head")
                                .text("COMP-1")
                                .x(0)
                                .y(200)
                                .w(960)
                                .h(200)
                                .background(new Color(11, 83, 148))
                                .fontcolor(Color.WHITE)
                                .bordercolor(Color.BLACK)
                                .fontSize(48)
                                .build(),
                        Rectangle.builder()
                                .id("COMP-1_body")
                                .x(0)
                                .y(400)
                                .w(960)
                                .h(400)
                                .background(Color.WHITE)
                                .bordercolor(Color.BLACK)
                                .build(),
                        Rectangle.builder()
                                .id("App-1")
                                .text("Application 1")
                                .rounded(true)
                                .x(40)
                                .y(440)
                                .w(240)
                                .h(120)
                                .background(Color.BLACK)
                                .fillStyle("solid")
                                .fontcolor(Color.WHITE)
                                .bordercolor(Color.BLACK)
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
                                .background(Color.BLACK)
                                .fillStyle("solid")
                                .fontcolor(Color.WHITE)
                                .bordercolor(Color.BLACK)
                                .fontSize(12)
                                .build()
                );
        assertEquals(new Coordinate(1, 0), c1.getAppMatrix().getAppCoordinate(a1));
        assertEquals(new Coordinate(1, 1), c1.getAppMatrix().getAppCoordinate(a2));
    }

    @Test
    void testModelWithSubComponents() {
        // fixture
        var comp1 = new L1Component("COMP-1", 1, 0, 4, 3, 1);
        var comp2 = new Component("COMP-11", 0, 0, 3, 2, 2);
        var app1 = new Application("A1", "App-1", "COMP-11");
        var app2 = new Application("A2", "App-2", "COMP-11");
        comp1.setComponents(List.of(comp2));
        var model = Model.builder().name("System 1").componentNames(List.of("Level 1", "Level 2")).build();
        model.setL1Components(List.of(comp1));
        model.setApplications(List.of(app1, app2));
        // test
        var renderModel = new RenderModel();
        var result = renderModel.render(model);
        // verify
        assertThat(result.getElements())
                .contains(
                        Rectangle.builder()
                                .id("_graphHeading")
                                .text("System 1")
                                .x(0)
                                .y(0)
                                .w(1600)
                                .h(200)
                                .fontcolor(Color.WHITE)
                                .background(new Color(0, 0, 156))
                                .bordercolor(Color.BLACK)
                                .fontSize(96)
                                .build(),
                        Rectangle.builder()
                                .id("COMP-1_head")
                                .text("COMP-1")
                                .x(0)
                                .y(200)
                                .w(1280)
                                .h(200)
                                .fontcolor(Color.WHITE)
                                .background(new Color(11, 83, 148))
                                .bordercolor(Color.BLACK)
                                .fontSize(48)
                                .build(),
                        Rectangle.builder()
                                .id("COMP-1_body")
                                .x(0)
                                .y(400)
                                .w(1280)
                                .h(400)
                                .background(Color.WHITE)
                                .bordercolor(Color.BLACK)
                                .build(),
                        Rectangle.builder()
                                .id("COMP-11_head")
                                .text("COMP-11")
                                .x(10)
                                .y(410)
                                .w(940)
                                .h(180)
                                .fontcolor(Color.BLACK)
                                .background(new Color(111, 168, 220))
                                .bordercolor(Color.BLACK)
                                .fontSize(40)
                                .build(),
                        Rectangle.builder()
                                .id("COMP-11_body")
                                .x(10)
                                .y(590)
                                .w(940)
                                .h(200)
                                .background(Color.WHITE)
                                .bordercolor(Color.BLACK)
                                .build(),
                        Rectangle.builder()
                                .id("A1")
                                .text("App-1")
                                .rounded(true)
                                .x(40)
                                .y(640)
                                .w(240)
                                .h(120)
                                .background(Color.BLACK)
                                .fillStyle("solid")
                                .fontcolor(Color.WHITE)
                                .bordercolor(Color.BLACK)
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
                                .background(Color.BLACK)
                                .fillStyle("solid")
                                .fontcolor(Color.WHITE)
                                .bordercolor(Color.BLACK)
                                .fontSize(12)
                                .build()
                );
        assertEquals(new Coordinate(2, 0), comp1.getAppMatrix().getAppCoordinate(app1));
        assertEquals(new Coordinate(2, 1), comp1.getAppMatrix().getAppCoordinate(app2));
        assertEquals(new Coordinate(1, 0), comp2.getAppMatrix().getAppCoordinate(app1));
        assertEquals(new Coordinate(1, 1), comp2.getAppMatrix().getAppCoordinate(app2));
    }

    @Test
    void testModelWithInformationFlows() {
        // fixture
        var model = Model.builder()
                .name("System 1")
                .componentNames(List.of("Level 1"))
                .build();
        var app1 = new Application("A1", "A1", "C1");
        var app2 = new Application("A2", "A2", "C1");
        var comp1 = new L1Component("C1", 1, 0, 3, 3, 1);
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
                .fontcolor(Color.WHITE)
                .background(Color.BLACK)
                .fillStyle("solid")
                .bordercolor(Color.BLACK)
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
                .fontcolor(Color.WHITE)
                .background(Color.BLACK)
                .fillStyle("solid")
                .bordercolor(Color.BLACK)
                .fontSize(12)
                .build();
        assertThat(result.getElements())
                .contains(
                        Rectangle.builder()
                                .id("_graphHeading")
                                .text("System 1")
                                .x(0)
                                .y(0)
                                .w(1280)
                                .h(200)
                                .fontcolor(Color.WHITE)
                                .background(new Color(0, 0, 156))
                                .bordercolor(Color.BLACK)
                                .fontSize(96)
                                .build(),
                        Rectangle.builder()
                                .id("C1_body")
                                .x(0)
                                .y(400)
                                .w(960)
                                .h(400)
                                .background(Color.WHITE)
                                .bordercolor(Color.BLACK)
                                .build(),
                        Rectangle.builder()
                                .id("C1_head")
                                .text("C1")
                                .x(0)
                                .y(200)
                                .w(960)
                                .h(200)
                                .fontcolor(Color.WHITE)
                                .background(new Color(11, 83, 148))
                                .bordercolor(Color.BLACK)
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
    void testModelWithL1InformationFlows() {
        // given
        var model = Model.builder()
                .name("System 1")
                .componentNames(List.of("Level 1", "Level 2"))
                .build();
        var app11_1 = new Application("A1", "A1", "C11");
        var app12_2 = new Application("A2", "A2", "C12");
        var app13_3 = new Application("A3", "A3", "C13");
        var comp1 = new L1Component("C1", 1, 1, 3, 3, 1);
        var comp11 = new Component("C11", 0, 0, 1, 2, 2);
        var comp12 = new Component("C12", 0, 1, 1, 2, 2);
        var comp13 = new Component("C13", 0, 2, 1, 2, 2);
        var if1_3 = new InformationFlow("IF1-3", "A1", "A3", "BO", Direction.ONE_WAY);
        comp1.setComponents(List.of(comp11, comp12, comp13));
        model.setL1Components(List.of(comp1));
        model.setApplications(List.of(app11_1, app12_2, app13_3));
        model.setInformationFlows(List.of(if1_3));
        // when
        var fixture = new RenderModel();
        var result = fixture.render(model);
        // then
        assertThat(result.getElements())
                .contains(Line.builder()
                        .start(Rectangle.builder()
                                .id("A1")
                                .text("A1")
                                .x(360)
                                .y(640)
                                .w(RenderModel.APP_WIDTH)
                                .h(RenderModel.APP_HEIGHT)
                                .background(Color.BLACK)
                                .bordercolor(Color.BLACK)
                                .fontcolor(Color.WHITE)
                                .fontSize(12)
                                .fillStyle("solid")
                                .rounded(true)
                                .layer(null)
                                .build())
                        .end(Rectangle.builder()
                                .id("A3")
                                .text("A3")
                                .x(1000)
                                .y(640)
                                .w(RenderModel.APP_WIDTH)
                                .h(RenderModel.APP_HEIGHT)
                                .background(Color.BLACK)
                                .bordercolor(Color.BLACK)
                                .fontcolor(Color.WHITE)
                                .fontSize(12)
                                .fillStyle("solid")
                                .rounded(true)
                                .layer(null)
                                .build())
                        .id("IF1-3")
                        .text("BO")
                        .hasEndArrow(true)
                        .anchors(new Point[]{
                                new Point(480, 620),
                                new Point(1120, 620)
                        })
                        .build()
                );
    }

    @Test
    void testModelWithLocalInformationFlows() {
        // given
        var model = Model.builder()
                .name("System 1")
                .componentNames(List.of("Level 1", "Level 2"))
                .build();
        var app1 = new Application("A1", "A1", "C111");
        var app2 = new Application("A2", "A2", "C111");
        var app3 = new Application("A3", "A3", "C111");
        var comp1 = new L1Component("C1", 1, 0, 3, 4, 1);
        var comp11 = new Component("C11", 0, 0, 3, 3, 2);
        var comp111 = new Component("C111", 0, 0, 3, 2, 3);
        var if12 = new InformationFlow("IF12", "A1", "A2", "BO", Direction.ONE_WAY);
        var if13 = new InformationFlow("IF13", "A1", "A3", "BO", Direction.ONE_WAY);
        var if23 = new InformationFlow("IF23", "A2", "A3", "BO", Direction.ONE_WAY);
        comp1.setComponents(List.of(comp11));
        comp11.setComponents(List.of(comp111));
        model.setL1Components(List.of(comp1));
        model.setApplications(List.of(app1, app2, app3));
        model.setInformationFlows(List.of(if12, if13, if23));
        // when
        var fixture = new RenderModel();
        var result = fixture.render(model);
        // then
        assertThat(result.getElements())
                .filteredOn(rme -> rme instanceof Line)
                .map(rme -> (Line) rme)
                .extracting(Line::getAnchors)
                .filteredOn(pts -> pts.length == 0)
                .hasSize(2);
        assertThat(result.getElements())
                .filteredOn(rme -> rme instanceof Line)
                .map(rme -> (Line) rme)
                .extracting(Line::getAnchors)
                .filteredOn(pts -> pts.length == 2)
                .hasSize(1);
        assertThat(result.getElements())
                .filteredOn(rme -> rme instanceof Line)
                .hasSize(3);
    }
    @Test
    void testModelWithCrossL1InformationFlows() {
        // given
        var model = Model.builder()
                .name("System 1")
                .componentNames(List.of("Level 1", "Level 2"))
                .build();
        var app11 = new Application("A11", "A11", "C11");
        var app12 = new Application("A12", "A12", "C11");
        var app2 = new Application("A2", "A2", "C2");
        var comp1 = new L1Component("C1", 1, 0, 3, 5, 1);
        var comp11 = new Component("C11", 0, 0, 1, 3, 2);
        var comp2 = new L1Component("C2", 9, 9, 3, 3, 1);
        var if1 = new InformationFlow("IF11-2", "A11", "A2", "BO", Direction.ONE_WAY);
        var if2 = new InformationFlow("IF12-2", "A12", "A2", "BO", Direction.ONE_WAY);
        var if3 = new InformationFlow("IF11-12", "A11", "A12", "BO", Direction.ONE_WAY);
        comp1.setComponents(List.of(comp11));
        model.setL1Components(List.of(comp1, comp2));
        model.setApplications(List.of(app11, app12, app2));
        model.setInformationFlows(List.of(if1, if2, if3));
        // when
        var fixture = new RenderModel();
        var result = fixture.render(model);
        // then
        var rectApp11 = Rectangle.builder()
                .id("A11")
                .text("A11")
                .rounded(true)
                .x(40)
                .y(640)
                .w(240)
                .h(120)
                .bordercolor(Color.BLACK)
                .fontcolor(Color.WHITE)
                .background(Color.BLACK)
                .fillStyle("solid")
                .fontSize(12)
                .build();
        var rectApp12 = Rectangle.builder()
                .id("A12")
                .text("A12")
                .rounded(true)
                .x(40)
                .y(840)
                .w(240)
                .h(120)
                .bordercolor(Color.BLACK)
                .fontcolor(Color.WHITE)
                .background(Color.BLACK)
                .fillStyle("solid")
                .fontSize(12)
                .build();
        var rectApp2 = Rectangle.builder()
                .id("C1_proxy_A2_1")
                .text("A2")
                .rounded(true)
                .x(-280)
                .y(640)
                .w(240)
                .h(120)
                .fontcolor(Color.WHITE)
                .bordercolor(Color.BLACK)
                .background(Color.BLACK)
                .fontSize(12)
                .fillStyle("solid")
                .layer(RenderModel.PROXIES)
                .originalId("A2")
                .build();
        assertThat(result.getElements())
                .contains(
                        rectApp11,
                        rectApp12,
                        rectApp2,
                        Line.builder()
                                .id("IF11-2")
                                .text("BO")
                                .start(rectApp11)
                                .end(rectApp2)
                                .layer(RenderModel.PROXIES)
                                .anchors(new Point[0])
                                .build(),
                        Line.builder()
                                .id("IF12-2")
                                .text("BO")
                                .start(rectApp12)
                                .end(rectApp2)
                                .layer(RenderModel.PROXIES)
                                .anchors(new Point[]{
                                        new Point(160, 820),
                                        new Point(-20, 820),
                                        new Point(-20, 700)
                                })
                                .build()
                );
    }

    @Test
    void testCoordOnAppTop() {
        var fixture = new RenderModel();
        var rect = Rectangle.builder()
                .x(50)
                .y(50)
                .w(RenderModel.APP_WIDTH)
                .h(RenderModel.APP_HEIGHT)
                .build();
        assertEquals(new Point(170, 50),
                fixture.coordOnApp(rect, RenderModel.Side.TOP));
    }

    @Test
    void testCoordOnAppBottom() {
        var fixture = new RenderModel();
        var rect = Rectangle.builder()
                .x(50)
                .y(50)
                .w(RenderModel.APP_WIDTH)
                .h(RenderModel.APP_HEIGHT)
                .build();
        assertEquals(new Point(170, 170),
                fixture.coordOnApp(rect, RenderModel.Side.BOTTOM));
    }

    @Test
    void testCoordOnAppLeft() {
        var fixture = new RenderModel();
        var rect = Rectangle.builder()
                .x(50)
                .y(50)
                .w(RenderModel.APP_WIDTH)
                .h(RenderModel.APP_HEIGHT)
                .build();
        assertEquals(new Point(50, 110),
                fixture.coordOnApp(rect, RenderModel.Side.LEFT));
    }

    @Test
    void testCoordOnAppRight() {
        var fixture = new RenderModel();
        var rect = Rectangle.builder()
                .x(50)
                .y(50)
                .w(RenderModel.APP_WIDTH)
                .h(RenderModel.APP_HEIGHT)
                .build();
        assertEquals(new Point(290, 110),
                fixture.coordOnApp(rect, RenderModel.Side.RIGHT));
    }

    @Test
    void testSideFromTo() {
        var fixture = new RenderModel();
        assertEquals(RenderModel.Side.LEFT, fixture.sideTo(true));
        assertEquals(RenderModel.Side.RIGHT, fixture.sideTo(false));
        assertEquals(RenderModel.Side.BOTTOM, fixture.sideFrom(true));
        assertEquals(RenderModel.Side.TOP, fixture.sideFrom(false));
    }

    private Rectangle buildRect(int row, int col, Component comp) {
        return Rectangle.builder()
                .x(col * RenderModel.COL_WIDTH + RenderModel.SPACING + comp.getCol() * RenderModel.COL_WIDTH)
                .y(row * RenderModel.ROW_HEIGHT + RenderModel.SPACING + comp.getRow() * RenderModel.ROW_HEIGHT)
                .w(RenderModel.APP_WIDTH)
                .h(RenderModel.APP_HEIGHT)
                .build();
    }

    @Test
    void testGetAnchorTwoVertSideBySide() {
        // given
        var model = new RenderModel();
        var comp = new Component("C1", 0, 0, 3, 3, 1);
        comp.setOrigin(0, 0);
        var c1 = new Coordinate(0, 0);
        var c2 = new Coordinate(1, 0);
        var rect1 = buildRect(c1.row(), c1.col(), comp);
        var rect2 = buildRect(c2.row(), c2.col(), comp);
        var fixture = comp.getAppMatrix();
        Application a1 = new Application("A1", "A1", "C1");
        Application a2 = new Application("A2", "A2", "C1");
        // when
        fixture.put(c1, a1);
        fixture.put(c2, a2);
        // then
        assertThat(model.getAnchors(fixture, rect1, rect2))
                .isEmpty();
    }

    @Test
    void testGetAnchorTwoHorizSideBySide() {
        // given
        var model = new RenderModel();
        var comp = new Component("C1", 0, 0, 3, 3, 1);
        comp.setOrigin(0, 0);
        var c1 = new Coordinate(0, 0);
        var c2 = new Coordinate(0, 1);
        var rect1 = buildRect(c1.row(), c1.col(), comp);
        var rect2 = buildRect(c2.row(), c2.col(), comp);
        var fixture = comp.getAppMatrix();
        Application a1 = new Application("A1", "A1", "C1");
        Application a2 = new Application("A2", "A2", "C1");
        // when
        fixture.put(c1, a1);
        fixture.put(c2, a2);
        // then
        assertThat(model.getAnchors(fixture, rect1, rect2))
                .isEmpty();
    }

    @Test
    void testGetAnchorTwoSameRowWithGap() {
        // given
        var model = new RenderModel();
        var comp = new Component("C1", 0, 0, 3, 3, 1);
        comp.setOrigin(0, 0);
        var c1 = new Coordinate(0, 0);
        var c2 = new Coordinate(0, 2);
        var rect1 = buildRect(c1.row(), c1.col(), comp);
        var rect2 = buildRect(c2.row(), c2.col(), comp);
        var fixture = comp.getAppMatrix();
        Application a1 = new Application("A1", "A1", "C1");
        Application a2 = new Application("A2", "A2", "C1");
        // when
        fixture.put(c1, a1);
        fixture.put(c2, a2);
        // then
        assertThat(model.getAnchors(fixture, rect1, rect2))
                .isEmpty();
    }

    @Test
    void testGetAnchorTwoVertWithGap() {
        // given
        var model = new RenderModel();
        var comp = new Component("C1", 0, 0, 3, 3, 1);
        comp.setOrigin(0, 0);
        var fixture = comp.getAppMatrix();
        var c1 = new Coordinate(0, 0);
        var c2 = new Coordinate(2, 0);
        var rect1 = buildRect(c1.row(), c1.col(), comp);
        var rect2 = buildRect(c2.row(), c2.col(), comp);
        Application a1 = new Application("A1", "A1", "C1");
        Application a2 = new Application("A2", "A2", "C1");
        // when
        fixture.put(c1, a1);
        fixture.put(c2, a2);
        // then
        assertThat(model.getAnchors(fixture, rect1, rect2))
                .isEmpty();
    }

    @Test
    void testGetAnchorThreeInARow() {
        // given
        var model = new RenderModel();
        var comp = new Component("C1", 0, 0, 3, 3, 1);
        comp.setOrigin(0, 0);
        var fixture = comp.getAppMatrix();
        var c1 = new Coordinate(1, 0);
        var c2 = new Coordinate(1, 1);
        var c3 = new Coordinate(1, 2);
        var rect1 = buildRect(c1.row(), c1.col(), comp);
        var rect2 = buildRect(c3.row(), c3.col(), comp);
        Application a1 = new Application("A1", "A1", "C1");
        Application a2 = new Application("A2", "A2", "C1");
        Application a3 = new Application("A3", "A3", "C1");
        // when
        fixture.put(c1, a1);
        fixture.put(c2, a2);
        fixture.put(c3, a3);
        // then
        assertThat(model.getAnchors(fixture, rect1, rect2))
                .containsExactly(new Point(160, 220), new Point(800, 220));
    }

    @Test
    void testGetAnchorThreeInACol() {
        // given
        var model = new RenderModel();
        var comp = new Component("C1", 0, 0, 3, 3, 1);
        comp.setOrigin(0, 0);
        var fixture = comp.getAppMatrix();
        var c1 = new Coordinate(0, 0);
        var c2 = new Coordinate(1, 0);
        var c3 = new Coordinate(2, 0);
        var rect1 = buildRect(c1.row(), c1.col(), comp);
        var rect2 = buildRect(c3.row(), c3.col(), comp);
        Application a1 = new Application("A1", "A1", "C1");
        Application a2 = new Application("A2", "A2", "C1");
        Application a3 = new Application("A3", "A3", "C1");
        // when
        fixture.put(c1, a1);
        fixture.put(c2, a2);
        fixture.put(c3, a3);
        // then
        assertThat(model.getAnchors(fixture, rect1, rect2))
                .containsExactly(new Point(300, 100), new Point(300, 500));
    }

    @Test
    void testGetAnchorTwoOneRowColApartBottomLeftTopRight() {
        // given
        var model = new RenderModel();
        var comp = new Component("C1", 0, 0, 3, 3, 1);
        comp.setOrigin(0, 0);
        var fixture = comp.getAppMatrix();
        var c1 = new Coordinate(2, 0);
        var c2 = new Coordinate(1, 1);
        var rect1 = buildRect(c1.row(), c1.col(), comp);
        var rect2 = buildRect(c2.row(), c2.col(), comp);
        Application a1 = new Application("A1", "A1", "C1");
        Application a2 = new Application("A2", "A2", "C1");
        // when
        fixture.put(c1, a1);
        fixture.put(c2, a2);
        // then
        assertThat(model.getAnchors(fixture, rect1, rect2))
                .containsExactly(new Point(160, 420),
                        new Point(340, 420),
                        new Point(340, 300));
    }

    @Test
    void testGetAnchorTwoOneRowColApartTopLeftBottomRight() {
        // given
        var model = new RenderModel();
        var comp = new Component("C1", 0, 0, 3, 3, 1);
        comp.setOrigin(0, 0);
        var fixture = comp.getAppMatrix();
        var c1 = new Coordinate(1, 0);
        var c2 = new Coordinate(2, 1);
        var rect1 = buildRect(c1.row(), c1.col(), comp);
        var rect2 = buildRect(c2.row(), c2.col(), comp);
        Application a1 = new Application("A1", "A1", "C1");
        Application a2 = new Application("A2", "A2", "C1");
        // when
        fixture.put(c1, a1);
        fixture.put(c2, a2);
        // then
        assertThat(model.getAnchors(fixture, rect1, rect2))
                .containsExactly(new Point(160, 380),
                        new Point(340, 380),
                        new Point(340, 500));
    }

    @Test
    void testGetAnchorTwoByThreeBottomLeftToTopRight() {
        // given
        var model = new RenderModel();
        var comp = new Component("C1", 0, 0, 2, 3, 1);
        comp.setOrigin(0, 0);
        var fixture = comp.getAppMatrix();
        var c1 = new Coordinate(2, 0);
        var c2 = new Coordinate(1, 1);
        var rect1 = buildRect(c1.row(), c1.col(), comp);
        var rect2 = buildRect(c2.row(), c2.col(), comp);
        var a1 = new Application("A1", "A1", "C1");
        var a2 = new Application("A2", "A2", "C1");
        // when
        fixture.put(c1, a1);
        fixture.put(c2, a2);
        System.out.println(fixture.dump());
        // then
        assertThat(model.getAnchors(fixture, rect1, rect2))
                .containsExactly(new Point(160, 420),
                        new Point(340, 420),
                        new Point(340, 300));
    }

}
