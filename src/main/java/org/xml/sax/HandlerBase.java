package org.xml.sax;

public class HandlerBase implements EntityResolver, DTDHandler, DocumentHandler, ErrorHandler {
    public InputSource resolveEntity(String paramString1, String paramString2) throws SAXException {
        return null;
    }

    public void notationDecl(String paramString1, String paramString2, String paramString3) {
    }

    public void unparsedEntityDecl(String paramString1, String paramString2, String paramString3, String paramString4) {
    }

    public void setDocumentLocator(Locator paramLocator) {
    }

    public void startDocument() throws SAXException {
    }

    public void endDocument() throws SAXException {
    }

    public void startElement(String paramString, AttributeList paramAttributeList) throws SAXException {
    }

    public void endElement(String paramString) throws SAXException {
    }

    public void characters(char[] paramArrayOfchar, int paramInt1, int paramInt2) throws SAXException {
    }

    public void ignorableWhitespace(char[] paramArrayOfchar, int paramInt1, int paramInt2) throws SAXException {
    }

    public void processingInstruction(String paramString1, String paramString2) throws SAXException {
    }

    public void warning(SAXParseException paramSAXParseException) throws SAXException {
    }

    public void error(SAXParseException paramSAXParseException) throws SAXException {
    }

    public void fatalError(SAXParseException paramSAXParseException) throws SAXException {
        throw paramSAXParseException;
    }
}
