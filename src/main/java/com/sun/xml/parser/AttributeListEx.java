package com.sun.xml.parser;

import org.xml.sax.AttributeList;

public interface AttributeListEx extends AttributeList {
    String getDefault(int paramInt);

    String getIdAttributeName();

    boolean isSpecified(int paramInt);
}
