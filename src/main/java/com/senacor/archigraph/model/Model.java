package com.senacor.archigraph.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Builder
@AllArgsConstructor
@Getter
public class Model {

    @Setter
    private String name;
    private Map<String, Component> componentMap;
    private Map<String, Application> applicationMap;
    private Map<String, InformationFlow> informationFlowMap;
    private List<Component> l1Components;

    public Model() {}

    public void setL1Components(List<Component> components) {
        l1Components = components;
        componentMap = components.stream()
                .flatMap(Component::flattened)
                .collect(Collectors.toMap(Component::getName, Function.identity()));
        for (Component c : components) {
            c.wireL1Component(c);
        }
        for (Component c : components) {
            c.wireParent(null);
        }
    }

    public void setApplications(List<Application> applications) {
        assert componentMap != null;
        applicationMap = applications.stream()
                .peek(a -> {
                    // TODO handle unmapped component
                    var c = componentMap.get(a.getComponentName());
                    a.setComponent(c);
                    c.addApplication(a);
                })
                .collect(Collectors.toMap(Application::getId, Function.identity()));
    }

    public void setInformationFlows(List<InformationFlow> informationFlows) {
        assert applicationMap != null;
        informationFlowMap = informationFlows.stream()
                .peek(i -> {
                    i.setSource(applicationMap.get(i.getSourceId()));
                    i.setDestination(applicationMap.get(i.getDestId()));
                })
                .collect(Collectors.toMap(InformationFlow::getId, Function.identity()));
        for (var c : componentMap.values()) {
            c.selectInformationFlows(informationFlows);
        }
    }


}
