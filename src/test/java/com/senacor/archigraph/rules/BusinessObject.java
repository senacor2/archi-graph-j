package com.senacor.archigraph.rules;

public class BusinessObject {

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
}
