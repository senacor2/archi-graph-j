package com.senacor.archigraph.render;

import com.senacor.archigraph.model.Model;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ModelFormatterTest {

    final ModelFormatter fixture = new ModelFormatter();

    @Test
    void testFormatModel() {
        // given
        var model = Model.builder()
                .name("Model 1")
                .build();
        var rect = Rectangle.builder()
                .id(model.getName())
                .build();
        // when
        fixture.formatHead(model, rect);
        // then
        assertEquals(ModelFormatter.BG_COLOR_MODEL, rect.getBackground());
        assertEquals(ModelFormatter.FG_COLOR_MODEL, rect.getFontcolor());
    }
}
