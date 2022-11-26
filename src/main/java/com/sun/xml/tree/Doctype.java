package com.sun.xml.tree;

import java.io.IOException;
import java.io.Writer;
import java.util.Vector;
import org.w3c.dom.DOMException;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Notation;

final class Doctype extends NodeBase implements DocumentType {
    private String name;
    private Nodemap entities;
    private Nodemap notations;
    private String publicId;
    private String systemId;
    private String internalSubset;

    Doctype(String paramString) {
        this.name = paramString;
        this.entities = new Nodemap();
        this.notations = new Nodemap();
    }

    Doctype(String paramString1, String paramString2, String paramString3) {
        this.publicId = paramString1;
        this.systemId = paramString2;
        this.internalSubset = paramString3;
    }

    void setPrintInfo(String paramString1, String paramString2, String paramString3) {
        this.publicId = paramString1;
        this.systemId = paramString2;
        this.internalSubset = paramString3;
    }

    public void writeXml(XmlWriteContext paramXmlWriteContext) throws IOException {
        Writer writer = paramXmlWriteContext.getWriter();
        Element element = getOwnerDocument().getDocumentElement();
        writer.write("<!DOCTYPE ");
        writer.write((element == null) ? "UNKNOWN-ROOT" : element.getNodeName());
        if (this.systemId != null) {
            if (this.publicId != null) {
                writer.write(" PUBLIC '");
                writer.write(this.publicId);
                writer.write("' '");
            } else {
                writer.write(" SYSTEM '");
            }
            writer.write(this.systemId);
            writer.write("'");
        }
        if (this.internalSubset != null) {
            writer.write(XmlDocument.eol);
            writer.write("[");
            writer.write(this.internalSubset);
            writer.write("]");
        }
        writer.write(">");
        writer.write(XmlDocument.eol);
    }

    public short getNodeType() {
        return 10;
    }

    public String getName() {
        return this.name;
    }

    public String getNodeName() {
        return this.name;
    }

    public Node cloneNode(boolean paramBoolean) {
        throw new RuntimeException(getMessage("DT-000"));
    }

    public NamedNodeMap getEntities() {
        return this.entities;
    }

    public NamedNodeMap getNotations() {
        return this.notations;
    }

    void setOwnerDocument(XmlDocument paramXmlDocument) {
        super.setOwnerDocument(paramXmlDocument);
        if (this.entities != null)
            for (byte b = 0; this.entities.item(b) != null; b++)
                ((NodeBase) this.entities.item(b)).setOwnerDocument(paramXmlDocument);
        if (this.notations != null)
            for (byte b = 0; this.notations.item(b) != null; b++)
                ((NodeBase) this.notations.item(b)).setOwnerDocument(paramXmlDocument);
    }

    void addNotation(String paramString1, String paramString2, String paramString3) {
        NotationNode notationNode = new NotationNode(paramString1, paramString2, paramString3);
        notationNode.setOwnerDocument((XmlDocument) getOwnerDocument());
        this.notations.setNamedItem(notationNode);
    }

    void addEntityNode(String paramString1, String paramString2, String paramString3, String paramString4) {
        EntityNode entityNode = new EntityNode(paramString1, paramString2, paramString3, paramString4);
        entityNode.setOwnerDocument((XmlDocument) getOwnerDocument());
        this.entities.setNamedItem(entityNode);
    }

    void addEntityNode(String paramString1, String paramString2) {
        if ("lt".equals(paramString1) || "gt".equals(paramString1) || "apos".equals(paramString1) || "quot".equals(paramString1) || "amp".equals(paramString1))
            return;
        EntityNode entityNode = new EntityNode(paramString1, paramString2);
        entityNode.setOwnerDocument((XmlDocument) getOwnerDocument());
        this.entities.setNamedItem(entityNode);
    }

    void setReadonly() {
        this.entities.readonly = true;
        this.notations.readonly = true;
    }

    static class NotationNode extends NodeBase implements Notation {
        private String notation;
        private String publicId;
        private String systemId;

        NotationNode(String param1String1, String param1String2, String param1String3) {
            this.notation = param1String1;
            this.publicId = param1String2;
            this.systemId = param1String3;
        }

        public String getPublicId() {
            return this.publicId;
        }

        public String getSystemId() {
            return this.systemId;
        }

        public short getNodeType() {
            return 12;
        }

        public String getNodeName() {
            return this.notation;
        }

        public Node cloneNode(boolean param1Boolean) {
            NotationNode notationNode = new NotationNode(this.notation, this.publicId, this.systemId);
            notationNode.setOwnerDocument((XmlDocument) getOwnerDocument());
            return notationNode;
        }

        public void writeXml(XmlWriteContext param1XmlWriteContext) throws IOException {
            Writer writer = param1XmlWriteContext.getWriter();
            writer.write("<!NOTATION ");
            writer.write(this.notation);
            if (this.publicId != null) {
                writer.write(" PUBLIC '");
                writer.write(this.publicId);
                if (this.systemId != null) {
                    writer.write("' '");
                    writer.write(this.systemId);
                }
            } else {
                writer.write(" SYSTEM '");
                writer.write(this.systemId);
            }
            writer.write("'>");
        }
    }

    static class EntityNode extends NodeBase implements Entity {
        private String entityName;
        private String publicId;
        private String systemId;
        private String notation;
        private String value;

        EntityNode(String param1String1, String param1String2, String param1String3, String param1String4) {
            this.entityName = param1String1;
            this.publicId = param1String2;
            this.systemId = param1String3;
            this.notation = param1String4;
        }

        EntityNode(String param1String1, String param1String2) {
            this.entityName = param1String1;
            this.value = param1String2;
        }

        public String getNodeName() {
            return this.entityName;
        }

        public short getNodeType() {
            return 6;
        }

        public String getPublicId() {
            return this.publicId;
        }

        public String getSystemId() {
            return this.systemId;
        }

        public String getNotationName() {
            return this.notation;
        }

        public Node cloneNode(boolean param1Boolean) {
            EntityNode entityNode = new EntityNode(this.entityName, this.publicId, this.systemId, this.notation);
            entityNode.setOwnerDocument((XmlDocument) getOwnerDocument());
            return entityNode;
        }

        public void writeXml(XmlWriteContext param1XmlWriteContext) throws IOException {
            Writer writer = param1XmlWriteContext.getWriter();
            writer.write("<!ENTITY ");
            writer.write(this.entityName);
            if (this.value == null) {
                if (this.publicId != null) {
                    writer.write(" PUBLIC '");
                    writer.write(this.publicId);
                    writer.write("' '");
                } else {
                    writer.write(" SYSTEM '");
                }
                writer.write(this.systemId);
                writer.write("'");
                if (this.notation != null) {
                    writer.write(" NDATA ");
                    writer.write(this.notation);
                }
            } else {
                writer.write(" \"");
                int i = this.value.length();
                for (byte b = 0; b < i; b++) {
                    char c = this.value.charAt(b);
                    if (c == '"') {
                        writer.write("&quot;");
                    } else {
                        writer.write(c);
                    }
                }
                writer.write(34);
            }
            writer.write(">");
        }
    }

    static class Nodemap implements NamedNodeMap {
        boolean readonly;
        Vector list;

        Nodemap() {
            this.list = new Vector();
        }

        public Node getNamedItem(String param1String) {
            int i = this.list.size();
            for (byte b = 0; b < i; b++) {
                Node node = item(b);
                if (node.getNodeName().equals(param1String))
                    return node;
            }
            return null;
        }

        public int getLength() {
            return this.list.size();
        }

        public Node item(int param1Int) {
            if (param1Int < 0 || param1Int >= this.list.size())
                return null;
            return this.list.elementAt(param1Int);
        }

        public Node removeNamedItem(String param1String) throws DOMException {
            throw new DomEx((short) 7);
        }

        public Node setNamedItem(Node param1Node) throws DOMException {
            if (this.readonly)
                throw new DomEx((short) 7);
            this.list.addElement(param1Node);
            return null;
        }
    }
}
