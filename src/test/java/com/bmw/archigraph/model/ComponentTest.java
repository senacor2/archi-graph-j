package com.bmw.archigraph.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ComponentTest {

    @Test
    public void testSelectInformationFlows() {
        // fixture
        var model = new Model();
        var l1Comp1 = new Component("l1 1", 0, 0, 0, 0, 1);
        var comp11 = new Component("C11", 0, 0, 0, 0, 2);
        var comp12 = new Component("C12", 0, 0, 0, 0, 2);
        var l1Comp2 = new Component("l1 2", 0, 0, 0, 0, 1);
        var comp21 = new Component("C21", 0, 0, 0, 0, 2);
        l1Comp1.setComponents(List.of(comp11, comp12));
        l1Comp2.setComponents(List.of(comp21));
        var app11_1 = new Application("A111", "App11-1", "C11");
        var app11_2 = new Application("A112", "App11-2", "C11");
        var app12_1 = new Application("A121", "App12-1", "C12");
        var app21_1 = new Application("A211", "App21-1", "C21");
        var ifLocal = new InformationFlow("IFLOCAL", "A111", "A112", "BO1", Direction.ONE_WAY);
        var ifL1Local = new InformationFlow("IFL1LOCAL", "A111", "A121", "BO2", Direction.ONE_WAY);
        var ifXL1 = new InformationFlow("IFXL1", "A111", "A211", "BO3", Direction.ONE_WAY);
        // test
        model.setL1Components(List.of(l1Comp1, l1Comp2));
        model.setApplications(List.of(app11_1, app11_2, app12_1, app21_1));
        model.setInformationFlows(List.of(ifXL1, ifLocal, ifL1Local));
        // verify
        assertThat(comp11.getLocalInformationFlows()).containsExactly(ifLocal);
        assertThat(comp11.getL1CompInformationFlows()).containsExactly(ifL1Local);
        assertThat(comp11.getCrossL1CompInformationFlows()).containsExactly(ifXL1);

        assertThat(comp12.getLocalInformationFlows()).hasSize(0);
        assertThat(comp12.getL1CompInformationFlows()).containsExactly(ifL1Local);
        assertThat(comp12.getCrossL1CompInformationFlows()).hasSize(0);

        assertThat(comp21.getLocalInformationFlows()).hasSize(0);
        assertThat(comp21.getL1CompInformationFlows()).hasSize(0);
        assertThat(comp21.getCrossL1CompInformationFlows()).containsExactly(ifXL1);

        assertThat(l1Comp1.getLocalInformationFlows()).hasSize(0);
        assertThat(l1Comp1.getL1CompInformationFlows()).hasSize(0);
        assertThat(l1Comp1.getCrossL1CompInformationFlows()).hasSize(0);

        assertThat(l1Comp2.getLocalInformationFlows()).hasSize(0);
        assertThat(l1Comp2.getL1CompInformationFlows()).hasSize(0);
        assertThat(l1Comp2.getCrossL1CompInformationFlows()).hasSize(0);
    }

    @Test
    void testSetArea() {

    }
}
