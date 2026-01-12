package com.senacor.archigraph.model;

import com.senacor.archigraph.rules.ObjectWithAttributes;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Application implements ObjectWithAttributes {

    private String id;

    private String name;

    private String componentName;

    private Component component;

    private Map<String, String> attributes;

    public Application(String id, String name, String componentName) {
        this.id = id;
        this.name = name;
        this.componentName = componentName;
        attributes = new HashMap<>(4);
    }

    public String getAttribute(String key) {
        return attributes.get(key);
    }

    public void setAttribute(String key, String value) {
        attributes.put(key, value);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null) return false;
        if (getClass() != other.getClass()) return false;
        return id.equals(((Application) other).id);
    }
}
