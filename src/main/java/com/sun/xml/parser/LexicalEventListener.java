package com.sun.xml.parser;

import org.xml.sax.DocumentHandler;
import org.xml.sax.SAXException;

public interface LexicalEventListener extends DocumentHandler {
    void comment(String paramString) throws SAXException;

    void endCDATA() throws SAXException;

    void endParsedEntity(String paramString, boolean paramBoolean) throws SAXException;

    void startCDATA() throws SAXException;

    void startParsedEntity(String paramString) throws SAXException;
}
