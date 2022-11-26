package com.sun.xml.parser;

class ElementDecl {
    String name;
    String id;
    String contentType;
    ElementValidator validator;
    ContentModel model;
    boolean ignoreWhitespace;
    boolean isFromInternalSubset;
    SimpleHashtable attributes = new SimpleHashtable();

    ElementDecl(String paramString) {
        this.name = paramString;
    }
}
