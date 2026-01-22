package com.senacor.archigraph.draw;

import com.mxgraph.io.mxCodec;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxXmlUtils;
import com.senacor.archigraph.render.Line;
import com.senacor.archigraph.render.Rectangle;
import com.senacor.archigraph.render.RenderModel;
import com.senacor.archigraph.render.RenderModelElement;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Node;

import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class DrawModelDrawIO implements DrawModel {

    private mxGraphModel graph;
    private mxICell defaultLayer;
    private final Map<String, mxICell> layers = new HashMap<>();

    @Override
    public DrawModel draw(RenderModel render) {
        graph = new mxGraphModel();
        var root = (mxICell)graph.getRoot();
        defaultLayer = root.getChildAt(0);
        graph.add(root, defaultLayer, 0);
        createLayers(root, render);
        for (var elem : render.getElements()) {
            elem.draw(this);
        }
        return this;
    }

    void createLayers(mxICell root, RenderModel render) {
        var layerNames = render.getElements().stream()
                .map(RenderModelElement::getLayer)
                .filter(Objects::nonNull)
                .toList();
        var index = 1;
        for (String name : layerNames) {
            var layer = new mxCell(name);
            layer.setParent(root);
            layer.setId(name);
            graph.add(root, layer, index++);
            layers.put(name, layer);
        }
    }

    mxICell getLayer(RenderModelElement rme) {
        return layers.getOrDefault(rme.getLayer(), defaultLayer);
    }

    public void draw(Rectangle rect) {
        log.debug("Draw rect {}", rect.getId());
        var geom = new mxGeometry(rect.getX(), rect.getY(), rect.getW(), rect.getH());
        var node = new mxCell(rect.getText(), geom, getStyle(rect));
        var layer = getLayer(rect);
        var index = layer.getChildCount();

        node.setId(rect.getId());
        node.setVertex(true);
        graph.add(layer, node, index);
    }

    public void draw(Line line) {
        log.debug("Draw line {}", line.getId());
        var geom = new mxGeometry();
        var node = new mxCell(line.getText(), geom, getStyle(line));
        var layer = getLayer(line);
        var index = layer.getChildCount();

        node.setId(line.getId());
        node.setValue(line.getText());
        node.setSource((mxICell) graph.getCell(line.getStart().getId()));
        node.setTarget((mxICell) graph.getCell(line.getEnd().getId()));
        node.setEdge(true);

        geom.setRelative(false);
        if (line.getAnchors().length == 0) {
            geom.setPoints(null);
        } else {
            geom.setPoints(Arrays.stream(line.getAnchors())
                    .map(p -> new mxPoint(p.x, p.y))
                    .toList());
        }
        graph.add(layer, node, index);
    }

    String getStyle(Rectangle rect) {
        StringBuilder str = new StringBuilder();

        if (rect.isRounded()) {
            str.append("rounded=1;");
        } else {
            str.append("rounded=0;");
        }
        str.append("whiteSpace=wrap;");
        str.append("html=1;");
        str.append("fontSize="); str.append(rect.getFontSize()); str.append(';');
        str.append("fontStyle=1;");
        str.append("align=center;");
        str.append("verticalAlign=middle;");
        if (rect.getBackground() != null) {
            str.append("fillColor="); str.append(formatColor(rect.getBackground())); str.append(';');
        }
        if (rect.getFontcolor() != null) {
            str.append("fontColor="); str.append(formatColor(rect.getFontcolor())); str.append(';');
        }
        if (rect.getBordercolor() != null) {
            str.append("strokeColor="); str.append(formatColor(rect.getBordercolor())); str.append(';');
        }
        if (rect.getFillStyle() != null) {
            str.append("fillStyle="); str.append(rect.getFillStyle()); str.append(';');
        }
        return str.toString();
    }

    String getStyle(Line line) {
        StringBuilder str = new StringBuilder();

        str.append("edgeStyle=orthogonalEdgeStyle;");
        str.append("rounded=1;");
        str.append("orthogonalLoop=1;");
        str.append("jettySize=auto;");
        str.append("html=1");
        str.append("curved=0;");
        str.append("strokeWidth=2;");
        if (line.isHasStartArrow()) {
            str.append("startArrow=block;startFill=1;");
        }
        if (line.isHasEndArrow()) {
            str.append("endArrow=block;endFill=1;");
        }
        return str.toString();
    }

    String formatColor(Color c) {
      return String.format("#%02X%02X%02X", c.getRed(), c.getGreen(), c.getBlue());
    }

    public String toString() {
        var codec = new mxCodec();
        var wrapper = mxWrap(codec.encode(graph));
        return mxXmlUtils.getXml(wrapper);
    }

    public void write(String filename) throws IOException {
        log.debug("Write to {}", filename);
        try (var outfile = new FileWriter(filename)) {
            outfile.write(toString());
        }

    }

    private Node mxWrap(Node xmlRoot) {
        var document = xmlRoot.getOwnerDocument();
        var mxFileNode = document.createElement("mxfile");
        var mxDiagramNode = document.createElement("diagram");

        document.setXmlVersion("1.0");

        mxFileNode.setAttribute("host", "Electron");
        mxFileNode.setAttribute("version", "24.7.17");
        mxFileNode.appendChild(mxDiagramNode);

        mxDiagramNode.setAttribute("name", "Page-1");
        mxDiagramNode.setAttribute("id", "Diagram 1");
        mxDiagramNode.appendChild(xmlRoot);

        return mxFileNode;
    }
}
