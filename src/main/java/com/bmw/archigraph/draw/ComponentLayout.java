package com.bmw.archigraph.draw;

import com.bmw.archigraph.model.Application;
import com.bmw.archigraph.model.Component;
import com.bmw.archigraph.model.InformationFlow;
import com.github.dakusui.combinatoradix.Combinator;
import com.github.dakusui.combinatoradix.Permutator;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

/**
 * Creates a layout of applications inside a component.
 * The algorithm takes information flows inside the component into account and minimizes
 * intersections of the information flow arcs.
 */
public class ComponentLayout {

    private final Component component;
    private int quality;
    private Map<Application, Coordinate> layout;

    public ComponentLayout(Component comp) {
        component = comp;
    }

    List<List<Coordinate>> appPositionsInComponent(final int rows, final int columns, final int appCount) {
        if (appCount > rows * columns)
            throw new IllegalArgumentException("Number of apps (%d) exceeds grid size (%d x %d)"
                    .formatted(appCount, rows, columns));
        var indexes = IntStream.range(0, rows * columns).boxed().toList();
        var result = new LinkedList<List<Coordinate>>();
        for (List<Integer> each : new Permutator<>(indexes, appCount)) {
            List<Coordinate> onePerm = each.stream()
                    .map(i -> Coordinate.fromIndex(columns, i))
                    .collect(Collectors.toList());
            result.add(onePerm);
        }
        return result;
    }

    /**
     * Check if the three points a, b and c are positioned CounterClockWise.
     * @link <a href="https://bryceboe.com/2006/10/23/line-segment-intersection-algorithm/">Line Segment Intersection Algorithm</a>
     * @param a First point
     * @param b Second point
     * @param c Third point
     * @return Trhe if the slobe of AB is less than the slope of AC.
     */
    boolean ccw(Coordinate a, Coordinate b, Coordinate c) {
        return (c.getRow() - a.getRow()) * (b.getCol() - a.getCol()) >
                (b.getRow() - a.getRow()) * (c.getCol() - a.getCol());
    }

    boolean linesIntersect(Coordinate fromApp1, Coordinate toApp1, Coordinate fromApp2, Coordinate toApp2) {
        return ccw(fromApp1, fromApp2, toApp2) != ccw(toApp1, fromApp2, toApp2) &&
                ccw(fromApp1, toApp1, fromApp2) != ccw(fromApp1, toApp1, toApp2);
    }

    RatedLayout layoutQuality(Map<Application, Coordinate> appPositions, List<InformationFlow> flows) {
        var combinations = new Combinator<>(flows, 2);
        var quality = StreamSupport.stream(combinations.spliterator(), false)
                .map(flowCombi -> List.of(
                        appPositions.get(flowCombi.getFirst().getSource()),
                        appPositions.get(flowCombi.getFirst().getDestination()),
                        appPositions.get(flowCombi.getLast().getSource()),
                        appPositions.get(flowCombi.getLast().getDestination())))
                .map(apps -> linesIntersect(apps.get(0), apps.get(1), apps.get(2), apps.get(3)) ? 1 : 0)
                .reduce(0, Integer::sum);
        return new RatedLayout(quality, appPositions);
    }

    Map<Application, Coordinate> zipmapAppsAndCoordinates(List<Application> apps, List<Coordinate> coords) {
        return IntStream.range(0, coords.size())
                .mapToObj(i -> new AppCoordinate(apps.get(i), coords.get(i)))
                .collect(Collectors.toMap(AppCoordinate::getApp, AppCoordinate::getCoord));
    }

    void layout(List<InformationFlow> flows) {
        if (component.getApplications().isEmpty()) {
            quality = 0;
            layout = new HashMap<>();
        } else if (flows.isEmpty()) {
            quality = 0;
            layout = new HashMap<>();
        } else {
            var best = appPositionsInComponent(component.getH(), component.getW(), component.getApplications().size()).stream()
                    .map(l -> zipmapAppsAndCoordinates(component.getApplications(), l))
                    .map(layout -> layoutQuality(layout, flows))
                    .max(RatedLayout::compareTo)
                    .orElseThrow();
            quality = best.quality;
            layout = best.layout;
        }
    }

    Coordinate getAppCoordinate(Application app) {
        return layout.get(app);
    }

    int getQuality() {
        return quality;
    }

    private record AppCoordinate(Application app, Coordinate coord) {
        Application getApp() { return app; }
        Coordinate getCoord() { return coord; }
    }

    private static class RatedLayout implements Comparable<RatedLayout> {

        private final int quality;
        private final Map<Application, Coordinate> layout;

        RatedLayout(int quality, Map<Application, Coordinate> layout) {
            this.quality = quality;
            this.layout = layout;
        }

        @Override
        public int compareTo(RatedLayout o) {
            return Integer.compare(quality, o.quality);
        }
    }

}
