package com.sun.xml.parser;

import org.xml.sax.DTDHandler;
import org.xml.sax.SAXException;

public interface DtdEventListener extends DTDHandler {
    void attributeDecl(String paramString1, String paramString2, String paramString3, String[] paramArrayOfString, String paramString4, boolean paramBoolean1, boolean paramBoolean2)
        throws SAXException;

    void elementDecl(String paramString1, String paramString2) throws SAXException;

    void endDtd() throws SAXException;

    void externalDtdDecl(String paramString1, String paramString2) throws SAXException;

    void externalEntityDecl(String paramString1, String paramString2, String paramString3) throws SAXException;

    void internalDtdDecl(String paramString) throws SAXException;

    void internalEntityDecl(String paramString1, String paramString2) throws SAXException;

    void startDtd(String paramString) throws SAXException;
}
