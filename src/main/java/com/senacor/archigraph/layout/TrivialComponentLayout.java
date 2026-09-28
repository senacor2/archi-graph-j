package com.senacor.archigraph.layout;

import com.senacor.archigraph.model.Application;
import com.senacor.archigraph.model.Component;
import com.senacor.archigraph.model.Coordinate;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * This layout can be used if the applications do not have information flows.
 */
public class TrivialComponentLayout extends AbstractComponentLayout {

    public TrivialComponentLayout(Component comp) {
        super(comp);
    }

    @Override
    public void layout() {
        layout = defaultLayout(component.getApplications());
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

    public int getQuality() {
        return 0;
    }
}
