package com.bmw.archigraph.draw;

import com.bmw.archigraph.model.Application;
import com.bmw.archigraph.model.Component;
import com.bmw.archigraph.model.InformationFlow;
import com.github.dakusui.combinatoradix.Combinator;
import com.github.dakusui.combinatoradix.Permutator;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

/**
 * Creates a layout of applications inside a component.
 * The algorithm takes information flows inside the component into account and minimizes
 * intersections of the information flow arcs.
 */
@Slf4j
public class ComponentLayout extends AbstractLayout {

    private int quality;

    public ComponentLayout(Component comp) {
        super(comp);
    }

    /**
     * Returns a list of all possible placements of apps inside a component. Apps are placed in a fixed grid.
     *
     * @param appCount Number of apps to be placed inside a component.
     * @return The inner list is one possible combination of app positions inside the grid. The outer list
     * collects all possible permutations.
     */
    List<List<Coordinate>> appPositionsInComponent(final int appCount) {
        final int rows = component.getAppHeight();
        final int columns = component.getAppWidth();
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
     *
     * @param a First point
     * @param b Second point
     * @param c Third point
     * @return Trhe if the slobe of AB is less than the slope of AC.
     * @link <a href="https://bryceboe.com/2006/10/23/line-segment-intersection-algorithm/">Line Segment Intersection Algorithm</a>
     */
    boolean ccw(Coordinate a, Coordinate b, Coordinate c) {
        return (c.row() - a.row()) * (b.col() - a.col()) >
                (b.row() - a.row()) * (c.col() - a.col());
    }

    /**
     * Check if the lines from the given apps would intersect.
     *
     * @param fromApp1 source of the first information flow.
     * @param toApp1   destination of the first information flow.
     * @param fromApp2 source of the second information flow.
     * @param toApp2   destination of the second information flow.
     * @return true, if the first and the second information flow cross each other. False if not. Also false, if
     * information flows share a common endpoint.
     */
    boolean linesIntersect(Coordinate fromApp1, Coordinate toApp1, Coordinate fromApp2, Coordinate toApp2) {
        return ccw(fromApp1, fromApp2, toApp2) != ccw(toApp1, fromApp2, toApp2) &&
                ccw(fromApp1, toApp1, fromApp2) != ccw(fromApp1, toApp1, toApp2);
    }

    /**
     * Given a possible layout of applications and the information flows, determine the quality of the layout.
     *
     * @param appPositions A possible placement of apps in the component grid.
     * @param flows        information flows between the apps.
     * @return The layout used with the computed layout quality.
     */
    RatedLayout layoutQuality(Map<Application, Coordinate> appPositions, List<InformationFlow> flows) {
        log.trace("Calculating layout quality of {}", appPositions);
        // TODO should also consider the length of the information flow lines, favoring shorter non-intersecting ones.
        var combinations = new Combinator<>(flows, 2);
        var quality = StreamSupport.stream(combinations.spliterator(), false)
                .map(flowCombi -> List.of(
                        appPositions.get(flowCombi.getFirst().getSource()),
                        appPositions.get(flowCombi.getFirst().getDestination()),
                        appPositions.get(flowCombi.getLast().getSource()),
                        appPositions.get(flowCombi.getLast().getDestination())))
                .map(apps -> linesIntersect(apps.get(0), apps.get(1), apps.get(2), apps.get(3)) ? 1 : 0)
                .reduce(0, Integer::sum);
        log.trace("Layout quality is {}", quality);
        return new RatedLayout(quality, appPositions);
    }

    /**
     * Join (zip) the lists and components side by side into a map keyed by the apps with the coords as value.
     * Both lists must have the same length.
     *
     * @param apps   Apps will be used as keys.
     * @param coords Coords will be used as values.
     * @return A map where each app in <code>apps</code> is associated with the coord at the same position in
     * <code>coords</code>.
     */
    Map<Application, Coordinate> zipmapAppsAndCoordinates(List<Application> apps, List<Coordinate> coords) {
        assert apps.size() == coords.size();
        return IntStream.range(0, coords.size())
                .mapToObj(i -> new AppCoordinate(apps.get(i), coords.get(i)))
                .collect(Collectors.toMap(AppCoordinate::app, AppCoordinate::coord));
    }

    /**
     * Create a default layout for the apps inside the component.
     *
     * @param apps List of apps.
     * @return the default layout where the component is filled with apps from the top left, line by line.
     */
    Map<Application, Coordinate> defaultLayout(List<Application> apps) {
        var coords = IntStream.range(0, apps.size())
                .mapToObj(i -> Coordinate.fromIndex(component.getAppWidth(), i))
                .toList();
        return zipmapAppsAndCoordinates(apps, coords);
    }

    /**
     * Create the application layout inside the component grid, taking information flows into account.
     * After this operation, the layout quality and the application positions are initialized and can be retrieved.
     */
    public void layout() {
        var flows = component.getLocalInformationFlows();
        if (component.getApplications().isEmpty()) {
            quality = 0;
            layout = new HashMap<>();
        } else if (flows.isEmpty()) {
            quality = 0;
            layout = defaultLayout(component.getApplications());
        } else {
            var best = appPositionsInComponent(component.getApplications().size()).stream()
                    .map(l -> zipmapAppsAndCoordinates(component.getApplications(), l))
                    .map(layout -> layoutQuality(layout, flows))
                    .min(RatedLayout::compareTo)
                    .orElseThrow();
            quality = best.quality;
            layout = best.layout;
        }
    }

    int getQuality() {
        return quality;
    }

    static class RatedLayout implements Comparable<RatedLayout> {

        @Getter
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
