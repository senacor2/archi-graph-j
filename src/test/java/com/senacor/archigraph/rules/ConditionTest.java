package com.senacor.archigraph.rules;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ConditionTest {

    final BusinessObject bo = new BusinessObject("Thorin");
    @Test
    void testConditionEquals() {
        // given
        final Condition fixture = new Condition("characterName", "Thorin");
        // when
        var result = fixture.match(bo);
        // then
        assertTrue(result);
    }

    @Test
    void testConditionNotEquals() {
        // given
        final Condition fixture = new Condition("characterName", "!Thorin");
        // when
        var result = fixture.match(bo);
        // then
        assertFalse(result);
    }

    @Test
    void testConditionWildcard() {
        // given
        final Condition fixture = new Condition("characterName", "*");
        // when
        var result = fixture.match(bo);
        // then
        assertTrue(result);
    }
}
