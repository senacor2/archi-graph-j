package com.senacor.archigraph.rules;

/**
 * Objects which can be customized with a map of additional attributes.
 */
public interface ObjectWithAttributes {

    /**
     * Gets the named attribute.
     * @param attributeName Name of the attribute.
     * @return The string value stored at the attribute.
     * @throws IllegalArgumentException when the name is not known.
     */
    String getAttribute(final String attributeName);
}
