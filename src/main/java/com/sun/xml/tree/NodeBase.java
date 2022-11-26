package com.sun.xml.tree;

import java.io.IOException;
import java.util.Locale;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

abstract class NodeBase implements Node, NodeEx, NodeList, XmlWritable {
    private ParentNode parent;
    private int parentIndex = -1;
    XmlDocument ownerDocument;
    boolean readonly;

    ParentNode getParentImpl() {
        return this.parent;
    }

    public boolean isReadonly() {
        return this.readonly;
    }

    public void setReadonly(boolean paramBoolean) {
        this.readonly = true;
        if (paramBoolean) {
            TreeWalker treeWalker = new TreeWalker(this);
            Node node;
            while ((node = treeWalker.getNext()) != null)
                ((NodeBase) node).setReadonly(false);
        }
    }

    public String getLanguage() {
        return getInheritedAttribute("xml:lang");
    }

    public String getInheritedAttribute(String paramString) {
        NodeBase nodeBase = this;
        Attr attr = null;
        ElementNode elementNode = (ElementNode) nodeBase;
        while (!(nodeBase instanceof ElementNode) || (attr = elementNode.getAttributeNode(paramString)) == null) {
            nodeBase = nodeBase.getParentImpl();
            if (nodeBase == null)
                break;
        }
        if (attr != null)
            return attr.getValue();
        return null;
    }

    public String getInheritedAttribute(String paramString1, String paramString2) {
        NodeBase nodeBase = this;
        Attr attr = null;
        ElementNode elementNode = (ElementNode) nodeBase;
        while (!(nodeBase instanceof ElementNode) || (attr = elementNode.getAttributeNode(paramString1, paramString2)) == null) {
            nodeBase = nodeBase.getParentImpl();
            if (nodeBase == null)
                break;
        }
        if (attr != null)
            return attr.getValue();
        return null;
    }

    public void writeChildrenXml(XmlWriteContext paramXmlWriteContext) throws IOException {
    }

    public Node getParentNode() {
        return this.parent;
    }

    void setParentNode(ParentNode paramParentNode, int paramInt) throws DOMException {
        if (this.parent != null && paramParentNode != null)
            this.parent.removeChild(this);
        this.parent = paramParentNode;
        this.parentIndex = paramInt;
    }

    void setOwnerDocument(XmlDocument paramXmlDocument) {
        this.ownerDocument = paramXmlDocument;
    }

    public Document getOwnerDocument() {
        return this.ownerDocument;
    }

    public boolean hasChildNodes() {
        return false;
    }

    public void setNodeValue(String paramString) {
        if (this.readonly)
            throw new DomEx((short) 7);
    }

    public String getNodeValue() {
        return null;
    }

    public Node getFirstChild() {
        return null;
    }

    public int getLength() {
        return 0;
    }

    public Node item(int paramInt) {
        return null;
    }

    public NodeList getChildNodes() {
        return this;
    }

    public Node getLastChild() {
        return null;
    }

    public Node appendChild(Node paramNode) throws DOMException {
        throw new DomEx((short) 3);
    }

    public Node insertBefore(Node paramNode1, Node paramNode2) throws DOMException {
        throw new DomEx((short) 3);
    }

    public Node replaceChild(Node paramNode1, Node paramNode2) throws DOMException {
        throw new DomEx((short) 3);
    }

    public Node removeChild(Node paramNode) throws DOMException {
        throw new DomEx((short) 3);
    }

    public Node getNextSibling() {
        if (this.parent == null)
            return null;
        if (this.parentIndex < 0 || this.parent.item(this.parentIndex) != this)
            this.parentIndex = this.parent.getIndexOf(this);
        return this.parent.item(this.parentIndex + 1);
    }

    public Node getPreviousSibling() {
        if (this.parent == null)
            return null;
        if (this.parentIndex < 0 || this.parent.item(this.parentIndex) != this)
            this.parentIndex = this.parent.getIndexOf(this);
        return this.parent.item(this.parentIndex - 1);
    }

    public NamedNodeMap getAttributes() {
        return null;
    }

    public int getIndexOf(Node paramNode) {
        return -1;
    }

    String getMessage(String paramString) {
        return getMessage(paramString, null);
    }

    String getMessage(String paramString, Object[] paramArrayOfObject) {
        Locale locale;
        if (this instanceof XmlDocument) {
            locale = ((XmlDocument) this).getLocale();
        } else if (this.ownerDocument == null) {
            locale = Locale.getDefault();
        } else {
            locale = this.ownerDocument.getLocale();
        }
        return XmlDocument.catalog.getMessage(locale, paramString, paramArrayOfObject);
    }

    public abstract Node cloneNode(boolean paramBoolean);

    public abstract String getNodeName();

    public abstract short getNodeType();

    public abstract void writeXml(XmlWriteContext paramXmlWriteContext) throws IOException;
}
