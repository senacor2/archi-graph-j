package com.senacor.archigraph.rules;

public class BusinessObject implements ObjectWithAttributes{

    private final String characterName;

    public BusinessObject() {
        this.characterName = "";
    }
    public BusinessObject(String name) {
        this.characterName = name;
    }

    public String getCharacterName() {
        return characterName;
    }

    public String getAttribute(final String attributeName) {
        if ("characterName".equals(attributeName)) return characterName;
        else throw new IllegalArgumentException("Unknown attribute: " + attributeName);
    }
}
