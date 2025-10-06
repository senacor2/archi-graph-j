package com.bmw.archigraph.render;

import com.bmw.archigraph.draw.ComponentLayout;
import com.bmw.archigraph.draw.Coordinate;
import com.bmw.archigraph.draw.AbstractLayout;
import com.bmw.archigraph.draw.ProxyBoxLayout;
import com.bmw.archigraph.model.Application;
import com.bmw.archigraph.model.Component;
import com.bmw.archigraph.model.InformationFlow;
import com.bmw.archigraph.model.Model;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.util.*;
import java.util.List;

@Data
@Slf4j
public class RenderModel {

    private static final Color BG_COLOR_MODEL = new Color(0, 0, 156);
    private static final Color FG_COLOR_MODEL = Color.WHITE;
    private static final Color BG_COLOR_COMP_HEAD = new Color(0, 0, 110);
    private static final Color FG_COLOR_COMP_HEAD = Color.WHITE;
    private static final Color BG_COLOR_COMP_BODY = Color.WHITE;
    private static final Color BG_COLOR_APP = Color.WHITE;
    private static final Color FG_COLOR_APP = Color.BLACK;
    private static final Color COLOR_LINE = Color.BLACK;

    private static final int ROW_HEIGHT = 200;
    private static final int COL_WIDTH = (int) (ROW_HEIGHT * 1.6);
    private static final int ROW_HEIGHT_HALF = ROW_HEIGHT / 2;
    private static final int COL_WIDTH_HALF = COL_WIDTH / 2;
    private static final int COMP_SPACING = 10;
    private static final int SPACING = 40;
    private static final int SPACING_HALF = SPACING / 2;
    private static final int APP_WIDTH = COL_WIDTH - SPACING * 2;
    private static final int APP_HEIGHT = ROW_HEIGHT - SPACING * 2;

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
     * Populates the RenderModel with logical drawing components like rectangles and lines.
     *
     * @param model An architecture model to be rendered
     * @return the populated render model.
     */
    public RenderModel render(Model model) {
        add(Rectangle.builder()
                .id("ROOT")
                .text(model.getName())
                .background(BG_COLOR_MODEL)
                .foreground(FG_COLOR_MODEL)
                .fontSize(48)
                .x(0)
                .y(0)
                .w(model.getL1Components().stream()
                        .map(c -> c.getCol() + c.getWidth())
                        .max(Integer::compareTo)
                        .orElse(0) * COL_WIDTH + COL_WIDTH)
                .h(ROW_HEIGHT_HALF)
                .build());
        for (var c : model.getL1Components()) {
            render(c);
        }
        return this;
    }

    /**
     * Render a component consisting of a heading and the body where the apps are drawn.
     *
     * @param comp  A component
     */
    void render(Component comp) {
        int x = comp.getAbsCompCol() * COL_WIDTH + indent(comp);
        int y = comp.getAbsCompRow() * ROW_HEIGHT + indent(comp) + (comp.getLevel() - 1) * ROW_HEIGHT;
        int w = comp.getWidth() * COL_WIDTH - (comp.getLevel() - 1) * COMP_SPACING * 2;
        log.debug("Render comp {} orig {}/{}", comp, x, y);

        // Heading rectangle
        add(Rectangle.builder()
                .id(comp.getName().replace(" ", "_") + "_head")
                .text(comp.getName())
                .background(BG_COLOR_COMP_HEAD)
                .foreground(FG_COLOR_COMP_HEAD)
                .fontSize(switch (comp.getLevel()) {
                    case 1 -> 48;
                    case 2 -> 40;
                    case 3 -> 32;
                    default -> 24;
                })
                .x(x)
                .y(y)
                .w(w)
                .h(ROW_HEIGHT)
                .build()
        );
        y += ROW_HEIGHT;
        // Body rectangle
        add(Rectangle.builder()
                .id(comp.getName().replace(" ", "_") + "_body")
                .background(BG_COLOR_COMP_BODY)
                .x(x)
                .y(y)
                .w(w)
                .h((comp.getHeight() - 1) * ROW_HEIGHT - indent(comp) * 2)
                .build()
        );
        // App Area
        add(Rectangle.builder()
                .id(comp.getName().replace(" ", "_") + "_apparea")
                .background(Color.LIGHT_GRAY)
                .foreground(Color.WHITE)
                .x(comp.getAbsoluteAppCol() * COL_WIDTH + indent(comp))
                .y(comp.getAbsoluteAppRow() * ROW_HEIGHT + indent(comp) + (comp.getLevel() - 1) * ROW_HEIGHT)
                .w(comp.getAppWidth() * COL_WIDTH - (comp.getLevel() - 1) * COMP_SPACING * 2)
                .h(comp.getAppHeight() * ROW_HEIGHT)
                .build());
        var layout = renderApplications(comp);
        renderLocalFlows(comp, x, y, layout);
        comp.setLayout(layout);
        for (var c : comp.getComponents()) {
            render(c);
        }
        if (comp.getParentComponent() != null) { // L1 components have no parent
            comp.getParentComponent().getLayout().add(layout, comp.getRow(), comp.getCol());
        } else { // but non-local flows are rendered at the l1 level
            renderL1CompFlows(comp, x, y, comp.getLayout());
            renderCrossL1CompFlows(comp, x, y, comp.getLayout());
        }
    }

    private static int indent(Component comp) {
        return (comp.getLevel() - 1) * COMP_SPACING;
    }

    /**
     * Render the applications inside a component.
     *
     * @param comp  The enclosing component.
     * @return the component layout, i.e. the row column positions for each app inside the component.
     */
    ComponentLayout renderApplications(Component comp) {
        var cl = new ComponentLayout(comp);
        cl.layout();
        for (var a : comp.getApplications()) {
            var coord = cl.getAppCoordinate(a);
            render(a, comp.getLevel(), comp.getAbsoluteAppRow(), comp.getAbsoluteAppCol(), coord);
        }
        return cl;
    }

    /**
     * Render an application into the render model.
     * @param app An Application object.
     * @param level The nesting level of the enclosing component. Used to calculate the offset of the component
     *              headings.
     * @param appAreaRow Absolute row position of the components app area.
     * @param appAreaCol Absolute column position of the components app area.
     * @param appCoord Row/Column coordinates of the application inside the component.
     */
    void render(Application app, int level, int appAreaRow, int appAreaCol, Coordinate appCoord) {
        log.debug("Render app {} appAreaRow {} appAreaCol {} coord {}", app.getId(), appAreaRow, appAreaCol, appCoord);
        add(Rectangle.builder()
                .id(app.getId())
                .text(app.getName())
                .background(BG_COLOR_APP)
                .foreground(FG_COLOR_APP)
                .fontSize(12)
                .rounded(true)
                .x(appAreaCol * COL_WIDTH + appCoord.col() * COL_WIDTH + SPACING)
                .y(appAreaRow * ROW_HEIGHT + indent(app.getComponent()) + (level - 1) * ROW_HEIGHT +
                        appCoord.row() * ROW_HEIGHT + SPACING)
                .w(APP_WIDTH)
                .h(APP_HEIGHT)
                .build());
    }

    /**
     * Draw the internal flow lines inside one component.
     * @param comp The enclosing component
     * @param origX The x position of the components left edge
     * @param origY The y position of the components top edge
     * @param layout The row column positions of all apps inside the component.
     */
    void renderLocalFlows(Component comp, int origX, int origY, ComponentLayout layout) {
        log.debug("Render local flows for {}", comp.getName());
        for (var flow : comp.getLocalInformationFlows()) {
            render(flow, comp.getLevel(), origX, origY, comp, layout);
        }
    }

    /**
     * Draw the flows inside one L1 component.
     * @param comp the L1 component.
     * @param origX The x position of the components left edge.
     * @param origY The y position of the components top edge.
     * @param layout The row column position of all apps inside this component.
     */
    void renderL1CompFlows(Component comp, int origX, int origY, AbstractLayout layout) {
        log.debug("Render l1 flows for {}", comp.getName());
        for (var flow : comp.getL1CompInformationFlows()) {
            render(flow, 1, origX, origY, comp, layout);
        }
    }

    /**
     *  Render the information flows across L1 component boundaries.
     *  Apps outside the component are represented by proxies which are placed around the L1 component.
     *  Proxies are created in this function.
     * @param comp The L1 component.
     * @param origX The x position of the components left edge.
     * @param origY The y position of the components top edge.
     * @param layout The rol column position of all apps inside this component.
     */
    void renderCrossL1CompFlows(Component comp, int origX, int origY, ComponentLayout layout) {
        log.debug("Render cross l1 flows for {}", comp.getName());
        int proxyOrigX = origX - COL_WIDTH;
        int proxyOrigY = origY - ROW_HEIGHT;
        var proxyBoxLayout = createAndPlaceProxies(comp, proxyOrigX, proxyOrigY);
        for (var flow : comp.getCrossL1CompInformationFlows()) {
            render(flow, 1, proxyOrigX, proxyOrigY, comp, proxyBoxLayout);
        }
    }

    /**
     * Render an information flow.
     * Adds a line object to the model connecting the source and destination app.
     * The two apps may be in the same component, in the same L1 component or in different L1 components.
     * @param flow An InformationFlow
     * @param level the nesting level of the component. Top level components have level 1.
     * @param origX The left edge of the enclosing component.
     * @param origY The top edge of the enclosing component.
     * @param comp The enclosing component or the L1 component containing one of the apps.
     * @param layout The layout of the enclosing component.
     */
    void render(InformationFlow flow, int level, int origX, int origY, Component comp, AbstractLayout layout) {
        log.debug("Render flow {}", flow.getId());
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
        Point[] anchors = getAnchors(layout, level, flow.getSource(), flow.getDestination(), origX, origY);

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
     * @return Returns a proxy box layout that contains the l1 layout plus all placed proxy apps.
     */
    private ProxyBoxLayout createAndPlaceProxies(Component comp, int origX, int origY) {
        var proxyBoxLayout = new ProxyBoxLayout(comp);
        for (var flow : comp.getCrossL1CompInformationFlows()) {
            var proxyApp = flow.getSource().getComponent().getL1Component() == comp ? flow.getDestination() : flow.getSource();
            var innerApp = flow.getSource().getComponent().getL1Component() == comp ? flow.getSource() : flow.getDestination();
            if (proxyBoxLayout.getAppCoordinate(proxyApp) == null) {
                var proxyAppCoord = proxyBoxLayout.findNearestEmptyCell(proxyBoxLayout.getAppCoordinate(innerApp));
                proxyBoxLayout.setProxyPosition(proxyApp, proxyAppCoord);
                add(renderApplicationProxy(proxyApp, comp, origX, origY, proxyAppCoord, proxyBoxLayout));
            }
        }
        return proxyBoxLayout;
    }

    private Rectangle renderApplicationProxy(Application app, Component parent, int origX, int origY,
                                             Coordinate proxyAppCoord, ProxyBoxLayout layout) {
        return Rectangle.builder()
                .id(getProxyAppId(parent.getName(), app.getId()))
                .text(app.getName())
                .fontSize(12)
                .background(Color.WHITE)
                .foreground(Color.BLACK)
                .rounded(true)
                .x(origX + proxyAppCoord.col() * COL_WIDTH + SPACING)
                .y(origY + proxyAppCoord.row() * ROW_HEIGHT + SPACING)
                .w(APP_WIDTH)
                .h(APP_HEIGHT)
                .build();
    }

    /**
     * Returns a vector of turning points for the line drawn between two applications.
     * The algorithm considers space blocked by other apps and routes lines around them.
     * @param layout The component layout that contains the coordinates of the apps and all other apps.
     * @param level nesting level of the enclosing component. Used to compute offsets.
     * @param source Application where the information flow starts.
     * @param dest Application where the information flow ends.
     * @param origX Left edge of the enclosing component.
     * @param origY Top edge of the enclosing component.
     * @return A vector of points where the information flow line shall be bent. The vector will be empty
     * when the Apps are directly adjacent or on the same for or column with no apps inbetween.
     */
    Point[] getAnchors(AbstractLayout layout, int level, Application source, Application dest, int origX, int origY) {
        var srcCoord = layout.getAppCoordinate(source);
        var dstCoord = layout.getAppCoordinate(dest);
        int distanceHor = Math.abs(srcCoord.col() - dstCoord.col());
        int distanceVrt = Math.abs(srcCoord.row() - dstCoord.row());
        Point[] result;

        log.debug("Get line anchors from {} to {}", srcCoord, dstCoord);
        if ((distanceHor == 0 && distanceVrt == 1) || (distanceVrt == 0 && distanceHor == 1)) {
            log.debug("Directly adjacent");
            result = new Point[0];
        } else if (distanceVrt == 0 && distanceHor > 1 && allCellsEmptyHor(layout, srcCoord, dstCoord)) {
            log.debug("Same row and space between is empty");
            result = new Point[0];
        } else if (distanceHor == 0 && distanceVrt > 1 && allCellsEmptyVert(layout, srcCoord, dstCoord)) {
            log.debug("Same column and space between is empty");
            result = new Point[0];
        } else if (distanceVrt == 0 && distanceHor > 1) {
            log.debug("Same row and apps between");
            // Start line at the top
            var x1 = origX + srcCoord.col() * COL_WIDTH + COL_WIDTH_HALF;
            var x2 = origX + dstCoord.col() * COL_WIDTH + COL_WIDTH_HALF;
            var y = origY + srcCoord.row() * ROW_HEIGHT + SPACING_HALF;
            result = new Point[]{new Point(x1, y), new Point(x2, y)};
        } else if (distanceHor == 0 && distanceVrt > 1) {
            log.debug("Same col and apps between");
            // Start line at the right
            var x = origX + (srcCoord.col() + 1) * COL_WIDTH - SPACING_HALF;
            var y1 = origY + srcCoord.row() * ROW_HEIGHT + ROW_HEIGHT_HALF;
            var y2 = origY + dstCoord.row() * ROW_HEIGHT + ROW_HEIGHT_HALF;
            result = new Point[]{new Point(x, y1), new Point(x, y2)};
        } else {
            log.debug("Different rows and columns");
            var topToBottom = srcCoord.row() < dstCoord.row();
            var leftToRight = srcCoord.col() < dstCoord.col();
            var startPoint = coordOnApp(level, origX, origY, srcCoord, sideFrom(topToBottom));
            var endPoint = coordOnApp(level, origX, origY, dstCoord, sideTo(leftToRight));
            var horSpacing = leftToRight ? - SPACING_HALF : SPACING_HALF;
            var vrtSpacing = topToBottom ? SPACING_HALF : - SPACING_HALF;
            result = new Point[]{
                    new Point(startPoint.x, startPoint.y + vrtSpacing),
                    new Point(endPoint.x + horSpacing, startPoint.y + vrtSpacing),
                    new Point(endPoint.x + horSpacing, endPoint.y)};
        }
        log.debug("Line anchors {}", Arrays.toString(result));
        return result;
    }

    boolean allCellsEmptyHor(AbstractLayout layout, Coordinate src, Coordinate dst) {
        var usedCells = layout.getUsedCells();
        var fromCol = Math.min(src.col(), dst.col()) + 1;
        var toCol = Math.max(src.col(), dst.col());
        for (int col = fromCol; col < toCol; col++) {
            if (usedCells.contains(new Coordinate(src.row(), col))) return false;
        }
        return true;
    }

    boolean allCellsEmptyVert(AbstractLayout layout, Coordinate src, Coordinate dst) {
        var usedCells = layout.getUsedCells();
        var fromRow = Math.min(src.row(), dst.row()) + 1;
        var toRow = Math.max(src.row(), dst.row());
        for (int row = fromRow; row < toRow; row++) {
            if (usedCells.contains(new Coordinate(row, src.col()))) return false;
        }
        return true;
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
     * Returns the docking point of the information flow line for a given position in the
     * component grid.
     * @param level nesting level of the component.
     * @param origX origin of the component on the x-axis.
     * @param origY origin of the component on the y-axis.
     * @param coord row/col position of the component.
     * @param side Side where the line shall connect: top, bottom, left of right.
     * @return the point where the information flow line connects to the app rectangle.
     */
    Point coordOnApp(int level, int origX, int origY, Coordinate coord, Side side) {
        var x = origX + coord.col() * COL_WIDTH;
        var y = origY + coord.row() * ROW_HEIGHT;
        var spacing = SPACING - (level-1) * COMP_SPACING;
        return switch (side) {
            case TOP -> new Point(x + spacing + APP_WIDTH / 2, y + spacing);
            case BOTTOM -> new Point(x + spacing + APP_WIDTH / 2, y + spacing + APP_HEIGHT);
            case LEFT -> new Point(x + spacing, y + spacing + APP_HEIGHT / 2);
            case RIGHT -> new Point(x + spacing + APP_WIDTH, y + spacing + APP_HEIGHT / 2);
        };
    }
}
