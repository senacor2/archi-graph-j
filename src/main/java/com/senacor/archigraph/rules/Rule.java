package com.senacor.archigraph.rules;

import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * A single rule inside the rule engine consisting of the rule name, condition fields and result fields.
 * When all conditions evaluate to true for a given object, the rule returns the result fields.
 */
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

    /**
     * Evaluate the rule against the object.
     * @param o an Object whose attribute names match the names in the rule conditions.
     * @return the rules result set if all rule conditions evaluate to true for the object.
     */
    Optional<Map<String, String>> evaluate(ObjectWithAttributes o) {
        return evaluate(o, Map.of());
    }

    /**
     * Evaluate the rule against an Object and a possibly empty context.
     * @param o An object whose attribute names match the names in the rule conditions.
     * @param context A possibly empty context of key-value pairs which is also matched against the rule.
     * @return the rules result set if all rule conditions evaluate to true for the object and the context.
     */
    Optional<Map<String, String>> evaluate(ObjectWithAttributes o, Map<String, String> context) {
        for (var c : conditions) {
            if (!c.match(o, context)) return Optional.empty();
        }
        return Optional.of(results);
    }

}
