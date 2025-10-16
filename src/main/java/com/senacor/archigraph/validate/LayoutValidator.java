package com.senacor.archigraph.validate;

import com.senacor.archigraph.model.Component;
import com.senacor.archigraph.model.Model;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * This validator checks that the layout constraints are met. Components must not overlap,
 * L1 components must have some space for proxies around them and sub-components must fit
 * entirely into the enclosing component.
 */
@Slf4j
public class LayoutValidator {

    /**
     * Check the layout rules for the model. Components must not overlap and must be properly spaced.
     * @param model to validate
     * @return A possibly empty list of issues in this model.
     */
    public List<ValidationIssue> validate(Model model) {
        log.debug("Starting layout validation");
        List<ValidationIssue> result = new LinkedList<>();
        Component[] components = new Component[model.getL1Components().size()];
        model.getL1Components().toArray(components);
        for (int i = 0; i < components.length; i++) {
            result.addAll(validateContainment(components[i]));
            for (int j = i+1; j < components.length; j++){
                var issue = validateNoOverlap(components[i], components[j]);
                issue.ifPresent(result::add);
                issue = validateSpacing(components[i], components[j]);
                issue.ifPresent(result::add);
            }
        }
        log.debug("Layout validation complete. {} issues found", result.size());
        return result;
    }

    /**
     * Check if all subcomponents are fully enclosed in the component. Traverse recursively.
     * @param component The component to be checked.
     * @return A list of layout violations.
     */
    private Collection<ValidationIssue> validateContainment(Component component) {
        List<ValidationIssue> result = new LinkedList<>();
        for (var c : component.getComponents()) {
            if (!component.getCompArea().contains(c.getCompArea().shifted(component.getRow() + 1, component.getCol()))) {
                result.add(new ValidationIssue(c.getName(), null,
                        String.format("Component %s is not fully contained in %s", c.getName(), component.getName())));
            }
            result.addAll(validateContainment(c));
        }
        return result;
    }

    /**
     * Check if the components do not overlap and there are two cells of empty space between
     * the components. This check can only be executed for L1 components.
     * @param c1 first l1 component to be checked
     * @param c2 second l1 component to be checked.
     * @return A validation issue if the components overlap
     */
    private Optional<ValidationIssue> validateNoOverlap(Component c1, Component c2) {
        if (c1.getCompArea().overlap(c2.getCompArea())) {
            return Optional.of(new ValidationIssue(c1.getName(), null,
                    String.format("Components %s and %s overlap", c1.getName(), c2.getName())));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Check if the components are separated by at least 2 cells in every direction.
     * @param c1 first l1 component to check.
     * @param c2 second l1 component to check.
     * @return A validation issue if the components are not properly spaced.
     */
    private Optional<ValidationIssue> validateSpacing(Component c1, Component c2) {
        final int MINDISTANCE = 2;
        if (c1.getCompArea().hasMinDistance(c2.getCompArea(), MINDISTANCE)) {
            return Optional.empty();
        } else {
            return Optional.of(new ValidationIssue(c1.getName(), null,
                    String.format("Components %s and %s should be separated by at least %d cells",
                    c1.getName(), c2.getName(), MINDISTANCE)));
        }
    }

}
