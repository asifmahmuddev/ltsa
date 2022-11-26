package com.sun.xml.tree;

import java.util.Locale;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class TreeWalker {
    private Node startPoint;
    private Node current;

    public TreeWalker(Node paramNode) {
        if (paramNode == null)
            throw new IllegalArgumentException(XmlDocument.catalog.getMessage(Locale.getDefault(), "TW-004"));
        if (!(paramNode instanceof NodeBase))
            throw new IllegalArgumentException(XmlDocument.catalog.getMessage(Locale.getDefault(), "TW-003"));
        this.startPoint = this.current = paramNode;
    }

    public Node getCurrent() {
        return this.current;
    }

    public Node getNext() {
        Node node1;
        Node node2;
        if (this.current == null)
            return null;
        switch (this.current.getNodeType()) {
            case 1 :
            case 9 :
            case 11 :
                node1 = this.current.getFirstChild();
                if (node1 != null) {
                    this.current = node1;
                    return node1;
                }
            case 2 :
            case 3 :
            case 4 :
            case 5 :
            case 6 :
            case 7 :
            case 8 :
            case 10 :
                node2 = this.current;
                for (; node2 != null && node2 != this.startPoint; node2 = node2.getParentNode()) {
                    node1 = node2.getNextSibling();
                    if (node1 != null) {
                        this.current = node1;
                        return node1;
                    }
                }
                this.current = null;
                return null;
        }
        throw new InternalError(((NodeBase) this.startPoint).getMessage("TW-000", new Object[]{Short.toString(this.current.getNodeType())}));
    }

    public Element getNextElement(String paramString) {
        Node node = getNext();
        for (; node != null; node = getNext()) {
            if (node.getNodeType() == 1 && (paramString == null || paramString.equals(node.getNodeName())))
                return (Element) node;
        }
        this.current = null;
        return null;
    }

    public void reset() {
        this.current = this.startPoint;
    }

    public Node removeCurrent() {
        if (this.current == null)
            throw new IllegalStateException(((NodeBase) this.startPoint).getMessage("TW-001"));
        Node node1 = this.current;
        Node node2 = this.current.getParentNode();
        Node node3 = null;
        if (node2 == null)
            throw new IllegalStateException(((NodeBase) this.startPoint).getMessage("TW-002"));
        Node node4 = this.current;
        for (; node4 != null && node4 != this.startPoint; node4 = node4.getParentNode()) {
            node3 = node4.getNextSibling();
            if (node3 != null) {
                this.current = node3;
                break;
            }
        }
        node2.removeChild(node1);
        return node3;
    }
}
