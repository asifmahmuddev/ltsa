package com.sun.xml.tree;

import org.w3c.dom.Node;

public interface NodeEx extends Node, XmlWritable {
    int getIndexOf(Node paramNode);

    String getInheritedAttribute(String paramString);

    String getInheritedAttribute(String paramString1, String paramString2);

    String getLanguage();

    boolean isReadonly();

    void setReadonly(boolean paramBoolean);
}
