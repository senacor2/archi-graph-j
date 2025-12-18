package com.senacor.archigraph.model;

import lombok.Data;

import java.util.Map;

@Data
public class Application {

    // Attribute names
    public static final String MARKET = "Market";
    public static final String TARGET = "Target";
    public static final String REPLACE_TNR = "ReplaceTnr";

    private String id;

  private String name;

  private String componentName;

  private Component component;

  private Map<String, String> attributes;

  public Application(String id, String name, String componentName, String attr1, String attr2, String attr3) {
    this.id = id;
    this.name = name;
    this.componentName = componentName;
    attributes = Map.of(MARKET, attr1, TARGET, attr2, REPLACE_TNR, attr3);
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
