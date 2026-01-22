package com.senacor.archigraph.draw;

import com.senacor.archigraph.render.Rectangle;
import com.senacor.archigraph.render.RenderModel;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DrawModelDrawIOTest {

    @Test
    void testDrawSimpleGraph() {
        // fixture
        var renderModel = new RenderModel();
        renderModel.setElements(List.of(
                Rectangle.builder()
                        .id("RECT-1")
                        .x(50)
                        .y(50)
                        .w(160)
                        .h(100)
                        .text("Rectangle 1")
                        .fontSize(22)
                        .background(Color.WHITE)
                        .fontcolor(Color.BLACK)
                        .build()));
        var fixture = new DrawModelDrawIO();
        // test
        var result = fixture.draw(renderModel).toString();
        // assert
        assertEquals(
                """
                        <mxfile host="Electron" version="24.7.17">\
                        <diagram id="Diagram 1" name="Page-1">\
                        <mxGraphModel>\
                        <root>\
                        <mxCell id="0"/>\
                        <mxCell id="1" parent="0"/>\
                        <mxCell id="RECT-1" parent="1" \
                        style="rounded=0;whiteSpace=wrap;html=1;fontSize=22;fontStyle=1;\
                        align=center;verticalAlign=middle;\
                        fillColor=#FFFFFF;fontColor=#000000;" \
                        value="Rectangle 1" vertex="1">\
                        <mxGeometry as="geometry" height="100.0" width="160.0" x="50.0" y="50.0"/>\
                        </mxCell>\
                        </root>\
                        </mxGraphModel>\
                        </diagram>\
                        </mxfile>""",
                result);
    }

    @Test
    void testGraphWithLayer() {
        // fixture
        var renderModel = new RenderModel();
        renderModel.setElements(List.of(
                Rectangle.builder()
                        .id("RECT-1")
                        .x(50)
                        .y(50)
                        .w(160)
                        .h(100)
                        .text("Rectangle 1")
                        .fontSize(22)
                        .background(Color.WHITE)
                        .fontcolor(Color.BLACK)
                        .build(),
                Rectangle.builder()
                        .id("RECT-2")
                        .layer("Proxies")
                        .x(350)
                        .y(350)
                        .w(160)
                        .h(100)
                        .text("Rectangle 2")
                        .fontSize(22)
                        .background(Color.WHITE)
                        .fontcolor(Color.BLACK)
                        .build()));
        var fixture = new DrawModelDrawIO();
        // test
        var result = fixture.draw(renderModel).toString();
        // assert
        assertEquals(
                """
                        <mxfile host="Electron" version="24.7.17">\
                        <diagram id="Diagram 1" name="Page-1">\
                        <mxGraphModel>\
                        <root>\
                        <mxCell id="0"/>\
                        <mxCell id="1" parent="0"/>\
                        <mxCell id="RECT-1" parent="1" \
                        style="rounded=0;whiteSpace=wrap;html=1;fontSize=22;fontStyle=1;\
                        align=center;verticalAlign=middle;\
                        fillColor=#FFFFFF;fontColor=#000000;" \
                        value="Rectangle 1" vertex="1">\
                        <mxGeometry as="geometry" height="100.0" width="160.0" x="50.0" y="50.0"/>\
                        </mxCell>\
                        <mxCell id="Proxies" parent="0" value="Proxies"/>\
                        <mxCell id="RECT-2" parent="Proxies" \
                        style="rounded=0;whiteSpace=wrap;html=1;fontSize=22;fontStyle=1;\
                        align=center;verticalAlign=middle;\
                        fillColor=#FFFFFF;fontColor=#000000;" \
                        value="Rectangle 2" vertex="1">\
                        <mxGeometry as="geometry" height="100.0" width="160.0" x="350.0" y="350.0"/>\
                        </mxCell>\
                        </root>\
                        </mxGraphModel>\
                        </diagram>\
                        </mxfile>""",
                result);
    }
}
