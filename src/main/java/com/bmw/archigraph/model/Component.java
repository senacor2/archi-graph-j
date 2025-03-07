package com.bmw.archigraph.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Getter
public class Component {

    private final String name;

    private final int x;

    private final int y;

    private final int w;

    private final int h;

    private final int level;

    private Component l1Component;

    private final List<Application> applications = new LinkedList<>();

    @Setter
    private List<Component> components = new LinkedList<>();

    private final List<InformationFlow> internalInformationFlows = new LinkedList<>();

    private final List<InformationFlow> crossCompInformationFlows = new LinkedList<>();

    public Component(String name, int x, int y, int w, int h, int level) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.level = level;
    }

    Stream<Component> flattened() {
        return Stream.concat(
                Stream.of(this),
                components.stream().flatMap(Component::flattened));
    }

    void wireL1Component(Component l1Comp) {
        l1Component = l1Comp;
        for (Component c : getComponents()) {
            c.wireL1Component(l1Comp);
        }
    }

    public void selectInformationFlows(Collection<InformationFlow> allFlows) {
        log.debug("Selecting information flows for {}", getName());
        for (var i : allFlows) {
            var srcIn = applications.contains(i.getSource());
            var dstIn = applications.contains(i.getDestination());
            log.debug("{} is {} {}", i.getId(), srcIn, dstIn);
            if (srcIn && dstIn) {
                internalInformationFlows.add(i);
            } else if (srcIn || dstIn) {
                crossCompInformationFlows.add(i);
            }
        }
    }

    public String toString() {
        return String.format("Component %s %d,%d %d,%d level %d l1 %s", name, x, y, w, h, level,
                l1Component == null ? "null" : l1Component.getName());
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null) return false;
        if (other.getClass() != getClass()) return false;
        return name.equals(((Component) other).name);
    }

    public void addApplication(Application a) {
        applications.add(a);
    }
}
