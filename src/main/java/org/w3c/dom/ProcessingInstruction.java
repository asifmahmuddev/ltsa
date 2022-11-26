package org.w3c.dom;

public interface ProcessingInstruction extends Node {
    String getData();

    String getTarget();

    void setData(String paramString) throws DOMException;
}
