package com.sun.xml.tree;

public interface NamespaceScoped extends NodeEx {
    String getLocalName();

    String getNamespace();

    String getPrefix();

    void setPrefix(String paramString);
}
