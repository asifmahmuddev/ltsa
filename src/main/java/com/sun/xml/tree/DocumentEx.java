package com.sun.xml.tree;

import java.util.Locale;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public interface DocumentEx extends Document, ElementFactory, XmlReadable, XmlWritable {
    void changeNodeOwner(Node paramNode);

    Locale chooseLocale(String[] paramArrayOfString);

    ElementEx getElementExById(String paramString);

    ElementFactory getElementFactory();

    Locale getLocale();

    String getSystemId();

    void setElementFactory(ElementFactory paramElementFactory);

    void setLocale(Locale paramLocale);
}
