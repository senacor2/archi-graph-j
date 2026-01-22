package com.senacor.archigraph.rules;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConditionTest {

    final BusinessObject bo = new BusinessObject("Thorin");

    final Map<String, String> context = Map.of("book", "The Hobbit");

    @ParameterizedTest
    @ValueSource(strings = {"Thorin", "*rin", "*Tho*", "*", "!Gimli", "!*li"})
    void testConditionMatch(String expr) {
        // given
        final Condition fixture = new Condition("characterName", expr);
        // when
        var result = fixture.match(bo, Map.of());
        // then
        assertTrue(result);
    }

    @Test
    void testContextMatch() {
        // given
        final Condition fixture = new Condition("book", "The Hobbit");
        // when
        var result = fixture.match(bo, context);
        // then
        assertTrue(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"!Thorin", "Gandalf", "*Axe"})
    void testConditionNoMatch(String expr) {
        // given
        final Condition fixture = new Condition("characterName", expr);
        // when
        var result = fixture.match(bo, Map.of());
        // then
        assertFalse(result, "Attribute");
    }

}
