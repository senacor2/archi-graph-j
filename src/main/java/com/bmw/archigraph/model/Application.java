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
  public String getAttribute(String key) {
    return attributes.get(key);
  }
}
