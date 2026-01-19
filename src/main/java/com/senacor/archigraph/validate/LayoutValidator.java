package com.senacor.archigraph.validate;

import com.senacor.archigraph.model.Component;
import com.senacor.archigraph.model.L1Component;
import com.senacor.archigraph.model.Model;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        L1Component[] l1components = new L1Component[model.getL1Components().size()];
        model.getL1Components().toArray(l1components);
        if (model.getComponentNames().size() > 4) {
            result.add(new ValidationIssue(null, null,
                    String.format("Maximum component nesting is 4 but is actually %d", model.getComponentNames().size())));
        }
        for (int i = 0; i < l1components.length; i++) {
            result.addAll(validateContainment(l1components[i]));
            validateProxySpace(l1components[i]).ifPresent(result::add);
            validateNesting(l1components[i], model.getComponentNames().size()).ifPresent(result::add);
            for (int j = i+1; j < l1components.length; j++){
                validateNoOverlap(l1components[i], l1components[j]).ifPresent(result::add);
                validateSpacing(l1components[i], l1components[j]).ifPresent(result::add);
            }
        }
        for (Component c : model.getComponentMap().values()) {
            var issue = validateAppArea(c);
            issue.ifPresent(result::add);
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
    private Optional<ValidationIssue> validateSpacing(L1Component c1, L1Component c2) {
        final int MINDISTANCE = c1.getProxyAreaSize() + c2.getProxyAreaSize();
        if (c1.getCompArea().hasMinDistance(c2.getCompArea(), MINDISTANCE)) {
            return Optional.empty();
        } else {
            return Optional.of(new ValidationIssue(c1.getName(), null,
                    String.format("Components %s and %s should be separated by at least %d cells",
                    c1.getName(), c2.getName(), MINDISTANCE)));
        }
    }

    /**
     * Check if a component with subcomponents and applications has an app area.
     * @param c the Component to check.
     * @return A validation issue if the component has no place for apps
     */
    private Optional<ValidationIssue> validateAppArea(Component c) {
        if (!c.getComponents().isEmpty() && !c.getApplications().isEmpty() && !c.isAppAreaOverride()) {
            return Optional.of(new ValidationIssue(c.getName(), null,
                    String.format("Component %s has subcomponents and applications but no AppArea",
                            c.getName())));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Check if an L1 component is big enough for all proxies needed.
     * @param comp An L1 component.
     * @return A validation issue if the number of proxies exceeds the space available around the component.
     */
    private Optional<ValidationIssue> validateProxySpace(L1Component comp) {
        final int proxyBoxSize = proxyBoxSize(comp);
        final int nbrProxies = comp.getCrossL1CompInformationFlows().stream()
                .map(flow -> {
                    if (flow.getSource().getComponent().getL1Component() == comp) return flow.getDestination();
                    else return flow.getSource();
                })
                .collect(Collectors.toSet())
                .size();
        if (nbrProxies > proxyBoxSize) {
            return Optional.of(new ValidationIssue(comp.getName(), null,
                    String.format("Component %s needs space for %d proxies and has only space for %d",
                            comp.getName(), nbrProxies, proxyBoxSize)));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Calculate the number of positions in the proxy box.
     * This is the size of the proxy box minus the size of the component
     * @param comp L1Component to check
     * @return the number of proxies that can be placed around the component.
     */
    private int proxyBoxSize(L1Component comp) {
        return (comp.getWidth() + 2*comp.getProxyAreaSize()) * (comp.getHeight() + 2*comp.getProxyAreaSize())
                - comp.getWidth() * comp.getHeight();
    }

    /**
     * Check if the component nesting is not more than 4.
     * Higher nestings will break the indentation of components relative to the parent.
     * @param comp a component to validate
     * @return A validation issue if the nesting is too deep.
     */
    private Optional<ValidationIssue> validateNesting(final Component comp, final int maxNesting) {
        if (comp.getLevel() > maxNesting) {
            return Optional.of(new ValidationIssue(comp.getName(), null,
                    String.format("Component %s is nested by more than %d levels (%d)", comp.getName(), maxNesting,
                            comp.getLevel())));
        } else {
            return Optional.empty();
        }
    }

}
