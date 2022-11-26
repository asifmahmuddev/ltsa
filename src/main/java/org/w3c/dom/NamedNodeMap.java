package org.w3c.dom;

public interface NamedNodeMap {
    int getLength();

    Node getNamedItem(String paramString);

    Node item(int paramInt);

    Node removeNamedItem(String paramString) throws DOMException;

    Node setNamedItem(Node paramNode) throws DOMException;
}
