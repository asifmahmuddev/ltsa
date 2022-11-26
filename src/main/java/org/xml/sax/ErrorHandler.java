package org.xml.sax;

public interface ErrorHandler {
    void error(SAXParseException paramSAXParseException) throws SAXException;

    void fatalError(SAXParseException paramSAXParseException) throws SAXException;

    void warning(SAXParseException paramSAXParseException) throws SAXException;
}
