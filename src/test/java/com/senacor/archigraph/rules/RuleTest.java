package com.senacor.archigraph.rules;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class RuleTest {

    final BusinessObject bo = new BusinessObject("Thorin");
    final Map<String, String> context = Map.of("book", "The Hobbit");

    @Test
    void testRuleMatchSimple() {
        // given
        var fixture = new Rule("simple",
                List.of(
                        new Condition("characterName", "Thorin"),
                        new Condition("book", "The Hobbit")),
                Map.of("Weapon", "Axe"));
        // when
        var resultWithContext = fixture.evaluate(bo, context);
        // then
        assertThat(resultWithContext).isPresent();
        assertThat(resultWithContext.get()).hasSize(1);
        assertThat(resultWithContext.get()).containsEntry("Weapon", "Axe");
    }

    @Test
    void testRuleNoMatchSimple() {
        // given
        var fixture = new Rule("noMatch",
                List.of(
                        new Condition("characterName", "!Thorin"),
                        new Condition("book", "The Hobbit")),
                Map.of("Weapon", "Axe"));
        // when
        var result = fixture.evaluate(bo);
        // then
        assertThat(result).isNotPresent();
    }
}
