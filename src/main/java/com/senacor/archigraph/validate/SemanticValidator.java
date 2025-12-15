package com.senacor.archigraph.validate;

import com.senacor.archigraph.model.Model;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;

/**
 * This validator checks the semantic integrity of the model.
 * Applications must have a component, information flows must have two apps and the number of apps in one
 * component must be smaller than the sizes of the component.
 */
@Slf4j
public class SemanticValidator {

    /**
     * Check the model for semantic errors.
     * @param model Model to be checked.
     * @param lenientComp skip the check for missing components.
     * @param lenientFlow skip the check for apps referenced in information flows but missing in the model.
     * @return A possibly empty list of validation errors.
     */
    public List<ValidationIssue> validate(Model model, boolean lenientComp, boolean lenientFlow) {
        log.debug("Starting semantic validation");
        List<ValidationIssue> result = new LinkedList<>();
        result.addAll(validateAppLinkage(model, lenientComp));
        result.addAll(validateAppCount(model));
        result.addAll(validateInformationFlows(model, lenientFlow));
        log.debug("Semantic validation complete. {} issues found", result.size());
        return result;
    }

    /**
     * Check if each app in the model points to one parent component which is also part of the model.
     * @param model Model to be checked.
     * @param lenientComp if false, this check is skipped.
     * @return a possibly empty list of validation errors.
     */
    public List<ValidationIssue> validateAppLinkage(Model model, boolean lenientComp) {
        if (lenientComp || model.getApplicationMap() == null) return new LinkedList<>();
        return model.getApplicationMap().values().stream()
                .filter(a -> a.getComponent() == null)
                .map(a -> new ValidationIssue(a.getComponentName(), a.getId(),
                        String.format("Component %s for application %s does not exist", a.getComponentName(), a.getId())))
                .toList();
    }

    /**
     * Validate that the number of apps directly contained in a component is smaller than the row*columns of
     * said component (which may also be checked against the app are size).
     * @param model Model to be checked.
     * @return a possibly empty list of validation error messages.
     */
    public List<ValidationIssue> validateAppCount(Model model) {
        if (model.getApplicationMap() == null) return new LinkedList<>();
        return model.getComponentMap().values().stream()
                .filter(c -> c.getApplications().size() > c.getAppWidth() * c.getAppHeight())
                .map(c -> new ValidationIssue(c.getName(), "",
                        String.format("Component %s has %d applications but has only room for %d", c.getName(),
                                c.getApplications().size(), c.getAppWidth() * c.getAppHeight())))
                .toList();
    }

    /**
     * Validate that the source and destination app referenced by the flow exist in the model.
     * @param model Model to be checked.
     * @param lenientFlow skip the check if false.
     * @return a possibly empty list of validation error messages.
     */
    public List<ValidationIssue> validateInformationFlows(Model model, boolean lenientFlow) {
        if (lenientFlow || model.getInformationFlowMap() == null) return new LinkedList<>();
        return model.getInformationFlowMap().values().stream()
                .filter(i -> i.getSource() == null || i.getDestination() == null)
                .map(i -> new ValidationIssue("", i.getId(),
                        String.format("Information flow %s cannot be built because one of the apps is missing", i.getId())))
                .toList();
    }

}
