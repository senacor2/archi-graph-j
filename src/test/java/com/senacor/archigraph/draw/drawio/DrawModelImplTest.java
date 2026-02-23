package com.senacor.archigraph.draw.drawio;

import com.senacor.archigraph.render.FontStyle;
import com.senacor.archigraph.render.Line;
import com.senacor.archigraph.render.Rectangle;
import com.senacor.archigraph.render.RenderModel;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DrawModelImplTest {

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
        var fixture = new DrawModelImpl();
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
                        style="rounded=0;whiteSpace=wrap;html=1;fontSize=22;\
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
        var fixture = new DrawModelImpl();
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
                        style="rounded=0;whiteSpace=wrap;html=1;fontSize=22;\
                        align=center;verticalAlign=middle;\
                        fillColor=#FFFFFF;fontColor=#000000;" \
                        value="Rectangle 1" vertex="1">\
                        <mxGeometry as="geometry" height="100.0" width="160.0" x="50.0" y="50.0"/>\
                        </mxCell>\
                        <mxCell id="Proxies" parent="0" value="Proxies"/>\
                        <mxCell id="RECT-2" parent="Proxies" \
                        style="rounded=0;whiteSpace=wrap;html=1;fontSize=22;\
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

    @Test
    void testProxyApplication() {
        // given
        var model = new RenderModel();
        Rectangle rect1 = Rectangle.builder()
                .id("RECT-1")
                .x(50)
                .y(50)
                .w(160)
                .h(100)
                .text("Rectangle 1")
                .fontSize(22)
                .fontStyle(FontStyle.BOLD)
                .background(Color.WHITE)
                .fontcolor(Color.BLACK)
                .build();
        Rectangle rect2 = Rectangle.builder()
                .id("RECT-2")
                .layer("Proxies")
                .x(350)
                .y(350)
                .w(160)
                .h(100)
                .text("Rectangle 2")
                .fontSize(22)
                .fontStyle(FontStyle.BOLD)
                .background(Color.WHITE)
                .fontcolor(Color.BLACK)
                .originalId("RECT-3")
                .build();
        model.setElements(List.of(
                rect1,
                rect2,
                Line.builder()
                        .id("IF12")
                        .start(rect1)
                        .end(rect2)
                        .text("IF12")
                        .layer("Proxies")
                        .hasEndArrow(true)
                        .anchors(new Point[]{})
                        .build()));
        var fixture = new DrawModelImpl();
        // test
        var result = fixture.draw(model).toString();
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
                        <UserObject id="RECT-2" label="Rectangle 2" \
                        link="data:action/json,{&quot;actions&quot;:[{&quot;select&quot;:{&quot;cells&quot;:[&quot;RECT-3&quot;]}},\
                        {&quot;scroll&quot;:{&quot;cells&quot;:[&quot;RECT-3&quot;]}}]}">\
                        <mxCell parent="Proxies" \
                        style="rounded=0;whiteSpace=wrap;html=1;fontSize=22;fontStyle=1;\
                        align=center;verticalAlign=middle;\
                        fillColor=#FFFFFF;fontColor=#000000;" \
                        vertex="1">\
                        <mxGeometry as="geometry" height="100.0" width="160.0" x="350.0" y="350.0"/>\
                        </mxCell>\
                        </UserObject>\
                        <mxCell edge="1" id="IF12" parent="Proxies" source="RECT-1" \
                        style="edgeStyle=orthogonalEdgeStyle;rounded=1;orthogonalLoop=1;jettySize=auto;html=1;\
                        curved=0;strokeWidth=2;endArrow=block;endFill=1;" \
                        target="RECT-2" value="IF12">\
                        <mxGeometry as="geometry"/>\
                        </mxCell>\
                        </root>\
                        </mxGraphModel>\
                        </diagram>\
                        </mxfile>""",
                result);

    }
}
