package com.bmw.archigraph.render;

import com.bmw.archigraph.draw.ComponentLayout;
import com.bmw.archigraph.draw.Coordinate;
import com.bmw.archigraph.model.Application;
import com.bmw.archigraph.model.Component;
import com.bmw.archigraph.model.InformationFlow;
import com.bmw.archigraph.model.Model;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.util.Arrays;
import java.util.LinkedList;
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

    /**
     * Adds a new element to the render model. This method is called during model rendering.
     *
     * @param element the new render model element.
     */
    void add(RenderModelElement element) {
        log.debug("Add render model element {}", element);
        elements.add(element);
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
        int x = comp.getAbsCol() * COL_WIDTH + indent(comp);
        int y = comp.getAbsRow() * ROW_HEIGHT + indent(comp) + (comp.getLevel() - 1) * ROW_HEIGHT;
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

    void render(Application app, int level, int compRow, int compCol, Coordinate appCoord) {
        log.debug("Render app {} compRow {} compCol {} coord {}", app.getId(), compRow, compCol, appCoord);
        add(Rectangle.builder()
                .id(app.getId())
                .text(app.getName())
                .background(BG_COLOR_APP)
                .foreground(FG_COLOR_APP)
                .fontSize(12)
                .rounded(true)
                .x(compCol * COL_WIDTH + appCoord.getCol() * COL_WIDTH + SPACING)
                .y(compRow * ROW_HEIGHT + appCoord.getRow() * ROW_HEIGHT + ROW_HEIGHT * level + SPACING)
                .w(APP_WIDTH)
                .h(APP_HEIGHT)
                .build());
    }

    /**
     * Draw the internal flow lines
     * @param comp The enclosing component
     * @param origX The x position of the components left edge
     * @param origY The y position of the components top edge
     * @param layout The row column positions of all apps inside the component.
     */
    void renderLocalFlows(Component comp, int origX, int origY, ComponentLayout layout) {
        for (var flow : comp.getLocalInformationFlows()) {
            render(flow, comp.getLevel(), origX, origY, layout);
        }
    }

    void renderL1CompFlows(Component comp, int origX, int origY, ComponentLayout layout) {
        for (var flow : comp.getL1CompInformationFlows()) {
            render(flow, 1, origX, origY, layout);
        }
    }

    void renderCrossL1CompFlows(Component comp, int origX, int origY, ComponentLayout layout) {
        // TODO create a new layout with proxies
        for (var flow : comp.getCrossL1CompInformationFlows()) {
            render(flow, 1, origX, origY, layout);
        }
    }

    void render(InformationFlow flow, int level, int origX, int origY, ComponentLayout layout) {
        log.debug("Render flow {}", flow.getId());
        Rectangle sourceRect = elements.stream()
                .filter(elem -> elem instanceof Rectangle)
                .map(elem -> (Rectangle) elem)
                .filter(elem -> elem.getId().equals(flow.getSourceId()))
                .findFirst()
                .orElseThrow();
        Rectangle destRect = elements.stream()
                .filter(elem -> elem instanceof Rectangle)
                .map(elem -> (Rectangle) elem)
                .filter(elem -> elem.getId().equals(flow.getDestId()))
                .findFirst()
                .orElseThrow();
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

    Point[] getAnchors(ComponentLayout layout, int level, Application source, Application dest, int origX, int origY) {
        var srcCoord = layout.getAppCoordinate(source);
        var dstCoord = layout.getAppCoordinate(dest);
        int distanceHor = Math.abs(srcCoord.getCol() - dstCoord.getCol());
        int distanceVrt = Math.abs(srcCoord.getRow() - dstCoord.getRow());
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
            var x1 = origX + srcCoord.getCol() * COL_WIDTH + COL_WIDTH_HALF;
            var x2 = origX + dstCoord.getCol() * COL_WIDTH + COL_WIDTH_HALF;
            var y = origY + srcCoord.getRow() * ROW_HEIGHT + SPACING_HALF;
            result = new Point[]{new Point(x1, y), new Point(x2, y)};
        } else if (distanceHor == 0 && distanceVrt > 1) {
            log.debug("Same col and apps between");
            // Start line at the right
            var x = origX + (srcCoord.getCol() + 1) * COL_WIDTH - SPACING_HALF;
            var y1 = origY + srcCoord.getRow() * ROW_HEIGHT + ROW_HEIGHT_HALF;
            var y2 = origY + dstCoord.getRow() * ROW_HEIGHT + ROW_HEIGHT_HALF;
            result = new Point[]{new Point(x, y1), new Point(x, y2)};
        } else {
            log.debug("Different rows and columns");
            var topToBottom = srcCoord.getRow() < dstCoord.getRow();
            var leftToRight = srcCoord.getCol() < dstCoord.getCol();
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

    boolean allCellsEmptyHor(ComponentLayout layout, Coordinate src, Coordinate dst) {
        var usedCells = layout.getUsedCells();
        var fromCol = Math.min(src.getCol(), dst.getCol()) + 1;
        var toCol = Math.max(src.getCol(), dst.getCol());
        for (int col = fromCol; col < toCol; col++) {
            if (usedCells.contains(new Coordinate(src.getRow(), col))) return false;
        }
        return true;
    }

    boolean allCellsEmptyVert(ComponentLayout layout, Coordinate src, Coordinate dst) {
        var usedCells = layout.getUsedCells();
        var fromRow = Math.min(src.getRow(), dst.getRow()) + 1;
        var toRow = Math.max(src.getRow(), dst.getRow());
        for (int row = fromRow; row < toRow; row++) {
            if (usedCells.contains(new Coordinate(row, src.getCol()))) return false;
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
        var x = origX + coord.getCol() * COL_WIDTH;
        var y = origY + coord.getRow() * ROW_HEIGHT;
        var spacing = SPACING - (level-1) * COMP_SPACING;
        return switch (side) {
            case TOP -> new Point(x + spacing + APP_WIDTH / 2, y + spacing);
            case BOTTOM -> new Point(x + spacing + APP_WIDTH / 2, y + spacing + APP_HEIGHT);
            case LEFT -> new Point(x + spacing, y + spacing + APP_HEIGHT / 2);
            case RIGHT -> new Point(x + spacing + APP_WIDTH, y + spacing + APP_HEIGHT / 2);
        };
    }
}
