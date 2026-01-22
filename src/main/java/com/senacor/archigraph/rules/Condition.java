package com.senacor.archigraph.rules;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Defines a single expression applied on an attribute of the business object.
 */
@Slf4j
public class Condition {

    /**
     * Name of the attribute to check.
     */
    private final String attributeName;

    private final Operator operator;

    private final String value;

    /**
     * Creates a new condition.
     *
     * @param attributeName Name of the attribute to get from the business object.
     * @param expression    A string with a logical expression. The expression can be
     *                      <ul>
     *                      <li><i>*</i> which matches all values of the business attribute.</li>
     *                      <li>a string value. The value must match equally with the business object attribute.</li>
     *                      <li><i>!</i> followed by a string value. The value must <b>not</b> be equal to the business object attribute.</li>
     *                      </ul>
     */
    public Condition(final String attributeName, final String expression) {
        this.attributeName = attributeName;
        this.operator = parseOperator(expression);
        this.value = parseValue(expression);
    }

    private Operator parseOperator(final String expression) {
        if (expression.equals("*")) return Operator.WILDCARD;
        else if (expression.startsWith("!")) return Operator.NOT_EQUAL;
        else return Operator.EQUAL;
    }

    private String parseValue(final String expression) {
        if (expression.equals("*")) return "";
        else if (expression.startsWith("!")) return expression.replaceFirst("^!", "");
        else return expression;
    }

    public boolean match(ObjectWithAttributes o, Map<String, String> context) {
        if (context.containsKey(attributeName)) {
            return evaluate(context.get(attributeName));
        } else {
            return evaluate(o.getAttribute(attributeName));
        }
    }

    private boolean evaluate(String propertyValue) {
        return switch (operator) {
            case Operator.WILDCARD -> true;
            case Operator.EQUAL -> match(value, propertyValue);
            case Operator.NOT_EQUAL -> !match(value, propertyValue);
        };
    }

    private static boolean match(String conditionValue, String propertyValue) {
        if (conditionValue.contains("*")) {
            return propertyValue.matches(conditionValue.replace("*", ".*"));
        } else {
            return conditionValue.equals(propertyValue);
        }
    }
}
