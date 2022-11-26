package com.sun.xml.tree;

import org.w3c.dom.CharacterData;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

abstract class DataNode extends NodeBase implements CharacterData {
    char[] data;
    static NodeListImpl childNodes = new NodeListImpl();

    DataNode() {
    }

    DataNode(char[] paramArrayOfchar, int paramInt1, int paramInt2) {
        this.data = new char[paramInt2];
        System.arraycopy(paramArrayOfchar, paramInt1, this.data, 0, paramInt2);
    }

    DataNode(String paramString) {
        if (paramString != null) {
            this.data = new char[paramString.length()];
            paramString.getChars(0, this.data.length, this.data, 0);
        } else {
            this.data = new char[0];
        }
    }

    public char[] getText() {
        return this.data;
    }

    public void setText(char[] paramArrayOfchar) {
        this.data = paramArrayOfchar;
    }

    public String toString() {
        return new String(this.data);
    }

    public String getData() {
        return toString();
    }

    public void setData(String paramString) {
        if (isReadonly())
            throw new DomEx((short) 7);
        if (paramString == null) {
            setText(new char[0]);
        } else {
            setText(paramString.toCharArray());
        }
    }

    public int getLength() {
        return (this.data == null) ? 0 : this.data.length;
    }

    public String substringData(int paramInt1, int paramInt2) throws DOMException {
        if (paramInt1 < 0 || paramInt1 > this.data.length || paramInt2 < 0)
            throw new DomEx((short) 1);
        paramInt2 = Math.min(paramInt2, this.data.length - paramInt1);
        return new String(this.data, paramInt1, paramInt2);
    }

    public void appendData(String paramString) {
        if (isReadonly())
            throw new DomEx((short) 7);
        int i = paramString.length();
        char[] arrayOfChar = new char[i + this.data.length];
        System.arraycopy(this.data, 0, arrayOfChar, 0, this.data.length);
        paramString.getChars(0, i, arrayOfChar, this.data.length);
        this.data = arrayOfChar;
    }

    public void insertData(int paramInt, String paramString) throws DOMException {
        if (isReadonly())
            throw new DomEx((short) 7);
        if (paramInt < 0 || paramInt >= this.data.length)
            throw new DomEx((short) 1);
        int i = paramString.length();
        char[] arrayOfChar = new char[i + this.data.length];
        System.arraycopy(this.data, 0, arrayOfChar, 0, paramInt);
        paramString.getChars(0, i, arrayOfChar, paramInt);
        System.arraycopy(this.data, paramInt, arrayOfChar, paramInt + i, this.data.length - paramInt);
        this.data = arrayOfChar;
    }

    public void deleteData(int paramInt1, int paramInt2) throws DOMException {
        if (isReadonly())
            throw new DomEx((short) 7);
        if (paramInt1 < 0 || paramInt1 >= this.data.length || paramInt2 < 0)
            throw new DomEx((short) 1);
        paramInt2 = Math.min(paramInt2, this.data.length - paramInt1);
        char[] arrayOfChar = new char[this.data.length - paramInt2];
        System.arraycopy(this.data, 0, arrayOfChar, 0, paramInt1);
        System.arraycopy(this.data, paramInt1 + paramInt2, arrayOfChar, paramInt1, arrayOfChar.length - paramInt1);
        this.data = arrayOfChar;
    }

    public void replaceData(int paramInt1, int paramInt2, String paramString) throws DOMException {
        if (isReadonly())
            throw new DomEx((short) 7);
        if (paramInt1 < 0 || paramInt1 >= this.data.length || paramInt2 < 0)
            throw new DomEx((short) 1);
        if (paramInt1 + paramInt2 >= this.data.length) {
            deleteData(paramInt1, paramInt2);
            appendData(paramString);
        } else if (paramString.length() == paramInt2) {
            paramString.getChars(0, paramString.length(), this.data, paramInt1);
        } else {
            char[] arrayOfChar = new char[this.data.length + paramString.length() - paramInt2];
            System.arraycopy(this.data, 0, arrayOfChar, 0, paramInt1);
            paramString.getChars(0, paramString.length(), arrayOfChar, paramInt1);
            System.arraycopy(this.data, paramInt1 + paramInt2, arrayOfChar, paramInt1 + paramString.length(), this.data.length - paramInt1 + paramInt2);
            this.data = arrayOfChar;
        }
    }

    public NodeList getChildNodes() {
        return childNodes;
    }

    public String getNodeValue() {
        return getData();
    }

    public void setNodeValue(String paramString) {
        setData(paramString);
    }

    static final class NodeListImpl implements NodeList {
        public Node item(int param1Int) {
            return null;
        }

        public int getLength() {
            return 0;
        }
    }
}
