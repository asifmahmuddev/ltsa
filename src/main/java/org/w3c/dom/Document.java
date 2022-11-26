package org.w3c.dom;

public interface Document extends Node {
    Attr createAttribute(String paramString) throws DOMException;

    CDATASection createCDATASection(String paramString) throws DOMException;

    Comment createComment(String paramString);

    DocumentFragment createDocumentFragment();

    Element createElement(String paramString) throws DOMException;

    EntityReference createEntityReference(String paramString) throws DOMException;

    ProcessingInstruction createProcessingInstruction(String paramString1, String paramString2) throws DOMException;

    Text createTextNode(String paramString);

    DocumentType getDoctype();

    Element getDocumentElement();

    NodeList getElementsByTagName(String paramString);

    DOMImplementation getImplementation();
}
