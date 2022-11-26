package org.w3c.dom;

public interface Attr extends Node {
    String getName();

    boolean getSpecified();

    String getValue();

    void setValue(String paramString);
}
