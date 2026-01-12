package com.senacor.archigraph.rules;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class ConditionTest {

    final BusinessObject bo = new BusinessObject("Thorin");

    @ParameterizedTest
    @ValueSource(strings = {"Thorin", "*rin", "*Tho*", "*", "!Gimli", "!*li"})
    void testConditionMatch(String expr) {
        // given
        final Condition fixture = new Condition("characterName", expr);
        // when
        var result = fixture.match(bo);
        // then
        assertTrue(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"!Thorin", "Gandalf", "*Axe"})
    void testConditionNoMatch(String expr) {
        // given
        final Condition fixture = new Condition("characterName", expr);
        // when
        var result = fixture.match(bo);
        // then
        assertFalse(result);
    }

}
