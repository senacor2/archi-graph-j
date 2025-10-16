package com.senacor.archigraph.validate;

import com.senacor.archigraph.model.Model;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;

/**
 * This validator checks the semantic integrity of the model.
 * Applications should have a
 */
@Slf4j
public class SemanticValidator {

    public List<ValidationIssue> validate(Model model) {
        log.debug("Starting semantic validation");
        List<ValidationIssue> result = new LinkedList<>();
        result.addAll(validateAppLinkage(model));
        result.addAll(validateAppCount(model));
        result.addAll(validateInformationFlows(model));
        log.debug("Semantic validation complete. {} issues found", result.size());
        return result;
    }

    public List<ValidationIssue> validateAppLinkage(Model model) {
        if (model.getApplicationMap() == null) return new LinkedList<>();
        return model.getApplicationMap().values().stream()
                .filter(a -> a.getComponent() == null)
                .map(a -> new ValidationIssue(a.getComponentName(), a.getId(),
                        String.format("Component %s for application %s does not exist", a.getComponentName(), a.getId())))
                .toList();
    }

    public List<ValidationIssue> validateAppCount(Model model) {
        if (model.getApplicationMap() == null) return new LinkedList<>();
        return model.getComponentMap().values().stream()
                .filter(c -> c.getApplications().size() > c.getAppWidth() * c.getAppHeight())
                .map(c -> new ValidationIssue(c.getName(), "",
                        String.format("Component %s has %d applications but has only room for %d", c.getName(),
                                c.getApplications().size(), c.getAppWidth() * c.getAppHeight())))
                .toList();
    }

    public List<ValidationIssue> validateInformationFlows(Model model) {
        if (model.getInformationFlowMap() == null) return new LinkedList<>();
        return model.getInformationFlowMap().values().stream()
                .filter(i -> i.getSource() == null || i.getDestination() == null)
                .map(i -> new ValidationIssue("", i.getId(),
                        String.format("Information flow %s cannot be built because one of the apps is missing", i.getId())))
                .toList();
    }

}
