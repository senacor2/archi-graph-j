package com.senacor.archigraph.rules;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RuleBaseTest {

    private final BusinessObject bo = new BusinessObject("Thorin");

    @Test
    void testLoadSimpleRuleBase() throws IOException {
        // given
        var fixture = new RuleBase();
        // when
        fixture.load("src/test/resources/singlerule.csv");
        // then
        assertEquals(1, fixture.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "src/test/resources/singlerule.csv",
            "src/test/resources/firstmatch.csv",
            "src/test/resources/wildcard.csv",
            "src/test/resources/nextmatch.csv"
    })
    void evaluateSimpleRuleBase(String filename) throws IOException {
        // given
        var fixture = new RuleBase();
        // when
        fixture.load(filename);
        var result = fixture.evaluate(bo);
        // then
        assertThat(result).isPresent();
        assertThat(result.get()).hasSize(1);
        assertThat(result.get()).containsEntry("Weapon", "Axe");
    }

    @Test
    void evaluateNoMatch() throws IOException {
        // given
        var fixture = new RuleBase();
        // when
        fixture.load("src/test/resources/nomatch.csv");
        var result = fixture.evaluate(bo);
        // then
        assertThat(result).isNotPresent();
    }
}
