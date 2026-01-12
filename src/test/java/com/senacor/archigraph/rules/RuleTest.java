package com.senacor.archigraph.rules;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class RuleTest {

    final BusinessObject bo = new BusinessObject("Thorin");

    @Test
    void testRuleMatchSimple() {
        // given
        var fixture = new Rule("simple", List.of(new Condition("characterName", "Thorin")),
                Map.of("Weapon", "Axe"));
        // when
        var result = fixture.evaluate(bo);
        // then
        assertThat(result).isPresent();
        assertThat(result.get()).hasSize(1);
        assertThat(result.get()).containsEntry("Weapon", "Axe");
    }

    @Test
    void testRuleNoMatchSimple() {
        // given
        var fixture = new Rule("noMatch", List.of(new Condition("characterName", "!Thorin")),
                Map.of("Weapon", "Axe"));
        // when
        var result = fixture.evaluate(bo);
        // then
        assertThat(result).isNotPresent();
    }
}
