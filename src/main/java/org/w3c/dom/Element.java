package org.w3c.dom;

public interface Element extends Node {
    String getAttribute(String paramString);

    Attr getAttributeNode(String paramString);

    NodeList getElementsByTagName(String paramString);

    String getTagName();

    void normalize();

    void removeAttribute(String paramString) throws DOMException;

    Attr removeAttributeNode(Attr paramAttr) throws DOMException;

    void setAttribute(String paramString1, String paramString2) throws DOMException;

    Attr setAttributeNode(Attr paramAttr) throws DOMException;
}
