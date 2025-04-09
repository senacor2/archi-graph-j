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
                .fontSize(28)
                .x(0)
                .y(0)
                .w(model.getL1Components().stream()
                        .map(c -> c.getX() + c.getW())
                        .max(Integer::compareTo)
                        .orElse(0) * COL_WIDTH)
                .h(ROW_HEIGHT_HALF)
                .build());
        for (var c : model.getL1Components()) {
            render(c, 0, ROW_HEIGHT);
        }
        return this;
    }

    /**
     * Render a component consisting of a heading and the body where the apps are drawn.
     *
     * @param comp  A component
     * @param origX the grid position (column) of the components left edge.
     * @param origY the grid position (row) of the components top.
     */
    void render(Component comp, int origX, int origY) {
        int x = origX + comp.getX() * COL_WIDTH;
        int y = origY + comp.getY() * ROW_HEIGHT;
        log.debug("Render comp {} orig {}/{}", comp.getName(), x, y);

        // Heading rectangle
        add(Rectangle.builder()
                .id(comp.getName().replace(" ", "_") + "_head")
                .text(comp.getName())
                .background(BG_COLOR_COMP_HEAD)
                .foreground(FG_COLOR_COMP_HEAD)
                .fontSize(switch (comp.getLevel()) {
                    case 1 -> 24;
                    case 2 -> 20;
                    case 3 -> 16;
                    default -> 12;
                })
                .x(x)
                .y(y)
                .w(comp.getW() * COL_WIDTH)
                .h(ROW_HEIGHT_HALF)
                .build()
        );
        y += ROW_HEIGHT_HALF;
        // Body rectangle
        add(Rectangle.builder()
                .id(comp.getName().replace(" ", "_") + "_body")
                .background(BG_COLOR_COMP_BODY)
                .x(x)
                .y(y)
                .w(comp.getW() * COL_WIDTH)
                .h(comp.getH() * ROW_HEIGHT)
                .build()
        );
        var layout = renderApplications(comp, x, y);
        renderInternalFlows(comp, x, y, layout);
        for (var c : comp.getComponents()) {
            render(c, x + COL_WIDTH_HALF, y + ROW_HEIGHT_HALF);
        }
    }

    /**
     * Render the applications inside a component.
     *
     * @param comp  The enclosing component.
     * @param origX left edge of the component
     * @param origY top edge of the component
     * @return the component layout, i.e. the row column positions for each app inside the component.
     */
    ComponentLayout renderApplications(Component comp, int origX, int origY) {
        var cl = new ComponentLayout(comp);
        cl.layout();
        for (var a : comp.getApplications()) {
            var coord = cl.getAppCoordinate(a);
            render(a, origX, origY, coord.row(), coord.col());
        }
        return cl;
    }

    void render(Application app, int origX, int origY, int row, int col) {
        log.debug("Render app {} orig {}/{} row {} col {}", app.getId(), origX, origY, row, col);
        add(Rectangle.builder()
                .id(app.getId())
                .text(app.getName())
                .background(BG_COLOR_APP)
                .foreground(FG_COLOR_APP)
                .fontSize(12)
                .rounded(true)
                .x(origX + col * COL_WIDTH + SPACING)
                .y(origY + row * ROW_HEIGHT + SPACING)
                .w(APP_WIDTH)
                .h(APP_HEIGHT)
                .build());
    }

    void renderInternalFlows(Component comp, int origX, int origY, ComponentLayout layout) {
        for (var flow : comp.getInternalInformationFlows()) {
            render(flow, origX, origY, layout);
        }
    }

    void render(InformationFlow flow, int origX, int origY, ComponentLayout layout) {
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
        Point[] anchors = getAnchors(layout, flow.getSource(), flow.getDestination(), origX, origY);

        add(Line.builder()
                .id(flow.getId())
                .text(flow.getBusinessObject())
                .start(sourceRect)
                .end(destRect)
                .anchors(anchors)
                .build());
    }

    Point[] getAnchors(ComponentLayout layout, Application source, Application dest, int origX, int origY) {
        var srcCoord = layout.getAppCoordinate(source);
        var dstCoord = layout.getAppCoordinate(dest);
        int distanceHor = Math.abs(srcCoord.getCol() - dstCoord.getCol());
        int distanceVrt = Math.abs(srcCoord.getRow() - dstCoord.getRow());
        Point[] result;

        if ((distanceHor == 0 && distanceVrt == 1) || (distanceVrt == 0 && distanceHor == 1)) {
            // directly adjacent
            result = new Point[0];
        } else if (distanceVrt == 0 && distanceHor > 1 && allCellsEmptyHor(layout, srcCoord, dstCoord)) {
            // same row and the column is empty between the cells
            result = new Point[0];
        } else if (distanceHor == 0 && distanceVrt > 1 && allCellsEmptyVert(layout, srcCoord, dstCoord)) {
            // same column and the row is empty between the cells
            result = new Point[0];
        } else if (distanceVrt == 0 && distanceHor > 1) {
            // same row with apps between
            // Start line at the top
            var x1 = origX + srcCoord.getCol() * COL_WIDTH + COL_WIDTH_HALF;
            var x2 = origX + dstCoord.getCol() * COL_WIDTH + COL_WIDTH_HALF;
            var y = origY + srcCoord.getRow() * ROW_HEIGHT + SPACING_HALF;
            result = new Point[]{new Point(x1, y), new Point(x2, y)};
        } else if (distanceHor == 0 && distanceVrt > 1) {
            // same column with apps between
            // Start line at the right
            var x = origX + (srcCoord.getCol() + 1) * COL_WIDTH - SPACING_HALF;
            var y1 = origY + srcCoord.getRow() * ROW_HEIGHT + ROW_HEIGHT_HALF;
            var y2 = origY + dstCoord.getRow() * ROW_HEIGHT + ROW_HEIGHT_HALF;
            result = new Point[]{new Point(x, y1), new Point(x, y2)};
        } else {
            // different rows and columns
            var topToBottom = srcCoord.getRow() < dstCoord.getRow();
            var leftToRight = srcCoord.getCol() < dstCoord.getCol();
            var startPoint = coordOnApp(origX, origY, srcCoord, sideFrom(topToBottom));
            var endPoint = coordOnApp(origX, origY, dstCoord, sideTo(leftToRight));
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
     * @param origX origin of the component on the x axis.
     * @param origY origin of the component on the y axis.
     * @param coord row/col position of the component.
     * @param side Side where the line shall connect: top, bottom, left of right.
     * @return the point where the information flow line connects to the app rectangle.
     */
    Point coordOnApp(int origX, int origY, Coordinate coord, Side side) {
        var x = origX + coord.getCol() * COL_WIDTH;
        var y = origY + coord.getRow() * ROW_HEIGHT;
        return switch (side) {
            case TOP -> new Point(x + SPACING + APP_WIDTH / 2, y + SPACING);
            case BOTTOM -> new Point(x + SPACING + APP_WIDTH / 2, y + SPACING + APP_HEIGHT);
            case LEFT -> new Point(x + SPACING, y + SPACING + APP_HEIGHT / 2);
            case RIGHT -> new Point(x + SPACING + APP_WIDTH, y + SPACING + APP_HEIGHT / 2);
        };
    }
}
