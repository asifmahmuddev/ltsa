package org.w3c.dom;

public interface Entity extends Node {
    String getNotationName();

    String getPublicId();

    String getSystemId();
}
