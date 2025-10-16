package com.senacor.archigraph.validate;

import com.senacor.archigraph.model.Model;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;

/**
 * This validator checks the semantic integrity of the model.
 * Applications should have a
 */
@Slf4j
public class SemanticValidator {

    public List<ValidationIssue> validate(Model model) {
        log.debug("Starting semantic validation");
        List<ValidationIssue> result;
        result = validateAppLinkage(model);
        result.addAll(validateAppCount(model));
        log.debug("Semantic validation complete. {} issues found", result.size());
        return result;
    }

    public List<ValidationIssue> validateAppLinkage(Model model) {
        if (model.getApplicationMap() == null) return Collections.emptyList();
        return model.getApplicationMap().values().stream()
                .filter(a -> a.getComponent() == null)
                .map(a -> new ValidationIssue(a.getComponentName(), a.getId(),
                        String.format("Component %s for application %s does not exist", a.getComponentName(), a.getId())))
                .toList();
    }

    public List<ValidationIssue> validateAppCount(Model model) {
        if (model.getApplicationMap() == null) return Collections.emptyList();
        return model.getComponentMap().values().stream()
                .filter(c -> c.getApplications().size() > c.getAppWidth() * c.getAppHeight())
                .map(c -> new ValidationIssue(c.getName(), "",
                        String.format("Component %s has too many applications", c.getName())))
                .toList();
    }

}
