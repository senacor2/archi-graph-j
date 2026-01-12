package com.senacor.archigraph.rules;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static java.util.stream.Collectors.toMap;

/**
 * A rule base is a sequence of rules and can be loaded from a CSV file.
 * The rules are evaluated in the order in which they are defined. The first rule that matches
 * returns the result.
 */
@Slf4j
public class RuleBase {

    public static final String RESULT_PREFIX = "result.";
    public static final String RULE_NAME = "Name";

    private final List<Rule> rules = new LinkedList<>();

    /**
     * Creates an empty rule base which must be loaded subsequently.
     */
    public RuleBase() {}

    /**
     * Creates a rulebase with exactly one rule. This constructor is used to create default rules.
     * The rulebase has "all wildcards" conditions and exactly one result set.
     * @param attributeNames a list of attribute names the rule base shall match against wildcards
     * @param results The result map of the rule base.
     */
    public RuleBase(List<String> attributeNames, Map<String, String> results) {
        rules.add(new Rule("catch all",
                attributeNames.stream()
                        .map(an -> new Condition(an, "*"))
                        .toList(),
                results));
    }
    /**
     * Loads a rule base from a CSV file.
     * @param filename Name of the rule base file.
     * @throws java.io.IOException when the file could not be read.
     */
    public void load(String filename) throws IOException {
        log.info("Loading rule base {}", filename);
        try (java.io.Reader in = new FileReader(filename)) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .get()
                    .parse(in);
            for (var record : records) {
                rules.add(parseRule(record));
            }
        }
    }

    public int size() {
        return rules.size();
    }

    private Rule parseRule(CSVRecord record) {
        var headers = record.getParser().getHeaderNames();
        String name = record.get(RULE_NAME);
        List<Condition> conds = new LinkedList<>();
        Map<String, String> results = new HashMap<>();
        for (var columnName : headers) {
            if (columnName.equals(RULE_NAME)) {
                continue;
            } else if (columnName.startsWith(RESULT_PREFIX)) {
                results.put(columnName.replace(RESULT_PREFIX, ""), record.get(columnName));
            } else {
                var expression = record.get(columnName);
                conds.add(new Condition(columnName.replace("cond", ""), expression));
            }
        }
        return new Rule(name, conds, results);
    }

    /**
     * Evaluate all rules in the rule base and return the results from the first matching rule.
     * @param o Business object to evaluate against the rule base.
     * @return A map of key/value pairs with the results. The optional is empty when no rule matches.
     */
    public Optional<Map<String, String>> evaluate(ObjectWithAttributes o) {
        for (var r : rules) {
            var result = r.evaluate(o);
            if (result.isPresent()) return result;
        }
        return Optional.empty();
    }

    /**
     * Returns a Map keyed by rule name with the result map of the rule as the value.
     * When there are multiple rules with the same name (which makes sense when you want to <i>or</i> rules)
     * then only the results of the first map are returned. In this case however it would be wise if all rules
     * with the same name should have the same result.
     * @return A map keyed by rule name with the result map as the value.
     */
    public Map<String, Map<String, String>> getNamedResultMap() {
        return rules.stream()
                .collect(toMap(Rule::getName, Rule::getResults, (first, _) -> first));
    }
}
