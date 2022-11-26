package org.w3c.dom;

public interface DocumentType extends Node {
    NamedNodeMap getEntities();

    String getName();

    NamedNodeMap getNotations();
}
