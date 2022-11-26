package org.xml.sax;

public interface DocumentHandler {
    void characters(char[] paramArrayOfchar, int paramInt1, int paramInt2) throws SAXException;

    void endDocument() throws SAXException;

    void endElement(String paramString) throws SAXException;

    void ignorableWhitespace(char[] paramArrayOfchar, int paramInt1, int paramInt2) throws SAXException;

    void processingInstruction(String paramString1, String paramString2) throws SAXException;

    void setDocumentLocator(Locator paramLocator);

    void startDocument() throws SAXException;

    void startElement(String paramString, AttributeList paramAttributeList) throws SAXException;
}
