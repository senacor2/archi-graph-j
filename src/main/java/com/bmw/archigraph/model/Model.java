package com.bmw.archigraph.model;

import com.bmw.archigraph.render.RenderModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Builder
@AllArgsConstructor
@Data
public class Model {

    private String name;
    private Map<String, Component> componentMap;
    private Map<String, Application> applicationMap;
    private Map<String, InformationFlow> informationFlowMap;
    private List<Component> l1Components;

    public Model() {}

    public Model components(List<Component> components) {
        l1Components = components;
        componentMap = components.stream()
                .flatMap(Component::flattened)
                .collect(Collectors.toMap(Component::getName, Function.identity()));
        for (Component c : components) {
            c.wireL1Component(c);
        }
        return this;
    }

    public Model applications(List<Application> applications) {
        assert componentMap != null;
        applicationMap = applications.stream()
                .peek(a -> a.setComponent(componentMap.get(a.getComponentName())))
                .collect(Collectors.toMap(Application::getId, Function.identity()));
        return this;
    }

    public Model informationFlows(List<InformationFlow> informationFlows) {
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
        return this;
    }


}
