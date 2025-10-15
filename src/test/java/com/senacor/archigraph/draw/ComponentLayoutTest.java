package com.senacor.archigraph.draw;

import com.senacor.archigraph.model.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

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
        var cl = new ComponentLayout(new Component("C1", 0, 0, 2, 3, 1));
        assertEquals(
                List.of(List.of(nc(0, 0)),
                        List.of(nc(0, 1)),
                        List.of(nc(1, 0)),
                        List.of(nc(1, 1))),
                cl.appPositionsInComponent(1));
    }

    @Test
    void testAppPositionsInComponentWithTwoApps() {
        var cl = new ComponentLayout(new Component("C1", 0, 0, 2, 3, 1));
        assertThat(cl.appPositionsInComponent(2))
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
        var cl = new ComponentLayout(new Component("C1", 0, 0, 2, 3, 1));
        assertThat(cl.appPositionsInComponent(3))
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
        var cl = new ComponentLayout(new Component("c1", 0, 0, 2, 2, 1));
        var exception = assertThrows(IllegalArgumentException.class,
                () -> cl.appPositionsInComponent(5));
        assertEquals("Number of apps (5) exceeds grid size (1 x 2)",
                exception.getMessage());
    }

    @Test
    void testLinesIntersect() {
        var cl = new ComponentLayout(new Component("Comp1", 0, 0, 2, 2, 1));
        assertTrue(cl.linesIntersect(nc(0, 0), nc(1, 1), nc(1, 0), nc(0, 1)));
        assertTrue(cl.linesIntersect(nc(1, 0), nc(0, 1), nc(0, 0), nc(1, 1)));
    }

    @Test
    void testLinesDoNotIntersect() {
        var cl = new ComponentLayout(new Component("Comp1", 0, 0, 2, 2, 1));
        assertFalse(cl.linesIntersect(nc(0, 0), nc(0, 1), nc(1, 0), nc(1, 1)));
        assertFalse(cl.linesIntersect(nc(1, 0), nc(1, 1), nc(0, 0), nc(0, 1)));
        assertFalse(cl.linesIntersect(nc(0, 0), nc(1, 0), nc(0, 1), nc(1, 1)));
        assertFalse(cl.linesIntersect(nc(0, 1), nc(1, 1), nc(0, 0), nc(1, 0)));
    }

    @Test
    void testLinesWithSameEndpoint() {
        var cl = new ComponentLayout(new Component("Comp1", 0, 0, 2, 2, 1));
        assertFalse(cl.linesIntersect(nc(0,1), nc(1, 1), nc(0, 0), nc(1, 1)));
    }

    @Test
    void testLinesIntersectViceVersa() {
        var cl = new ComponentLayout(new Component("Comp1", 0, 0, 2, 2, 1));
        assertFalse(cl.linesIntersect(nc(0, 0), nc(0, 1), nc(0, 1), nc(0, 0)));
    }

    @Test
    void testLinesIntersectWithSixApps() {
        var cl = new ComponentLayout(new Component("Comp1", 0, 0, 2, 2, 1));
        assertFalse(cl.linesIntersect(nc(2, 2), nc(1, 2), nc(0, 2), nc(2, 1)));
        assertFalse(cl.linesIntersect(nc(0, 2), nc(2, 1), nc(0, 1), nc(1, 1)));
        assertFalse(cl.linesIntersect(nc(2, 2), nc(1, 2), nc(0, 1), nc(1, 1)));
    }

    @Test
    void testLayoutQualityNoIntersections() {
        var cl = new ComponentLayout(new Component("Comp 1", 0, 0, 2, 2, 1));
        var appA = new Application("A", "A", "C", "", "", "");
        var appB = new Application("B", "B", "C", "", "", "");
        var appC = new Application("C", "C", "C", "", "", "");
        var appD = new Application("D", "D", "C", "", "", "");
        var ifAB = new InformationFlow("IFAB", "A", "B", "BO", Direction.ONE_WAY);
        var ifCD = new InformationFlow("IFCD", "C", "D", "BO", Direction.ONE_WAY);
        ifAB.setSource(appA);
        ifAB.setDestination(appB);
        ifCD.setSource(appC);
        ifCD.setDestination(appD);
        var appPos = Map.of(
                appA, nc(0, 0),
                appB, nc(0, 1),
                appC, nc(1, 0),
                appD, nc(1, 1));
        assertEquals(0, cl.layoutQuality(appPos, List.of(ifAB, ifCD)).getQuality());
    }

    @Test
    void testLayoutQualityDiagonalIntersections() {
        var cl = new ComponentLayout(new Component("Comp1", 0, 0, 2, 2, 1));
        var appA = new Application("A", "A", "C", "", "", "");
        var appB = new Application("B", "B", "C", "", "", "");
        var appC = new Application("C", "C", "C", "", "", "");
        var appD = new Application("D", "D", "C", "", "", "");
        var ifAD = new InformationFlow("IFAD", "A", "D", "BO", Direction.ONE_WAY);
        var IFBC = new InformationFlow("IFBC", "B", "C", "BO", Direction.ONE_WAY);
        ifAD.setSource(appA);
        ifAD.setDestination(appD);
        IFBC.setSource(appB);
        IFBC.setDestination(appC);
        var appPos = Map.of(
                appA, nc(0, 0),
                appB, nc(0, 1),
                appC, nc(1, 0),
                appD, nc(1, 1));
        assertEquals(1, cl.layoutQuality(appPos, List.of(ifAD, IFBC)).getQuality());
    }

    @Test
    void testLayoutQualitySameEndpoint() {
        var cl = new ComponentLayout(new Component("Comp1", 0, 0, 2, 2, 1));
        var appA = new Application("A", "A", "C", "", "", "");
        var appB = new Application("B", "B", "C", "", "", "");
        var appC = new Application("C", "C", "C", "", "", "");
        var ifAB = new InformationFlow("IFAB", "A", "B", "BO", Direction.ONE_WAY);
        var IFCB = new InformationFlow("IFCB", "C", "B", "BO", Direction.ONE_WAY);
        ifAB.setSource(appA);
        ifAB.setDestination(appB);
        IFCB.setSource(appC);
        IFCB.setDestination(appB);
        var appPos = Map.of(
                appA, nc(0, 0),
                appB, nc(0, 1),
                appC, nc(1, 1));
        assertEquals(0, cl.layoutQuality(appPos, List.of(ifAB, IFCB)).getQuality());
    }

    @Test
    void TestLayoutQualityThreeFlows() {
        var cl = new ComponentLayout(new Component("Comp1", 0, 0, 3, 3, 1));
        var appA = new Application("A", "A", "C", "", "", "");
        var appB = new Application("B", "B", "C", "", "", "");
        var appC = new Application("C", "C", "C", "", "", "");
        var appD = new Application("D", "D", "C", "", "", "");
        var appE = new Application("E", "E", "C", "", "", "");
        var appF = new Application("F", "F", "C", "", "", "");
        var ifAB = new InformationFlow("IFAB", "A", "B", "BO", Direction.ONE_WAY);
        var ifCD = new InformationFlow("IFCD", "C", "D", "BO", Direction.ONE_WAY);
        var ifEF = new InformationFlow("IFEF", "E", "F", "BO", Direction.ONE_WAY);
        ifAB.setSource(appA);
        ifAB.setDestination(appB);
        ifCD.setSource(appC);
        ifCD.setDestination(appD);
        ifEF.setSource(appE);
        ifEF.setDestination(appF);
        var appPos = Map.of(
                appA, nc(0, 0),
                appB, nc(2, 2),
                appC, nc(0, 2),
                appD, nc(2, 0),
                appE, nc(1, 0),
                appF, nc(1, 2));
        assertEquals(3, cl.layoutQuality(appPos, List.of(ifAB, ifCD, ifEF)).getQuality());
    }

    @Test
    void testFindAppPositionsEmptyComponent() {
        var comp = new Component("COMP-1", 0, 0, 2, 2, 1);
        var cl = new ComponentLayout(comp);
        cl.layout();
        assertEquals(0, cl.getQuality());
    }

    @Test
    void testFindAppPositionsWithoutFlows() {
        // fixture
        var comp = new Component("COMP-1", 0, 0, 2, 2, 1);
        var appA = new Application("APP-A", "A1", "COMP-1", "", "", "");
        var appB = new Application("APP-B", "A2", "COMP-1", "", "", "");
        var appC = new Application("APP-C", "A3", "COMP-1", "", "", "");
        var appD = new Application("APP-D", "A4", "COMP-1", "", "", "");
        var model = new Model();
        model.setL1Components(List.of(comp));
        model.setApplications(List.of(appA, appB, appC, appD));
        // test
        var cl = new ComponentLayout(comp);
        cl.layout();
        // verify
        assertEquals(nc(0, 0), cl.getAppCoordinate(appA), "App-A");
        assertEquals(nc(0, 1), cl.getAppCoordinate(appB), "App-B");
        assertEquals(nc(1, 0), cl.getAppCoordinate(appC), "App-C");
        assertEquals(nc(1, 1), cl.getAppCoordinate(appD), "App-D");
        assertEquals(0, cl.getQuality());
    }

    @Test
    void testFindAppPositions2in2x2() {
        // fixture
        var model = new Model();
        var comp = new Component("COMP-1", 0, 0, 2, 3, 1);
        var appA = new Application("APP-A", "A1", "COMP-1", "", "", "");
        var appB = new Application("APP-B", "A2", "COMP-1", "", "", "");
        var appC = new Application("APP-C", "A3", "COMP-1", "", "", "");
        var appD = new Application("APP-D", "A4", "COMP-1", "", "", "");
        var ifAC = new InformationFlow("IF-AC", "APP-A", "APP-C", "BO", Direction.ONE_WAY);
        var ifBD = new InformationFlow("IF-BD", "APP-B", "APP-D", "BO", Direction.ONE_WAY);
        model.setL1Components(List.of(comp));
        model.setApplications(List.of(appA, appB, appC, appD));
        model.setInformationFlows(List.of(ifAC, ifBD));
        // test
        var cl = new ComponentLayout(comp);
        cl.layout();
        // verify
        assertEquals(nc(0, 0), cl.getAppCoordinate(appA), "App-A");
        assertEquals(nc(0, 1), cl.getAppCoordinate(appB), "App-B");
        assertEquals(nc(1, 0), cl.getAppCoordinate(appC), "App-C");
        assertEquals(nc(1, 1), cl.getAppCoordinate(appD), "App-D");
        assertEquals(0, cl.getQuality());
    }

    @Test
    void testFindAppPositions6in3x3() {
        // fixture
        var comp = new Component("COMP-1", 0, 0, 3, 3, 1);
        var appA = new Application("APP-A", "A1", "COMP-1", "", "", "");
        var appB = new Application("APP-B", "A2", "COMP-1", "", "", "");
        var appC = new Application("APP-C", "A3", "COMP-1", "", "", "");
        var appD = new Application("APP-D", "A4", "COMP-1", "", "", "");
        var appE = new Application("APP-E", "A5", "COMP-1", "", "", "");
        var appF = new Application("APP-F", "A6", "COMP-1", "", "", "");
        var ifAB = new InformationFlow("IF-AB", "APP-A", "APP-B", "BO", Direction.ONE_WAY);
        var ifCD = new InformationFlow("IF-CD", "APP-C", "APP-D", "BO", Direction.ONE_WAY);
        var ifEF = new InformationFlow("IF-EF", "APP-E", "APP-F", "BO", Direction.ONE_WAY);
        var model = new Model();
        model.setL1Components(List.of(comp));
        model.setApplications(List.of(appA, appB, appC, appD, appE, appF));
        model.setInformationFlows(List.of(ifAB, ifCD, ifEF));
        // test
        var cl = new ComponentLayout(comp);
        cl.layout();
        // verify
        assertEquals(nc(0, 0), cl.getAppCoordinate(appA), "App-A");
        assertEquals(nc(0, 1), cl.getAppCoordinate(appB), "App-B");
        assertEquals(nc(0, 2), cl.getAppCoordinate(appC), "App-C");
        assertEquals(nc(1, 0), cl.getAppCoordinate(appD), "App-D");
        assertEquals(nc(1, 1), cl.getAppCoordinate(appE), "App-E");
        assertEquals(nc(1, 2), cl.getAppCoordinate(appF), "App-F");
        assertEquals(0, cl.getQuality());
    }

    @Test
    void testFindAppPositions6in3x3WithMoreFlows() {
        // fixture
        var comp = new Component("COMP-1", 0, 0, 3, 3, 1);
        var appA = new Application("APP-A", "A1", "COMP-1", "", "", "");
        var appB = new Application("APP-B", "A2", "COMP-1", "", "", "");
        var appC = new Application("APP-C", "A3", "COMP-1", "", "", "");
        var appD = new Application("APP-D", "A4", "COMP-1", "", "", "");
        var appE = new Application("APP-E", "A5", "COMP-1", "", "", "");
        var appF = new Application("APP-F", "A6", "COMP-1", "", "", "");

        var ifAB = new InformationFlow("IF-AB", "APP-A", "APP-B", "BO", Direction.ONE_WAY);
        var ifAC = new InformationFlow("IF-AC", "APP-A", "APP-C", "BO", Direction.ONE_WAY);

        var ifBD = new InformationFlow("IF-BD", "APP-B", "APP-D", "BO", Direction.ONE_WAY);
        var ifBE = new InformationFlow("IF-BE", "APP-B", "APP-E", "BO", Direction.ONE_WAY);
        var ifBF = new InformationFlow("IF-BF", "APP-B", "APP-F", "BO", Direction.ONE_WAY);

        var ifCD = new InformationFlow("IF-CD", "APP-C", "APP-D", "BO", Direction.ONE_WAY);

        var ifDA = new InformationFlow("IF-DA", "APP-D", "APP-A", "BO", Direction.ONE_WAY);

        var ifEF = new InformationFlow("IF-EF", "APP-E", "APP-F", "BO", Direction.ONE_WAY);

        var ifFB = new InformationFlow("IF-FB", "APP-F", "APP-B", "BO", Direction.ONE_WAY);

        var model = new Model();
        model.setL1Components(List.of(comp));
        model.setApplications(List.of(appA, appB, appC, appD, appE, appF));
        model.setInformationFlows(List.of(ifAB, ifAC, ifBD, ifBE, ifBF, ifCD, ifDA, ifEF, ifFB));

        // test
        var cl = new ComponentLayout(comp);
        cl.layout();
        // verify
        assertEquals(nc(0, 0), cl.getAppCoordinate(appA), "App-A");
        assertEquals(nc(1, 0), cl.getAppCoordinate(appB), "App-B");
        assertEquals(nc(0, 1), cl.getAppCoordinate(appC), "App-C");
        assertEquals(nc(0, 2), cl.getAppCoordinate(appD), "App-D");
        assertEquals(nc(1, 1), cl.getAppCoordinate(appE), "App-E");
        assertEquals(nc(1, 2), cl.getAppCoordinate(appF), "App-F");
        assertEquals(1, cl.getQuality());
    }

    @Test
    void testAddLayouts() {
        // Fixture
        Component outerComp = new Component("Outer", 0, 0, 4, 5, 1);
        Application app1 = new Application("A1", "App1", "Outer", "", "", "");
        Application app2 = new Application("A2", "App2", "Outer", "", "", "");
        Component innerComp = new Component("Inner", 2, 2, 2, 2, 2);
        Application app3 = new Application("A3", "App3", "Inner", "", "", "");
        Application app4 = new Application("A4", "App4", "Inner", "", "", "");
        outerComp.setComponents(List.of(innerComp));
        Model model = new Model();
        model.setL1Components(List.of(outerComp));
        model.setApplications(List.of(app1, app2, app3, app4));
        // test
        innerComp.layout();
        outerComp.layout();
        outerComp.getAppMatrix().merge(innerComp.getAppMatrix(), innerComp.getRow()+1, innerComp.getCol());
        System.out.println(outerComp.getAppMatrix().dump());
        // verify
        assertEquals(nc(1, 0), outerComp.getAppCoordinate(app1), "App1");
        assertEquals(nc(1, 1), outerComp.getAppCoordinate(app2), "App2");
        assertEquals(nc(4, 2), outerComp.getAppCoordinate(app3), "App3");
        assertEquals(nc(4, 3), outerComp.getAppCoordinate(app4), "App4");
    }

    @Test
    void testAddLayoutsWithAppAreaOnOuterComp() {
        // Fixture
        Component outerComp = new Component("Outer", 0, 0, 4, 5, 1);
        outerComp.setAppArea(new Area(1, 3, 1, 2));
        Application app1 = new Application("A1", "App1", "Outer", "", "", "");
        Application app2 = new Application("A2", "App2", "Outer", "", "", "");
        Component innerComp = new Component("Inner", 1, 1, 2, 2, 2);
        Application app3 = new Application("A3", "App3", "Inner", "", "", "");
        Application app4 = new Application("A4", "App4", "Inner", "", "", "");
        outerComp.setComponents(List.of(innerComp));
        Model model = new Model();
        model.setL1Components(List.of(outerComp));
        model.setApplications(List.of(app1, app2, app3, app4));
        // test
        innerComp.layout();
        outerComp.layout();
        outerComp.getAppMatrix().merge(innerComp.getAppMatrix(), innerComp.getRow()+1, innerComp.getCol());
        System.out.println(outerComp.getAppMatrix().dump());
        // verify
        assertEquals(nc(2, 3), outerComp.getAppCoordinate(app1), "App1");
        assertEquals(nc(3, 3), outerComp.getAppCoordinate(app2), "App2");
        assertEquals(nc(3, 1), outerComp.getAppCoordinate(app3), "App3");
        assertEquals(nc(3, 2), outerComp.getAppCoordinate(app4), "App4");

    }
}
