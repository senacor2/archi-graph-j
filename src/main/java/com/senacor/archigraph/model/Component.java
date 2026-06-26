package com.senacor.archigraph.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
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
     * The coordinates are relative to the components' origin.
     */
    private Area appArea;

    /**
     * Whether the appArea has been overridden.
     */
    private boolean isAppAreaOverride;

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

    private final Set<InformationFlow> l1CompInformationFlows = new LinkedHashSet<>();

    private final Set<InformationFlow> crossL1CompInformationFlows = new LinkedHashSet<>();

    private final AppMatrix appMatrix;

    public Component(String name, int row, int col, int width, int height, int level) {
        this.name = name;
        this.compArea = new Area(row, col, width, height);
        this.appArea = new Area(1, 0, width, height - 1);
        this.isAppAreaOverride = false;
        this.level = level;
        this.appMatrix = new AppMatrix(height, width);
    }

    /**
     * @return this component and all transitively contained components as a stream.
     */
    Stream<Component> flattened() {
        return Stream.concat(
                Stream.of(this),
                components.stream().flatMap(Component::flattened));
    }

    /**
     * Link this component to the top-most component.
     * @param l1Comp The top-most component in the component hierarchy.
     */
    void wireL1Component(Component l1Comp) {
        l1Component = l1Comp;
        for (Component c : getComponents()) {
            c.wireL1Component(l1Comp);
        }
    }

    /**
     * Link this component to the directly enclosing component.
     * This method transitively wires all enclosed components to their respective parents by traversing the
     * component tree recursively.
     * This link is null when this component is the top-most (aka l1) component.
     * @param parent the directly enclosing component.
     */
    void wireParent(Component parent) {
        parentComponent = parent;
        for (var c : getComponents()) {
            c.wireParent(this);
        }
    }

    /**
     * Overwrites the default application drawing area. The default drawing area is the component area minus
     * the top row which is used for the heading.
     * @param area Area where the applications of this component are placed.
     */
    public void setAppArea(@NonNull Area area) {
        appArea = new Area(area.row() + 1, area.col(), area.width(), area.height());
        isAppAreaOverride = true;
    }

    /**
     * @return the row position of the component relative to the enclosing component.
     */
    public int getRow() {
        return compArea.row();
    }

    /**
     * @return the column position of the component relative to the enclosing component.
     */
    public int getCol() {
        return compArea.col();
    }

    /**
     * @return the height in rows of the component.
     */
    public int getHeight() {
        return compArea.height();
    }

    /**
     * @return the width in columns of the component.
     */
    public int getWidth() {
        return compArea.width();
    }

    /**
     * @return the number of columns of the application drawing area.
     */
    public int getAppWidth() {
        return appArea.width();
    }

    /**
     * @return the number of rows of the application drawing area.
     */
    public int getAppHeight() {
        return appArea.height();
    }

    /**
     * @return the number of rows the application drawing area is displaced relative to the component origin.
     */
    public int getAppRowOffset() { return appArea.row(); }

    /**
     * @return the number of columns the application drawing area is displaced relative to the component origin.
     */
    public int getAppColOffset() { return appArea.col(); }

    /**
     * Translate app coordinate to component coordinate shifting the coordinate by the app offsets.
     * @param appCoord a coordinate for the app area.
     * @return a coordinate for the component.
     */
    public Coordinate translateToComponent(Coordinate appCoord) {
        return new Coordinate(appCoord, getAppRowOffset(), getAppColOffset());
    }

    /**
     * Returns the absolute row position of a component summing up the row positions of all parents.
     * @return the row position relative to the sheet origin.
     */
    public int getAbsCompRow() {
        return compArea.row() + (parentComponent == null ? 0 : parentComponent.getAbsCompRow());
    }

    /**
     * Returns the absolute column position of a component summing up the column positions of all parents.
     * @return the column position relative to the sheet origin.
     */
    public int getAbsCompCol() {
        return compArea.col() + (parentComponent == null ? 0 : parentComponent.getAbsCompCol());
    }

    /**
     * Returns the coordinate of a given app.
     * The coordinate takes the app areas position into account.
     * @param app the given app.
     * @return The coordinate of the app.
     */
    public Coordinate getAppCoordinate(Application app) {
        return appMatrix.getAppCoordinate(app);
    }

    /**
     * Set the position of the top left corner of the component box.
     * This information is later needed to translate between coordinates and positions.
     * @param x horizontal pixel offset from the left edge of the drawing.
     * @param y vertical pixel offset from the top edge of the drawing.
     */
    public void setOrigin(int x, int y) {
        appMatrix.setOrigin(x, y);
    }

    /**
     * Scan all information flows and sort them into
     * <ul>
     *     <li>information flows local to this component,</li>
     *     <li>information flows with one end in this component and the other end in a different component,
     *     which is in the same l1 component,</li>
     *     <li>information flows with one end in this component and the other end in a different l1 component</li>
     *     <li>information flows not relevant for this component</li>
     * </ul>
     * @param allFlows All information flows in this model.
     */
    public void selectInformationFlows(Collection<InformationFlow> allFlows) {
        log.debug("Selecting information flows for {}", getName());
        for (var i : allFlows) {
            var srcIn = applications.contains(i.getSource());
            var dstIn = applications.contains(i.getDestination());
            if (i.getSource() == null) {
                log.trace("Skipping information flow {} because source app {} could not be found", i.getId(), i.getSourceId());
                continue;
            }
            if (i.getSource().getComponent() == null) {
                log.trace("Skipping information flow {} because {} has no component", i.getId(), i.getSource().getId());
                continue;
            }
            if (i.getDestination() == null) {
                log.trace("Skipping information flow {} because destination app {} could not be found", i.getId(),
                        i.getDestId());
                continue;
            }
            if (i.getDestination().getComponent() == null) {
                log.trace("Skipping information flow {} because {} has no component", i.getId(), i.getDestination().getId());
                continue;
            }
            var sameL1Comp = i.getSource().getComponent().getL1Component() == i.getDestination().getComponent().getL1Component();
            log.trace("{} is src in = {}, dst in = {} same l1 = {}", i.getId(), srcIn, dstIn, sameL1Comp);
            if (srcIn && dstIn) {
                addToLocalInformationFlows(i);
            } else if ((srcIn || dstIn) && sameL1Comp) {
                l1Component.addToL1InformationFlows(i);
            } else if (srcIn || dstIn) {
                l1Component.addToCrossL1InformationFlows(i);
            } else {
                log.trace("{} not in any flow of {}", i.getId(), name);
            }
        }
    }

    public void layout() {
        log.debug("Doing layout for {}", this);
        var layout = new ComponentLayout(this);
        layout.layout();
        layout.fillInto(appMatrix);
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

    void addToLocalInformationFlows(InformationFlow flow) {
        log.debug("add {} to local flows of {}", flow.getId(), name);
        localInformationFlows.add(flow);
    }

    void addToL1InformationFlows(InformationFlow flow) {
        log.debug("add {} to l1 local flows of {}", flow.getId(), name);
        l1CompInformationFlows.add(flow);
    }

    void addToCrossL1InformationFlows(InformationFlow flow) {
        log.debug("add {} to cross l1 flows of {}", flow.getId(), name);
        crossL1CompInformationFlows.add(flow);
    }

    public Application getApplicationAt(Coordinate coord) {
        return appMatrix.get(coord);
    }
}
