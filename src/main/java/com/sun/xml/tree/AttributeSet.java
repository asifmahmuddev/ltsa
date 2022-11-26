package com.sun.xml.tree;

import com.sun.xml.parser.AttributeListEx;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Vector;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.AttributeList;

final class AttributeSet implements NamedNodeMap, XmlWritable {
    private boolean readonly;
    private Vector list;
    private ElementNode nameScope;

    AttributeSet(ElementNode paramElementNode) {
        this.list = new Vector(5);
        this.nameScope = paramElementNode;
    }

    AttributeSet(AttributeSet paramAttributeSet, boolean paramBoolean) {
        int i = paramAttributeSet.getLength();
        this.list = new Vector(i);
        for (byte b = 0; b < i; b++) {
            Node node = paramAttributeSet.item(b);
            if (!(node instanceof AttributeNode))
                throw new IllegalArgumentException(((NodeBase) node).getMessage("A-003"));
            node = node.cloneNode(paramBoolean);
            ((AttributeNode) node).setNameScope(null);
            this.list.addElement(node);
        }
    }

    AttributeSet(AttributeList paramAttributeList) throws DOMException {
        int i = paramAttributeList.getLength();
        AttributeListEx attributeListEx = null;
        this.list = new Vector(i);
        if (paramAttributeList instanceof AttributeListEx)
            attributeListEx = (AttributeListEx) paramAttributeList;
        for (byte b = 0; b < i; b++)
            this.list.addElement(new AttributeNode(paramAttributeList.getName(b), paramAttributeList.getValue(b), (attributeListEx == null) ? true : attributeListEx.isSpecified(b),
                (attributeListEx == null) ? null : attributeListEx.getDefault(b)));
        this.list.trimToSize();
    }

    void trimToSize() {
        this.list.trimToSize();
    }

    public void setReadonly() {
        this.readonly = true;
        for (byte b = 0; b < this.list.size(); b++)
            ((AttributeNode) this.list.elementAt(b)).setReadonly(true);
    }

    public boolean isReadonly() {
        if (this.readonly)
            return true;
        for (byte b = 0; b < this.list.size(); b++) {
            if (((AttributeNode) this.list.elementAt(b)).isReadonly())
                return true;
        }
        return false;
    }

    void setNameScope(ElementNode paramElementNode) {
        if (paramElementNode != null && this.nameScope != null)
            throw new IllegalStateException(paramElementNode.getMessage("A-004"));
        this.nameScope = paramElementNode;
        int i = this.list.size();
        for (byte b = 0; b < i; b++) {
            AttributeNode attributeNode = this.list.elementAt(b);
            attributeNode.setNameScope(null);
            attributeNode.setNameScope(paramElementNode);
        }
    }

    ElementNode getNameScope() {
        return this.nameScope;
    }

    String getValue(String paramString) {
        Attr attr = (Attr) getNamedItem(paramString);
        if (attr == null)
            return "";
        return attr.getValue();
    }

    public Node getNamedItem(String paramString) {
        int i = this.list.size();
        for (byte b = 0; b < i; b++) {
            Node node = item(b);
            if (node.getNodeName().equals(paramString))
                return node;
        }
        return null;
    }

    public int getLength() {
        return this.list.size();
    }

    public Node item(int paramInt) {
        if (paramInt < 0 || paramInt >= this.list.size())
            return null;
        return this.list.elementAt(paramInt);
    }

    public Node removeNamedItem(String paramString) throws DOMException {
        int i = this.list.size();
        if (this.readonly)
            throw new DomEx((short) 7);
        for (byte b = 0; b < i; b++) {
            Node node = item(b);
            if (node.getNodeName().equals(paramString)) {
                AttributeNode attributeNode = (AttributeNode) node;
                if (attributeNode.getDefaultValue() != null) {
                    attributeNode = new AttributeNode(attributeNode);
                    attributeNode.setOwnerDocument((XmlDocument) this.nameScope.getOwnerDocument());
                    this.list.setElementAt(attributeNode, b);
                } else {
                    this.list.removeElementAt(b);
                }
                attributeNode.setNameScope(null);
                return node;
            }
        }
        throw new DomEx((short) 8);
    }

    public Node setNamedItem(Node paramNode) throws DOMException {
        if (this.readonly)
            throw new DomEx((short) 7);
        if (!(paramNode instanceof AttributeNode) || paramNode.getOwnerDocument() != this.nameScope.getOwnerDocument())
            throw new DomEx((short) 4);
        AttributeNode attributeNode = (AttributeNode) paramNode;
        if (attributeNode.getNameScope() != null)
            throw new DomEx((short) 10);
        int i = this.list.size();
        for (byte b = 0; b < i; b++) {
            AttributeNode attributeNode1 = (AttributeNode) item(b);
            if (attributeNode1.getNodeName().equals(paramNode.getNodeName())) {
                if (attributeNode1.isReadonly())
                    throw new DomEx((short) 7);
                attributeNode.setNameScope(this.nameScope);
                this.list.setElementAt(paramNode, b);
                attributeNode1.setNameScope(null);
                return attributeNode1;
            }
        }
        attributeNode.setNameScope(this.nameScope);
        this.list.addElement(paramNode);
        return null;
    }

    public void writeXml(XmlWriteContext paramXmlWriteContext) throws IOException {
        Writer writer = paramXmlWriteContext.getWriter();
        int i = this.list.size();
        for (byte b = 0; b < i; b++) {
            AttributeNode attributeNode = this.list.elementAt(b);
            if (attributeNode.getSpecified()) {
                writer.write(32);
                attributeNode.writeXml(paramXmlWriteContext);
            }
        }
    }

    public void writeChildrenXml(XmlWriteContext paramXmlWriteContext) throws IOException {
    }

    public String toString() {
        try {
            CharArrayWriter charArrayWriter = new CharArrayWriter();
            XmlWriteContext xmlWriteContext = new XmlWriteContext(charArrayWriter);
            writeXml(xmlWriteContext);
            return charArrayWriter.toString();
        } catch (IOException iOException) {
            return super.toString();
        }
    }
}
