package com.bmw.archigraph.model;

import lombok.Data;

import java.util.List;
import java.util.stream.Stream;

@Data
public class Component {

    private String name;

    private int x;

    private int y;

    private int w;

    private int h;

    private int level;

    private Component l1Component;

    private List<Application> applications = List.of();

    private List<Component> components = List.of();

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
        setL1Component(l1Comp);
        for (Component c : getComponents()) {
            c.wireL1Component(l1Comp);
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
        return name.equals(((Component)other).name);
    }

}
