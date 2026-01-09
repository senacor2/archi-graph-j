package com.senacor.archigraph.rules;

import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Rule {

    /**
     * Name of the rule.
     */
    @Getter
    private final String name;

    /**
     * Conditions of the rule. All must evaluate to <code>true</code> or the rule will return no result.
     */
    private final List<Condition> conditions;

    /**
     * A key value map of results returned by the rule when all conditions evaluate to true.
     */
    @Getter
    private final Map<String, String> results;

    public Rule(String name, List<Condition> conditions, Map<String, String> results) {
        this.name = name;
        this.conditions = conditions;
        this.results = results;
    }

    Optional<Map<String, String>> evaluate(Object o) {
        for (var c : conditions) {
            if (!c.match(o)) return Optional.empty();
        }
        return Optional.of(results);
    }

}
