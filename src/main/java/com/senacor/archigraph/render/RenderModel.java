package com.senacor.archigraph.render;

import com.senacor.archigraph.model.*;
import com.senacor.archigraph.model.Component;
import com.senacor.archigraph.rules.RuleBase;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.util.*;
import java.util.List;

@Data
@Slf4j
public class RenderModel {

    private static final Color COLOR_LINE = Color.BLACK;

    /**
     * Grid row height.
     */
    static final int ROW_HEIGHT = 200;

    /**
     * Grid column width.
     */
    static final int COL_WIDTH = (int) (ROW_HEIGHT * 1.6);
    private static final int ROW_HEIGHT_HALF = ROW_HEIGHT / 2;
    private static final int COL_WIDTH_HALF = COL_WIDTH / 2;
    /**
     * Each component is spaced by this amount w.r.t. the enclosing component.
     */
    private static final int COMP_SPACING = 10;

    /**
     * The spacing of a component w.r.t. the cell. Note that the distance to the component border
     * is different because components may be nested.
     */
    static final int SPACING = 40;

    /**
     * A half spacing is used to place information flow lines.
     */
    private static final int SPACING_HALF = SPACING / 2;

    /**
     * Width of an app.
     */
    static final int APP_WIDTH = COL_WIDTH - SPACING * 2;
    public static final int APP_WIDTH_HALF = APP_WIDTH / 2;

    /**
     * Height of an app.
     */
    static final int APP_HEIGHT = ROW_HEIGHT - SPACING * 2;
    public static final int APP_HEIGHT_HALF = APP_HEIGHT / 2;

    /**
     * Contains the rules to format an application, e.g. colors, fonts etc.
     */
    private AppFormatter appFormatter = new AppFormatter();

    /**
     * Contains rules to format a component.
     */
    private ComponentFormatter compFormatter = new ComponentFormatter();

    /**
     * Contains rules to format the model.
     */
    private ModelFormatter modelFormatter = new ModelFormatter();

    enum Side {
        TOP, BOTTOM, LEFT, RIGHT
    }

    private List<RenderModelElement> elements = new LinkedList<>();
    private Map<String, RenderModelElement> elementsById = new HashMap<>();

    /**
     * Adds a new element to the render model. This method is called during model rendering.
     *
     * @param element the new render model element.
     */
    void add(RenderModelElement element) {
        log.debug("Add render model element {}", element);
        elements.add(element);
        elementsById.put(element.getId(), element);
    }

    /**
     * Set the rule bases required to format the applications.
     * @param ruleBase a loaded rule base.
     */
    public void setRuleBase(RuleBase ruleBase) {
        appFormatter.setRuleBase(ruleBase);
    }
    /**
     * Populates the RenderModel with logical drawing components like rectangles and lines.
     *
     * @param model An architecture model to be rendered
     * @return the populated render model.
     */
    public RenderModel render(Model model) {
        Rectangle modelRect = Rectangle.builder()
                .id("ROOT")
                .text(model.getName())
                .x(0)
                .y(0)
                .w(model.getL1Components().stream()
                        .map(c -> c.getCol() + c.getWidth())
                        .max(Integer::compareTo)
                        .orElse(0) * COL_WIDTH + COL_WIDTH)
                .h(ROW_HEIGHT)
                .build();
        modelFormatter.formatHead(model, modelRect);
        add(modelRect);
        for (var c : model.getL1Components()) {
            render(c);
        }
        renderLegend(model);
        return this;
    }

    /**
     * Render a component consisting of a heading and the body where the apps are drawn.
     *
     * @param comp A component
     */
    void render(Component comp) {
        int x = comp.getAbsCompCol() * COL_WIDTH + indent(comp);
        int y = comp.getAbsCompRow() * ROW_HEIGHT + indent(comp) + (comp.getLevel() - 1) * ROW_HEIGHT;
        int w = comp.getWidth() * COL_WIDTH - (comp.getLevel() - 1) * COMP_SPACING * 2;
        log.debug("Render comp {} orig {}/{}", comp, x, y);

        // Heading rectangle
        var compHeadRect = Rectangle.builder()
                .id(comp.getName().replace(" ", "_") + "_head")
                .text(comp.getName())
                .x(x)
                .y(y)
                .w(w)
                .h(ROW_HEIGHT)
                .build();
        compFormatter.formatHead(comp, compHeadRect);
        add(compHeadRect);
        y += ROW_HEIGHT;
        // Body rectangle
        var compBodyRect = Rectangle.builder()
                .id(comp.getName().replace(" ", "_") + "_body")
                .x(x)
                .y(y)
                .w(w)
                .h((comp.getHeight() - 1) * ROW_HEIGHT - indent(comp) * 2)
                .build();
        compFormatter.formatBody(comp, compBodyRect);
        add(compBodyRect);
        renderApplications(comp);
        renderLocalFlows(comp);
        for (var c : comp.getComponents()) {
            render(c);
        }
        if (comp.getParentComponent() == null) { // L1 components have no parent
            renderL1CompFlows(comp);
            renderCrossL1CompFlows(comp, x, y);
        } else {
            comp.getParentComponent().getAppMatrix().merge(comp.getAppMatrix(), comp.getRow() + 1, comp.getCol());
            log.debug("After merge {} into {}: \n{}", comp.getName(), comp.getParentComponent().getName(),
                    comp.getParentComponent().getAppMatrix().dump());

        }
    }

    private static int indent(Component comp) {
        return (comp.getLevel() - 1) * COMP_SPACING;
    }

    /**
     * Render the applications inside a component.
     *
     * @param comp The enclosing component.
     */
    void renderApplications(Component comp) {
        comp.layout();
        for (Coordinate coord : comp.getAppMatrix().usedCoordinates()) {
            var app = comp.getApplicationAt(coord);
            render(app, comp.getLevel(), comp.getAbsCompRow(), comp.getAbsCompCol(), coord);
        }
    }

    /**
     * Render an application into the render model.
     *
     * @param app        An Application object.
     * @param level      The nesting level of the enclosing component. Used to calculate the offset of the component
     *                   headings.
     * @param absCompRow Absolute row position of the component.
     * @param absCompCol Absolute column position of the component.
     * @param appCoord   Row/Column coordinates of the application inside the component.
     */
    void render(Application app, int level, int absCompRow, int absCompCol, Coordinate appCoord) {
        log.debug("Render app {} absCompRow {} absCompCol {} coord {}", app.getId(), absCompRow, absCompCol, appCoord);
        var rect = Rectangle.builder()
                .id(app.getId())
                .x(absCompCol * COL_WIDTH + appCoord.col() * COL_WIDTH + SPACING)
                .y(absCompRow * ROW_HEIGHT + (level - 1) * ROW_HEIGHT + appCoord.row() * ROW_HEIGHT + SPACING)
                .w(APP_WIDTH)
                .h(APP_HEIGHT)
                .build();
        appFormatter.format(app, rect);
        add(rect);
    }

    /**
     * Draw the internal flow lines inside one component.
     *
     * @param comp  The enclosing component
     */
    void renderLocalFlows(Component comp) {
        var count = 0;
        log.debug("Render local flows for {}", comp.getName());
        for (var flow : comp.getLocalInformationFlows()) {
            render(flow, false, comp);
            count++;
        }
        log.debug("Render local flows for {}: {} flows rendered", comp.getName(), count);
    }

    /**
     * Draw the flows inside one L1 component.
     *
     * @param comp  the L1 component.
     */
    void renderL1CompFlows(Component comp) {
        var count = 0;
        log.debug("Render l1 flows for {}", comp.getName());
        for (var flow : comp.getL1CompInformationFlows()) {
            render(flow, false, comp);
            count++;
        }
        log.debug("Render l1 flows for {}: {} flows rendered", comp.getName(), count);
    }

    /**
     * Render the information flows across L1 component boundaries.
     * Apps outside the component are represented by proxies which are placed around the L1 component.
     * Proxies are created in this function.
     *
     * @param comp  The L1 component.
     * @param origX The x position of the components left edge.
     * @param origY The y position of the components top edge.
     */
    void renderCrossL1CompFlows(Component comp, int origX, int origY) {
        var count = 0;
        log.debug("Render cross l1 flows for {}", comp.getName());
        int proxyOrigX = origX - COL_WIDTH;
        int proxyOrigY = origY - ROW_HEIGHT * 2; // take header row offset into account
        createAndPlaceProxies(comp, proxyOrigX, proxyOrigY);
        for (var flow : comp.getCrossL1CompInformationFlows()) {
            render(flow, true, comp);
            count++;
        }
        log.debug("Render cross l1 flows for {}: {} flows rendered", comp.getName(), count);
    }

    /**
     * Render an information flow.
     * Adds a line object to the model connecting the source and destination app.
     * The two apps may be in the same component, in the same L1 component or in different L1 components.
     *
     * @param flow  An InformationFlow
     * @param proxy True when proxies shall be rendered, false otherwise.
     * @param comp  The enclosing component or the L1 component containing one of the apps.
     */
    void render(InformationFlow flow, boolean proxy, Component comp) {
        log.debug("Render flow {} proxy = {}", flow.getId(), proxy);
        Component compL1 = comp.getParentComponent() == null ? comp : comp.getL1Component();
        Rectangle sourceRect;
        Rectangle destRect;
        if (flow.getSource().getComponent().getL1Component() == compL1) {
            sourceRect = (Rectangle) elementsById.get(flow.getSourceId());
        } else {
            sourceRect = findProxyRectangle(comp.getName(), flow.getSourceId());
        }
        if (flow.getDestination().getComponent().getL1Component() == compL1) {
            destRect = (Rectangle) elementsById.get(flow.getDestId());
        } else {
            destRect = findProxyRectangle(comp.getName(), flow.getDestId());
        }
        log.debug("Render flow from {} to {}", sourceRect, destRect);
        Point[] anchors = getAnchors(proxy ? comp.getL1AppMatrix() : comp.getAppMatrix(),
                flow.getSource(), flow.getDestination(), sourceRect, destRect);

        add(Line.builder()
                .id(flow.getId())
                .text(flow.getBusinessObject())
                .start(sourceRect)
                .end(destRect)
                .anchors(anchors)
                .build());
    }

    private Rectangle findProxyRectangle(String compName, String appId) {
        String proxyId = getProxyAppId(compName, appId);
        log.debug("Find proxy rectangle for {}", proxyId);
        if (elementsById.containsKey(proxyId)) {
            return (Rectangle) elementsById.get(proxyId);
        } else {
            log.error("Proxy app {} not found", proxyId);
            throw new IllegalArgumentException("Proxy app not found: " + proxyId);
        }
    }

    private String getProxyAppId(String compName, String appId) {
        return compName + "-proxy-" + appId;
    }

    /**
     * Create application proxies for all apps linked to information flows into or out of the component that
     * are outside the component.
     *
     * @param comp  Enclosing L1 component.
     * @param origX Left edge x position of the proxy box.
     * @param origY Top edge y position of the proxy box.
     */
    private void createAndPlaceProxies(Component comp, int origX, int origY) {
        var proxyBoxLayout = new ProxyBoxLayout(comp);
        comp.getL1AppMatrix().merge(comp.getAppMatrix(), 1, 1);
        for (var flow : comp.getCrossL1CompInformationFlows()) {
            var proxyApp = flow.getSource().getComponent().getL1Component() == comp ? flow.getDestination() : flow.getSource();
            var innerApp = flow.getSource().getComponent().getL1Component() == comp ? flow.getSource() : flow.getDestination();
            if (proxyBoxLayout.getAppCoordinate(proxyApp) == null) {
                var proxyAppCoord = proxyBoxLayout.findNearestEmptyCell(comp.getL1AppMatrix().getAppCoordinate(innerApp));
                proxyBoxLayout.setProxyPosition(proxyApp, proxyAppCoord);
                add(renderApplicationProxy(proxyApp, comp, origX, origY, proxyAppCoord));
            }
        }
        proxyBoxLayout.stream()
                .forEach(e -> comp.getL1AppMatrix().put(e.getValue(), e.getKey()));
    }

    private Rectangle renderApplicationProxy(Application app, Component parent, int origX, int origY,
                                             Coordinate proxyAppCoord) {
        log.debug("Render proxy for {} base pos x={} y={} coord = {}", app.getId(), origX, origY, proxyAppCoord);
        var rect = Rectangle.builder()
                .id(getProxyAppId(parent.getName(), app.getId()))
                .x(origX + proxyAppCoord.col() * COL_WIDTH + SPACING)
                .y(origY + proxyAppCoord.row() * ROW_HEIGHT + SPACING)
                .w(APP_WIDTH)
                .h(APP_HEIGHT)
                .build();
        appFormatter.formatProxy(app, rect);
        return rect;
    }

    /**
     * Returns a vector of turning points for the line drawn between two applications.
     * The algorithm considers space blocked by other apps and routes lines around them.
     *
     * @param appMatrix The component layout that contains the coordinates of the apps and all other apps.
     * @param source    Application where the information flow starts.
     * @param dest      Application where the information flow ends.
     * @return A vector of points where the information flow line shall be bent. The vector will be empty
     * when the Apps are directly adjacent or on the same for or column with no apps inbetween.
     */
    Point[] getAnchors(AppMatrix appMatrix, Application source, Application dest,
                       Rectangle sourceRect, Rectangle destRect) {
        var srcCoord = appMatrix.getAppCoordinate(source);
        var dstCoord = appMatrix.getAppCoordinate(dest);
        int distanceHor = Math.abs(srcCoord.col() - dstCoord.col());
        int distanceVrt = Math.abs(srcCoord.row() - dstCoord.row());
        Point[] result;

        log.debug("Get line anchors from {} to {}", srcCoord, dstCoord);
        if ((distanceHor == 0 && distanceVrt == 1) || (distanceVrt == 0 && distanceHor == 1)) {
            log.debug("Directly adjacent");
            result = new Point[0];
        } else if (distanceVrt == 0 && distanceHor > 1 && appMatrix.allCellsEmptyHor(srcCoord, dstCoord)) {
            log.debug("Same row and space between is empty");
            result = new Point[0];
        } else if (distanceHor == 0 && distanceVrt > 1 && appMatrix.allCellsEmptyVert(srcCoord, dstCoord)) {
            log.debug("Same column and space between is empty");
            result = new Point[0];
        } else if (distanceVrt == 0 && distanceHor > 1) {
            log.debug("Same row and apps between");
            // Start line at the top
            var x1 = sourceRect.getX() + APP_WIDTH_HALF;
            var x2 = destRect.getX() + APP_WIDTH_HALF;
            var y = sourceRect.getY() - SPACING_HALF;
            result = new Point[]{new Point(x1, y), new Point(x2, y)};
        } else if (distanceHor == 0 && distanceVrt > 1) {
            log.debug("Same col and apps between");
            // Start line at the right
            var x = sourceRect.getX() + APP_WIDTH + SPACING_HALF;
            var y1 = sourceRect.getY() + APP_HEIGHT_HALF;
            var y2 = destRect.getY() + APP_HEIGHT_HALF;
            result = new Point[]{new Point(x, y1), new Point(x, y2)};
        } else {
            log.debug("Different rows and columns");
            var topToBottom = srcCoord.row() < dstCoord.row();
            var leftToRight = srcCoord.col() < dstCoord.col();
            var startPoint = coordOnApp(sourceRect, sideFrom(topToBottom));
            var endPoint = coordOnApp(destRect, sideTo(leftToRight));
            var horSpacing = leftToRight ? -SPACING_HALF : SPACING_HALF;
            var vrtSpacing = topToBottom ? SPACING_HALF : -SPACING_HALF;
            result = new Point[]{
                    new Point(startPoint.x, startPoint.y + vrtSpacing),
                    new Point(endPoint.x + horSpacing, startPoint.y + vrtSpacing),
                    new Point(endPoint.x + horSpacing, endPoint.y)};
        }
        log.debug("Line anchors {}", Arrays.toString(result));
        return result;
    }

    Side sideFrom(boolean topToBottom) {
        if (topToBottom) return Side.BOTTOM;
        else return Side.TOP;
    }

    Side sideTo(boolean leftToRight) {
        if (leftToRight) return Side.LEFT;
        else return Side.RIGHT;
    }

    /**
     * Returns the docking point of the information flow line for an application rectangle,
     *
     * @param rect An application rectangle.
     * @param side Side where the line shall connect: top, bottom, left of right.
     * @return the point where the information flow line connects to the app rectangle.
     */
    Point coordOnApp(Rectangle rect, Side side) {
        return switch (side) {
            case TOP -> new Point(rect.getX() + APP_WIDTH_HALF, rect.getY());
            case BOTTOM -> new Point(rect.getX() + APP_WIDTH_HALF, rect.getY() + APP_HEIGHT);
            case LEFT -> new Point(rect.getX(), rect.getY() + APP_HEIGHT_HALF);
            case RIGHT -> new Point(rect.getX() + APP_WIDTH, rect.getY() + APP_HEIGHT_HALF);
        };
    }

    void renderLegend(Model model) {
        var appRects = appFormatter.getSamplesForLegend();
        var compRects = compFormatter.getSamplesForLegend(model.getComponentNames());
        final int legendX = model.getL1Components().stream()
                .map(c -> c.getCol() + c.getWidth())
                .max(Integer::compareTo)
                .orElse(0) * COL_WIDTH + COL_WIDTH;
        final int maxY = model.getL1Components().stream()
                .map(c -> c.getRow() + c.getHeight())
                .max(Integer::compareTo)
                .orElse(0) * ROW_HEIGHT + ROW_HEIGHT;
        final int width = 2 * COL_WIDTH;
        final int height = Math.max(appRects.size(), compRects.length) * ROW_HEIGHT;
        add(Rectangle.builder()
                .background(Color.WHITE)
                .bordercolor(Color.BLACK)
                .x(legendX)
                .y(maxY - height)
                .w(width)
                .h(height)
                .build());
        int x = legendX + SPACING;
        int y = maxY - height + SPACING;
        for (var rect : compRects) {
            rect.setX(x);
            rect.setY(y);
            rect.setW(APP_WIDTH);
            rect.setH(APP_HEIGHT);
            add(rect);
            y += APP_HEIGHT + SPACING * 2;
        }
        x = legendX + COL_WIDTH + SPACING;
        y = maxY - height + SPACING;
        for (var rect: appRects) {
            rect.setX(x);
            rect.setY(y);
            rect.setW(APP_WIDTH);
            rect.setH(APP_HEIGHT);
            add(rect);
            y += APP_HEIGHT + SPACING * 2;
        }
    }
}
