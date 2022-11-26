package com.sun.xml.tree;

import org.xml.sax.SAXException;

public interface XmlReadable {
    void doneChild(NodeEx paramNodeEx, ParseContext paramParseContext) throws SAXException;

    void doneParse(ParseContext paramParseContext) throws SAXException;

    void startParse(ParseContext paramParseContext) throws SAXException;
}
