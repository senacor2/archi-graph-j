package com.bmw.archigraph.model;

import lombok.Data;

import java.util.Map;

@Data
public class Application {

  private String id;

  private String name;

  private String componentName;

  private Component component;

  private Map<String, String> attributes;

  public Application(String id, String name, String componentName, String attr1, String attr2, String attr3) {
    this.id = id;
    this.name = name;
    this.componentName = componentName;
    attributes = Map.of("OS", attr1, "Cloud", attr2, "Expires", attr3);
  }

  public Application(String id, String name, String componentName) {
    this(id, name, componentName, "", "", "");
  }

  public String getAttribute(String key) {
    return attributes.get(key);
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
    return id.equals(((Application)other).id);
  }
}
