package com.senacor.archigraph.layout;

import com.senacor.archigraph.model.AppMatrix;
import com.senacor.archigraph.model.Application;
import com.senacor.archigraph.model.Component;
import com.senacor.archigraph.model.Coordinate;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * Base class for layouts that map applications to coordinates.
 * A layout always uses a coordinate system with 0,0 being the top left. It is the component's task
 * to project it relative to its own coordinates.
 */
@Slf4j
public abstract class AbstractComponentLayout {

    protected final Component component;

    protected Map<Application, Coordinate> layout;

    protected AbstractComponentLayout(Component comp) {
        component = comp;
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
     * Create a readable layout of the applications inside the component.
     */
    public abstract void layout();

    /**
     * Returns the row column position of an app as defined by the layout.
     *
     * @param app An Application.
     * @return The coordinate of the app. Will return <code>null</code> if the layout does not contain the app.
     */
    public Coordinate getAppCoordinate(Application app) {
        return layout.get(app);
    }

    public abstract int getQuality();

    public void fillInto(AppMatrix appMatrix) {
        layout.forEach((app, coord) -> appMatrix.put(component.translateToComponent(coord), app));
    }

    public record AppCoordinate(Application app, Coordinate coord) {
    }
}
