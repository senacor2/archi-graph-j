package com.senacor.archigraph.layout;

import com.senacor.archigraph.model.*;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;

public class HeuristicComponentLayoutTest {


    @Test
    void testLargeBoxAndManyApps() {
        var model = new Model();
        var c1 = new L1Component("C1", 1, 1, 6, 38, 0);
        var apps = new LinkedList<Application>();
        for (int i = 0; i < 100; i++) {
            apps.add(new Application("a" + i, "A" + i, "C1"));
        }
        var flows = new LinkedList<InformationFlow>();
        for (int i = 0; i < 98; i = i+2) {
            flows.add(new InformationFlow("if" + i, "a" + i, "a" + i + 1, "", Direction.ONE_WAY));
        }
        model.setL1Components(List.of(c1));
        model.setApplications(apps);
        model.setInformationFlows(flows);

        // when
        c1.layout();

        // then
        assertThat(c1.getApplications()).isNotNull();
        assertThat(StreamSupport.stream(c1.getAppMatrix().usedCoordinates().spliterator(), false).count())
                .isEqualTo(100);
    }

}
