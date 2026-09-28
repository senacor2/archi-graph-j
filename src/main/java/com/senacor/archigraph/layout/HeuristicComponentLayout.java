package com.senacor.archigraph.layout;

import com.senacor.archigraph.model.Application;
import com.senacor.archigraph.model.Component;
import com.senacor.archigraph.model.Coordinate;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * Layout class that uses a heuristic approach to lay out applications inside
 * a component. This class shall be used when components are large and have
 * many applications.
 * This optimizer uses the simulated annealing approach.
 */
@Slf4j
public class HeuristicComponentLayout extends OptimizingComponentLayout {

    private final float initialTemperature;

    private static final int MAX_ITERATIONS = 1000;

    private final Random random = new Random();

    public HeuristicComponentLayout(Component comp, float initialTemperature) {
        super(comp);
        this.initialTemperature = initialTemperature;
    }

    public HeuristicComponentLayout(Component comp) {
        this(comp, 10.0f);
    }

    @Override
    public void layout() {
        OptimizingComponentLayout.RatedLayout bestSoFar;
        createInitialLayout();
        bestSoFar = layoutQuality(layout, component.getLocalInformationFlows());
        for (int i = 0; i < MAX_ITERATIONS; i++) {
            var candidate = permutateLayout(layout);
            var candidateRating = layoutQuality(candidate, component.getLocalInformationFlows());
            var diff = candidateRating.getQuality() - bestSoFar.getQuality();
            var currTemp = initialTemperature / (float)(i+1);
            var metropolis = Math.exp(-diff / currTemp);
            log.trace("Current iteration: temp = {}, quality = {}, metropolis = {}",
                    currTemp, candidateRating.getQuality(), metropolis);
            if (diff < 0 || random.nextFloat() < metropolis) {
                bestSoFar = candidateRating;
            }
        }
        quality = bestSoFar.getQuality();
        layout = bestSoFar.getLayout();
        log.debug("Optimized quality = {}", quality);
    }

    private void createInitialLayout() {
        var coords = IntStream.range(0, component.getApplications().size())
                .mapToObj(i -> Coordinate.fromIndex(component.getAppWidth(), i))
                .toList();
        layout = zipmapAppsAndCoordinates(component.getApplications(), coords);
    }

    /**
     * Randomly swap two applications in the layout.
     * @param prevLayout The layout computed so far
     * @return a new layout with two applications swapped against each other.
     */
    private Map<Application, Coordinate> permutateLayout(Map<Application, Coordinate> prevLayout) {
        var result = new HashMap<>(prevLayout);
        int swap1 = random.nextInt(prevLayout.size());
        int swap2;
        do {
            swap2 = random.nextInt(prevLayout.size());
        } while (swap1 == swap2);
        var app1 = result.keySet().stream()
                .skip(swap1)
                .findFirst()
                .orElseThrow();
        var app2 = result.keySet().stream()
                .skip(swap2)
                .findFirst()
                .orElseThrow();
        var coord1 = result.get(app1);
        var coord2 = result.get(app2);
        result.put(app1, coord2);
        result.put(app2, coord1);
        return result;
    }

}
