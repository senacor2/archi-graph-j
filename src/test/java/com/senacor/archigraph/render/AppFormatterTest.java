package com.senacor.archigraph.render;

import com.senacor.archigraph.model.Application;
import com.senacor.archigraph.rules.RuleBase;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AppFormatterTest {

    private static final AppFormatter fixture = new AppFormatter();

    @BeforeAll
    static void setup() throws IOException {
        fixture.loadRules("data/rules.csv");
    }

    @Test
    void testFormatProxy() {
        // given
        var app = new Application("ID1", "App-1", "Comp-1");
        var rect = Rectangle.builder()
                .id(app.getId())
                .build();
        // when
        fixture.formatProxy(app, rect);
        // then
        assertEquals(Color.WHITE, rect.getBackground());
        assertEquals(Color.BLACK, rect.getBordercolor());
        assertEquals(Color.BLACK, rect.getFontcolor());
    }

    @Test
    void testFormatExistingApp() {
        // given
        var app = new Application("ID1", "App-1", "Comp-1");
        app.setAttribute("market", "central");
        var rect = Rectangle.builder()
                .id(app.getId())
                .build();
        // when
        fixture.format(app, rect);
        // then
        assertEquals(Color.BLACK, rect.getBackground());
        assertEquals(Color.BLACK, rect.getBordercolor());
        assertEquals(Color.WHITE, rect.getFontcolor());
    }

    @Test
    void testFormatNewApp() {
        // given
        var app = new Application("ID1", "App-1", "Comp-1");
        app.setAttribute("market", "central");
        app.setAttribute("target", "2026");
        var rect = Rectangle.builder()
                .id(app.getId())
                .build();
        // when
        fixture.format(app, rect);
        // then
        assertEquals(AppFormatter.LAWN_GREEN, rect.getBackground());
        assertEquals(Color.WHITE, rect.getFontcolor());
        assertEquals(Color.BLACK, rect.getBordercolor());
    }

    @Test
    void testFormatRetiredApp() {
        // given
        var app = new Application("ID1", "App-1", "Comp-1");
        app.setAttribute("market", "central");
        app.setAttribute("replacedByTnr", "Yes");
        var rect = Rectangle.builder()
                .id(app.getId())
                .build();
        // when
        fixture.format(app, rect);
        // then
        assertEquals(AppFormatter.PINK, rect.getBackground());
        assertEquals(Color.BLACK, rect.getBordercolor());
        assertEquals(Color.BLACK, rect.getFontcolor());
    }

    @Test
    void testFormatLocalApp() {
        var app = new Application("ID1", "App-1", "Comp-1");
        app.setAttribute("market", "DE");
        var rect = Rectangle.builder()
                .id(app.getId())
                .build();
        // when
        fixture.format(app, rect);
        // then
        assertEquals(AppFormatter.SEA_GREEN, rect.getBackground());
        assertEquals(Color.BLACK, rect.getBordercolor());
        assertEquals(Color.WHITE, rect.getFontcolor());
    }

    @Test
    void testGetLegend() {
        assertEquals(5, fixture.getSamplesForLegend().length);
    }


}
