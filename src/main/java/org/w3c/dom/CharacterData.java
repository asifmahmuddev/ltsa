package org.w3c.dom;

public interface CharacterData extends Node {
    void appendData(String paramString) throws DOMException;

    void deleteData(int paramInt1, int paramInt2) throws DOMException;

    String getData() throws DOMException;

    int getLength();

    void insertData(int paramInt, String paramString) throws DOMException;

    void replaceData(int paramInt1, int paramInt2, String paramString) throws DOMException;

    void setData(String paramString) throws DOMException;

    String substringData(int paramInt1, int paramInt2) throws DOMException;
}
