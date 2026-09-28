package com.senacor.archigraph.layout;

import com.github.dakusui.combinatoradix.Combinator;
import com.senacor.archigraph.model.Application;
import com.senacor.archigraph.model.Component;
import com.senacor.archigraph.model.Coordinate;
import com.senacor.archigraph.model.InformationFlow;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

/**
 * Layout class that uses an optimizing approach to lay out the applications inside
 * a component.
 */
@Slf4j
public abstract class OptimizingComponentLayout extends AbstractComponentLayout {

    protected int quality;

    protected OptimizingComponentLayout(Component comp) {
        super(comp);
    }

    @Override
    abstract public void layout();

    @Override
    public int getQuality() {
        return quality;
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
    protected RatedLayout layoutQuality(Map<Application, Coordinate> appPositions, List<InformationFlow> flows) {
        log.trace("Calculating layout quality of {}", appPositions);
        // TODO should also consider the length of the information flow lines, favoring shorter non-intersecting ones.
        var combinations = new Combinator<>(flows, 2);
        var quality = StreamSupport.stream(combinations.spliterator(), false)
                .map(flowCombi -> List.of(
                        appPositions.get(flowCombi.getFirst().getSource()),
                        appPositions.get(flowCombi.getFirst().getDestination()),
                        appPositions.get(flowCombi.getLast().getSource()),
                        appPositions.get(flowCombi.getLast().getDestination())))
                .map(apps -> linesIntersect(apps.getFirst(), apps.get(1), apps.get(2), apps.get(3)) ? 1 : 0)
                .reduce(0, Integer::sum);
        log.trace("Layout quality is {}", quality);
        return new RatedLayout(quality, appPositions);
    }

    protected static class RatedLayout implements Comparable<RatedLayout> {

        @Getter
        private final int quality;
        @Getter
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
