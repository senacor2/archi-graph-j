package com.senacor.archigraph.render;

import com.senacor.archigraph.model.Component;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.awt.Color;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ComponentFormatterTest {

    final ComponentFormatter fixture = new ComponentFormatter();

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4})
    void testFormatL1Component(int level) {
        // given
        var comp = new Component("Comp1", 0, 0, 2, 2, level);
        var headRect = Rectangle.builder()
                .id(comp.getName())
                .build();
        var bodyRect = Rectangle.builder()
                .id(comp.getName() + "-body")
                .build();
        // when
        fixture.formatHead(comp, headRect);
        fixture.formatBody(comp, bodyRect);
        // then
        assertEquals(ComponentFormatter.BG_COLOR_COMP_HEAD[level-1], headRect.getBackground());
        assertEquals(ComponentFormatter.FONT_COLOR_COMP_HEAD[level-1], headRect.getFontcolor());
        assertEquals(Color.BLACK, headRect.getBordercolor());
        assertEquals(ComponentFormatter.BG_COLOR_COMP_BODY, bodyRect.getBackground());
        assertEquals(ComponentFormatter.FG_COLOR_COMP_BODY, bodyRect.getBordercolor());
    }

    @Test
    void testGetLegend() {
        assertEquals(3, fixture.getSamplesForLegend(List.of("Domain", "Product", "Subproduct")).length);
    }
}
