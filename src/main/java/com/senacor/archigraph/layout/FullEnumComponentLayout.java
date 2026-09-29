package com.senacor.archigraph.layout;

import com.github.dakusui.combinatoradix.Permutator;
import com.senacor.archigraph.model.Component;
import com.senacor.archigraph.model.Coordinate;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Creates a layout of applications inside a component.
 * The algorithm takes information flows inside the component into account and minimizes
 * intersections of the information flow arcs.
 */
@Slf4j
public class FullEnumComponentLayout extends OptimizingComponentLayout {

    public FullEnumComponentLayout(Component comp) {
        super(comp);
    }

    /**
     * Computes all possible permutations of app positions inside the component and returns
     * them as a stream of position list where each list is one possible placement.
     *
     * @param appCount Number of apps in this component.
     * @return A stream of List with app coordinates.
     */
    Stream<List<Coordinate>> appPositionsInComponent(final int appCount) {
        final int rows = component.getAppHeight();
        final int columns = component.getAppWidth();
        var indexes = IntStream.range(0, rows * columns).boxed().toList();
        return StreamSupport.stream(new LongPermutationSpliterator<>(new Permutator<>(indexes, appCount)), true)
                .map(onePerm -> onePerm.stream()
                        .map(i -> Coordinate.fromIndex(columns, i))
                        .toList());
    }

    /**
     * Create the application layout inside the component grid, taking information flows into account.
     * After this operation, the layout quality and the application positions are initialized and can be retrieved.
     */
    @Override
    public void layout() {
        log.debug("Starting full enumeration layout for grid {}/{} and {} apps",
                component.getAppHeight(), component.getAppWidth(), component.getApplications().size());
        var flows = component.getLocalInformationFlows();
        // First run - terminate early if there is an intersection-free solution
        Optional<RatedLayout> firstBest = appPositionsInComponent(component.getApplications().size())
                .unordered()
                .map(l -> zipmapAppsAndCoordinates(component.getApplications(), l))
                .map(layout -> layoutQuality(layout, flows))
                .filter(o -> o.getQuality() == 0)
                .findAny();

        // If there is no best solution, find the number of minimal intersections. Currently, there seems
        // to be no better way to achieve this with parallel streams.
        var best = firstBest.orElseGet(() -> appPositionsInComponent(component.getApplications().size())
                .map(l -> zipmapAppsAndCoordinates(component.getApplications(), l))
                .map(layout -> layoutQuality(layout, flows))
                .min(RatedLayout::compareTo)
                .orElseThrow());
        quality = best.getQuality();
        layout = best.getLayout();
    }

    static class LongPermutationSpliterator<E> implements Spliterator<List<E>> {

        private final Permutator<E> permutator;
        private long current;
        private final long end;

        public LongPermutationSpliterator(Permutator<E> permutator) {
            this(permutator, 0L, permutator.size());
        }

        public LongPermutationSpliterator(Permutator<E> permutator, long start, long end) {
            this.permutator = permutator;
            this.current = start;
            this.end = end;
        }

        @Override
        public boolean tryAdvance(Consumer<? super List<E>> action) {
            if (current < end) {
                action.accept(permutator.get(current));
                current++;
                return true;
            } else {
                return false;
            }
        }

        @Override
        public Spliterator<List<E>> trySplit() {
            long remaining = end - current;

            // do not split if splits are too small
            if (remaining < 10_000L) {
                return null;
            }
            long mid = current + (remaining / 2);

            // create a new spliterator for the first half of the current spliterator
            Spliterator<List<E>> prefix = new LongPermutationSpliterator<>(permutator, current, mid);

            // move the current index of this spliterator behind the new spliterator
            this.current = mid;

            return prefix;
        }

        @Override
        public long estimateSize() {
            return end - current;
        }

        @Override
        public int characteristics() {
            return IMMUTABLE | NONNULL | ORDERED | SIZED | SUBSIZED;
        }
    }

}
