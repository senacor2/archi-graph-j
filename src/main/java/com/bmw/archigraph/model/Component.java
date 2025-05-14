package com.bmw.archigraph.model;

import com.bmw.archigraph.draw.Area;
import com.bmw.archigraph.draw.ComponentLayout;
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

    /**
     * The cell coordinates and size of the entire component.
     */
    private final Area compArea;

    /**
     * The cell coordinates and size of the area where components are drawn inside the app.
     */
    private Area appArea;

    /**
     * Component nesting level. The top level components have level == 1.
     */
    private final int level;

    private Component l1Component;

    private Component parentComponent;

    private final List<Application> applications = new LinkedList<>();

    @Setter
    private List<Component> components = new LinkedList<>();

    private final List<InformationFlow> localInformationFlows = new LinkedList<>();

    private final List<InformationFlow> l1CompInformationFlows = new LinkedList<>();

    private final List<InformationFlow> crossL1CompInformationFlows = new LinkedList<>();

    @Setter
    private ComponentLayout layout;

    public Component(String name, int row, int col, int w, int h, int level) {
        this.name = name;
        this.compArea = new Area(row, col, w, h);
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

    void wireParent(Component parent) {
        parentComponent = parent;
        for (var c : getComponents()) {
            c.wireParent(this);
        }
    }

    public int getRow() {
        return compArea.row();
    }

    public int getCol() {
        return compArea.col();
    }

    public int getHeight() {
        return compArea.height();
    }

    public int getWidth() {
        return compArea.width();
    }

    public int getAbsoluteAppRow() {
        if (appArea == null) {
            return getAbsRow();
        } else {
            return appArea.row() + (parentComponent == null ? 0 : parentComponent.getAbsRow());
        }
    }

    public int getAbsoluteAppCol() {
        if (appArea == null) {
            return getAbsCol();
        } else {
            return appArea.col() + (parentComponent == null ? 0 : parentComponent.getAbsCol());
        }
    }

    public int getAppWidth() {
        if (appArea == null) {
            return getWidth();
        } else {
            return appArea.width();
        }
    }

    public int getAppHeight() {
        if (appArea == null) {
            return getHeight();
        } else {
            return appArea.height();
        }
    }

    /**
     * Returns the absolute row position of a component summing up the row positions
     * of all parents.
     * @return the row position relative to the sheet origin.
     */
    public int getAbsRow() {
        return compArea.row() + (parentComponent == null ? 0 : parentComponent.getAbsRow());
    }

    /**
     * Returns the absolute column position of a component summing up the column
     * positions of all parents.
     * @return the column position relative to the sheet origin.
     */
    public int getAbsCol() {
        return compArea.col() + (parentComponent == null ? 0 : parentComponent.getAbsCol());
    }

    public void selectInformationFlows(Collection<InformationFlow> allFlows) {
        log.debug("Selecting information flows for {}", getName());
        for (var i : allFlows) {
            var srcIn = applications.contains(i.getSource());
            var dstIn = applications.contains(i.getDestination());
            var sameL1Comp = i.getSource().getComponent().getL1Component() == i.getDestination().getComponent().getL1Component();
            log.debug("{} is src in = {}, dst in = {} same l1 = {}", i.getId(), srcIn, dstIn, sameL1Comp);
            if (srcIn && dstIn) {
                log.debug("add {} to local flows if {}", i.getId(), name);
                localInformationFlows.add(i);
            } else if ((srcIn || dstIn) && sameL1Comp) {
                log.debug("add {} to l1 local flows of {}", i.getId(), name);
                l1CompInformationFlows.add(i);
            } else if (srcIn || dstIn) {
                log.debug("add {} to cross l1 flows of {}", i.getId(), name);
                crossL1CompInformationFlows.add(i);
            } else {
                log.debug("{} not in any flow of {}", i.getId(), name);
            }
        }
    }

    public String toString() {
        return String.format("Component %s %d,%d %d,%d level %d l1 %s", name, compArea.col(), compArea.row(),
                compArea.width(), compArea.height(), level,
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
