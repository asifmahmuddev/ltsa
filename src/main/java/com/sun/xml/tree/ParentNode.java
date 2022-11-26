package com.sun.xml.tree;

import java.io.IOException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

abstract class ParentNode extends NodeBase implements XmlReadable {
    private NodeBase[] children;
    private int length;

    public void trimToSize() {
        if (this.length == 0) {
            this.children = null;
        } else if (this.children.length != this.length) {
            NodeBase[] arrayOfNodeBase = new NodeBase[this.length];
            System.arraycopy(this.children, 0, arrayOfNodeBase, 0, this.length);
            this.children = arrayOfNodeBase;
        }
    }

    void reduceWaste() {
        if (this.children == null)
            return;
        if (this.children.length - this.length > 6)
            trimToSize();
    }

    public void writeChildrenXml(XmlWriteContext paramXmlWriteContext) throws IOException {
        if (this.children == null)
            return;
        int i = 0;
        boolean bool = true;
        boolean bool1 = true;
        if (getNodeType() == 1) {
            bool = "preserve".equals(getInheritedAttribute("xml:space"));
            i = paramXmlWriteContext.getIndentLevel();
        }
        try {
            if (!bool)
                paramXmlWriteContext.setIndentLevel(i + 2);
            for (byte b = 0; b < this.length; b++) {
                if (!bool && this.children[b].getNodeType() != 3) {
                    paramXmlWriteContext.printIndent();
                    bool1 = false;
                }
                this.children[b].writeXml(paramXmlWriteContext);
            }
        } finally {
            if (!bool) {
                paramXmlWriteContext.setIndentLevel(i);
                if (!bool1)
                    paramXmlWriteContext.printIndent();
            }
        }
    }

    public void startParse(ParseContext paramParseContext) throws SAXException {
    }

    public void doneChild(NodeEx paramNodeEx, ParseContext paramParseContext) throws SAXException {
    }

    public void doneParse(ParseContext paramParseContext) throws SAXException {
    }

    public final boolean hasChildNodes() {
        return !(this.length <= 0);
    }

    public final Node getFirstChild() {
        if (this.length == 0)
            return null;
        return this.children[0];
    }

    public final Node getLastChild() {
        if (this.length == 0)
            return null;
        return this.children[this.length - 1];
    }

    public final int getLength() {
        return this.length;
    }

    public final Node item(int paramInt) {
        if (this.length == 0 || paramInt >= this.length)
            return null;
        try {
            return this.children[paramInt];
        } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
            return null;
        }
    }

    private NodeBase checkDocument(Node paramNode) throws DOMException {
        if (paramNode == null)
            throw new DomEx((short) 3);
        if (!(paramNode instanceof NodeBase))
            throw new DomEx((short) 4);
        Document document = paramNode.getOwnerDocument();
        XmlDocument xmlDocument = this.ownerDocument;
        NodeBase nodeBase = (NodeBase) paramNode;
        if (xmlDocument == null && this instanceof XmlDocument)
            xmlDocument = (XmlDocument) this;
        if (document != null && document != xmlDocument)
            throw new DomEx((short) 4);
        if (document == null)
            nodeBase.setOwnerDocument(xmlDocument);
        if (nodeBase.hasChildNodes()) {
            byte b = 0;
            while (true) {
                Node node = nodeBase.item(b);
                if (node != null) {
                    if (node.getOwnerDocument() == null) {
                        ((NodeBase) node).setOwnerDocument(xmlDocument);
                    } else if (node.getOwnerDocument() != xmlDocument) {
                        throw new DomEx((short) 4);
                    }
                    b++;
                }
                break;
            }
        }
        return nodeBase;
    }

    private void checkNotAncestor(Node paramNode) throws DOMException {
        if (!paramNode.hasChildNodes())
            return;
        ParentNode parentNode = this;
        while (parentNode != null) {
            if (paramNode == parentNode)
                throw new DomEx((short) 3);
            Node node = parentNode.getParentNode();
        }
    }

    private void mutated() {
        XmlDocument xmlDocument = this.ownerDocument;
        if (xmlDocument == null && this instanceof XmlDocument)
            xmlDocument = (XmlDocument) this;
        if (xmlDocument != null)
            xmlDocument.mutationCount++;
    }

    private void consumeFragment(Node paramNode1, Node paramNode2) throws DOMException {
        ParentNode parentNode = (ParentNode) paramNode1;
        Node node;
        for (byte b = 0; (node = parentNode.item(b)) != null; b++) {
            checkNotAncestor(node);
            checkChildType(node.getNodeType());
        }
        while ((node = parentNode.item(0)) != null)
            insertBefore(node, paramNode2);
    }

    public Node appendChild(Node paramNode) throws DOMException {
        if (this.readonly)
            throw new DomEx((short) 7);
        NodeBase nodeBase = checkDocument(paramNode);
        if (paramNode.getNodeType() == 11) {
            consumeFragment(paramNode, null);
            return paramNode;
        }
        checkNotAncestor(paramNode);
        checkChildType(nodeBase.getNodeType());
        if (this.children == null) {
            this.children = new NodeBase[3];
        } else if (this.children.length == this.length) {
            NodeBase[] arrayOfNodeBase = new NodeBase[this.length * 2];
            System.arraycopy(this.children, 0, arrayOfNodeBase, 0, this.length);
            this.children = arrayOfNodeBase;
        }
        nodeBase.setParentNode(this, this.length);
        this.children[this.length++] = nodeBase;
        mutated();
        return nodeBase;
    }

    public Node insertBefore(Node paramNode1, Node paramNode2) throws DOMException {
        if (this.readonly)
            throw new DomEx((short) 7);
        if (paramNode2 == null)
            return appendChild(paramNode1);
        if (this.length == 0)
            throw new DomEx((short) 8);
        NodeBase nodeBase = checkDocument(paramNode1);
        if (paramNode1.getNodeType() == 11) {
            consumeFragment(paramNode1, paramNode2);
            return paramNode1;
        }
        checkNotAncestor(paramNode1);
        checkChildType(paramNode1.getNodeType());
        if (this.children.length == this.length) {
            NodeBase[] arrayOfNodeBase = new NodeBase[this.length * 2];
            System.arraycopy(this.children, 0, arrayOfNodeBase, 0, this.length);
            this.children = arrayOfNodeBase;
        }
        for (byte b = 0; b < this.length; b++) {
            if (this.children[b] == paramNode2) {
                nodeBase.setParentNode(this, b);
                System.arraycopy(this.children, b, this.children, b + 1, this.length - b);
                this.children[b] = nodeBase;
                this.length++;
                mutated();
                return paramNode1;
            }
        }
        throw new DomEx((short) 8);
    }

    public Node replaceChild(Node paramNode1, Node paramNode2) throws DOMException {
        if (this.readonly)
            throw new DomEx((short) 7);
        if (paramNode1 == null || paramNode2 == null)
            throw new DomEx((short) 3);
        if (this.children == null)
            throw new DomEx((short) 8);
        NodeBase nodeBase = checkDocument(paramNode1);
        if (paramNode1.getNodeType() == 11) {
            consumeFragment(paramNode1, paramNode2);
            return removeChild(paramNode2);
        }
        checkNotAncestor(paramNode1);
        checkChildType(paramNode1.getNodeType());
        for (byte b = 0; b < this.length; b++) {
            if (this.children[b] == paramNode2) {
                nodeBase.setParentNode(this, b);
                this.children[b] = nodeBase;
                ((NodeBase) paramNode2).setParentNode(null, -1);
                mutated();
                return paramNode2;
            }
        }
        throw new DomEx((short) 8);
    }

    public Node removeChild(Node paramNode) throws DOMException {
        if (this.readonly)
            throw new DomEx((short) 7);
        if (!(paramNode instanceof NodeBase))
            throw new DomEx((short) 8);
        NodeBase nodeBase = (NodeBase) paramNode;
        for (byte b = 0; b < this.length; b++) {
            if (this.children[b] == nodeBase) {
                if (b + 1 != this.length)
                    System.arraycopy(this.children, b + 1, this.children, b, this.length - 1 - b);
                this.length--;
                this.children[this.length] = null;
                nodeBase.setParentNode(null, -1);
                mutated();
                return paramNode;
            }
        }
        throw new DomEx((short) 8);
    }

    public NodeList getElementsByTagName(String paramString) {
        if ("*".equals(paramString))
            paramString = null;
        return new TagList(this, paramString);
    }

    class TagList implements NodeList {
        private final ParentNode this$0;
        private String tag;
        private int lastMutationCount;
        private int lastIndex;
        private TreeWalker lastWalker;

        private int getLastMutationCount() {
            XmlDocument xmlDocument = (XmlDocument) this.this$0.getOwnerDocument();
            return (xmlDocument == null) ? 0 : xmlDocument.mutationCount;
        }

        TagList(ParentNode this$0, String param1String) {
            this.this$0 = this$0;
            this.tag = param1String;
        }

        public Node item(int param1Int) {
            if (param1Int < 0)
                return null;
            int i = getLastMutationCount();
            if (this.lastWalker != null && (param1Int < this.lastIndex || i != this.lastMutationCount))
                this.lastWalker = null;
            if (this.lastWalker == null) {
                this.lastWalker = new TreeWalker(this.this$0);
                this.lastIndex = -1;
                this.lastMutationCount = i;
            }
            if (param1Int == this.lastIndex)
                return this.lastWalker.getCurrent();
            Element element = null;
            while (param1Int > this.lastIndex && (element = this.lastWalker.getNextElement(this.tag)) != null)
                this.lastIndex++;
            return element;
        }

        public int getLength() {
            TreeWalker treeWalker = new TreeWalker(this.this$0);
            Element element = null;
            byte b = 0;
            while ((element = treeWalker.getNextElement(this.tag)) != null)
                b++;
            return b;
        }
    }

    public final int getIndexOf(Node paramNode) {
        for (byte b = 0; b < this.length; b++) {
            if (this.children[b] == paramNode)
                return b;
        }
        return -1;
    }

    abstract void checkChildType(int paramInt) throws DOMException;
}
