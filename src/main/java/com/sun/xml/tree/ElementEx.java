package com.sun.xml.tree;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;

public interface ElementEx extends Element, NodeEx, NamespaceScoped, XmlReadable {
    String getAttribute(String paramString1, String paramString2);

    Attr getAttributeNode(String paramString1, String paramString2);

    String getIdAttributeName();

    Object getUserObject();

    void setUserObject(Object paramObject);

    void trimToSize();
}
