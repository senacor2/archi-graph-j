package com.senacor.archigraph.layout;

import com.senacor.archigraph.model.Application;
import com.senacor.archigraph.model.Component;
import com.senacor.archigraph.model.Coordinate;
import com.senacor.archigraph.model.InformationFlow;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * Layout class that uses a heuristic approach to lay out applications inside
 * a component. This class shall be used when components are large and have
 * many applications.
 * This optimizer uses the simulated annealing approach.
 */
@Slf4j
public class HeuristicComponentLayout extends OptimizingComponentLayout {

    private final Random random = new Random();

    public HeuristicComponentLayout(Component comp) {
        super(comp);
    }

    @Override
    public void layout() {
        log.debug("Starting heuristic layout for grid = {}/{} and {} apps",
                component.getAppHeight(), component.getAppWidth(), component.getApplications().size());
        var currentState = createInitialLayout(component.getAppHeight() * component.getAppWidth());
        int bestSoFar = layoutQuality(currentState, component.getLocalInformationFlows());
        double temp = 100.0f;
        while (temp > 0.01) {
            var candidate = permutateLayout(currentState);
            int candidateQuality = layoutQuality(candidate, component.getLocalInformationFlows());
            int diff = candidateQuality - bestSoFar;
            temp *= 0.99;
            double metropolis = Math.exp(-diff / temp);
            log.trace("Current Iteration: temp = {}, quality = {}, metropolis = {}",
                    temp, candidateQuality, metropolis);
            if (diff < 0 || random.nextDouble() < metropolis) {
                currentState = candidate;
                bestSoFar = candidateQuality;
            }
        }
        quality = bestSoFar;
        layout = createAppToPosMap(currentState);
        log.debug("Optimized quality = {}", quality);
    }

    private Application[] createInitialLayout(final int nbrPositions) {
        var result = new Application[nbrPositions];
        int i = 0;
        for (Application app : component.getApplications()) {
            result[i] = app;
            i++;
        }
        return result;
    }

    /**
     * Randomly swap two applications in the layout.
     * @param prevLayout The layout computed so far
     * @return a new layout with two applications swapped against each other.
     */
    private Application[] permutateLayout(Application[] prevLayout) {
        var result = Arrays.copyOf(prevLayout, prevLayout.length);
        int sourceIndex = random.nextInt(prevLayout.length);
        int targetIndex;
        do {
            targetIndex = random.nextInt(prevLayout.length);
        } while (sourceIndex == targetIndex);
        Application temp = result[targetIndex];
        result[targetIndex] = result[sourceIndex];
        result[sourceIndex] = temp;
        return result;
    }

    private int layoutQuality(Application[] appPositions, List<InformationFlow> flows) {
        Map<Application, Coordinate> appsToPos = createAppToPosMap(appPositions);
        return layoutQuality(appsToPos, flows).getQuality();
    }

    private Map<Application, Coordinate> createAppToPosMap(Application[] appPositions) {
        Map<Application, Coordinate> appsToPos = new HashMap<>();
        for (int i = 0; i < appPositions.length; i++) {
            if (appPositions[i] != null) {
                appsToPos.put(appPositions[i], Coordinate.fromIndex(component.getAppWidth(), i));
            }
        }
        return appsToPos;
    }

}
