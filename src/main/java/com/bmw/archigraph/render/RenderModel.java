package com.bmw.archigraph.render;

import com.bmw.archigraph.draw.ComponentLayout;
import com.bmw.archigraph.model.Application;
import com.bmw.archigraph.model.Component;
import com.bmw.archigraph.model.Model;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

@Data
@Slf4j
public class RenderModel {

    private static final Color BG_COLOR_MODEL = new Color(0, 0, 156);
    private static final Color FG_COLOR_MODEL = new Color(255, 255, 255);
    private static final Color BG_COLOR_COMP = new Color(0, 0, 110);
    private static final Color FG_COLOR_COMP = new Color(255, 255, 255);
    private static final Color BG_COLOR_APP = new Color(255, 255, 255);
    private static final Color FG_COLOR_APP = new Color(0, 0, 0);
    private static final Color COLOR_LINE = new Color(0, 0, 0);

    private static final int ROW_HEIGHT = 200;
    private static final int COL_WIDTH = (int) (ROW_HEIGHT * 1.6);
    private static final int ROW_HEIGHT_HALF = ROW_HEIGHT / 2;
    private static final int COL_WIDTH_HALF = COL_WIDTH / 2;
    private static final int SPACING = 40;
    private static final int APP_WIDTH = COL_WIDTH - SPACING * 2;
    private static final int APP_HEIGHT = ROW_HEIGHT - SPACING * 2;

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
     * @param c A component
     * @param origX the grid position (column) of the components left edge.
     * @param origY the grid position (row) of the components top.
     */
    void render(Component c, int origX, int origY) {
        // Heading rectangle
        add(Rectangle.builder()
                .id(c.getName().replace(" ", "_") + "_head")
                .text(c.getName())
                .background(BG_COLOR_COMP)
                .foreground(FG_COLOR_COMP)
                .fontSize(switch (c.getLevel()) {
                    case 1 -> 24;
                    case 2 -> 20;
                    case 3 -> 16;
                    default -> 12;
                })
                .x(origX + c.getX() * COL_WIDTH)
                .y(origY + c.getY() * ROW_HEIGHT)
                .w(c.getW() * COL_WIDTH)
                .h(ROW_HEIGHT_HALF)
                .build()
        );
        // Body rectangle
        add(Rectangle.builder()
                .id(c.getName().replace(" ", "_") + "_body")
                .background(Color.WHITE)
                .x(origX + c.getX() * COL_WIDTH)
                .y(origY + c.getY() * ROW_HEIGHT)
                .w(c.getW() * COL_WIDTH)
                .h(c.getH() * ROW_HEIGHT + ROW_HEIGHT_HALF)
                .build()
        );
        renderApplications(c, origX, origY);
    }

    /**
     * Render the applications inside a component.
     *
     * @param comp The enclosing component.
     * @param origX left edge of the component
     * @param origY top edge of the component
     */
    void renderApplications(Component comp, int origX, int origY) {
        var cl = new ComponentLayout(comp);
        cl.layout();
        for (var a : comp.getApplications()) {
            var coord = cl.getAppCoordinate(a);
            render(a, origX, origY, coord.row(), coord.col());
        }
    }

    void render(Application app, int origX, int origY, int row, int col) {
        add(Rectangle.builder()
                .id(app.getId())
                .text(app.getName())
                .background(Color.WHITE)
                .background(Color.BLACK)
                .fontSize(12)
                .rounded(true)
                .x(origX + col * COL_WIDTH + SPACING)
                .y(origY + row * ROW_HEIGHT + SPACING)
                .w(APP_WIDTH)
                .h(APP_HEIGHT)
                .build());
    }
}
