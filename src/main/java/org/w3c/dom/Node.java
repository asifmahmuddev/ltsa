package org.w3c.dom;

public interface Node {
    public static final short ELEMENT_NODE = 1;
    public static final short ATTRIBUTE_NODE = 2;
    public static final short TEXT_NODE = 3;
    public static final short CDATA_SECTION_NODE = 4;
    public static final short ENTITY_REFERENCE_NODE = 5;
    public static final short ENTITY_NODE = 6;
    public static final short PROCESSING_INSTRUCTION_NODE = 7;
    public static final short COMMENT_NODE = 8;
    public static final short DOCUMENT_NODE = 9;
    public static final short DOCUMENT_TYPE_NODE = 10;
    public static final short DOCUMENT_FRAGMENT_NODE = 11;
    public static final short NOTATION_NODE = 12;

    Node appendChild(Node paramNode) throws DOMException;

    Node cloneNode(boolean paramBoolean);

    NamedNodeMap getAttributes();

    NodeList getChildNodes();

    Node getFirstChild();

    Node getLastChild();

    Node getNextSibling();

    String getNodeName();

    short getNodeType();

    String getNodeValue() throws DOMException;

    Document getOwnerDocument();

    Node getParentNode();

    Node getPreviousSibling();

    boolean hasChildNodes();

    Node insertBefore(Node paramNode1, Node paramNode2) throws DOMException;

    Node removeChild(Node paramNode) throws DOMException;

    Node replaceChild(Node paramNode1, Node paramNode2) throws DOMException;

    void setNodeValue(String paramString) throws DOMException;
}
