package org.jdom.input;

import java.io.IOException;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.EntityRef;
import org.jdom.Namespace;
import org.xml.sax.Attributes;
import org.xml.sax.DTDHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

public class SAXHandler extends DefaultHandler implements LexicalHandler, DeclHandler, DTDHandler {
    private static final String CVS_ID = "@(#) $RCSfile: SAXHandler.java,v $ $Revision: 1.39 $ $Date: 2002/03/15 05:36:48 $ $Name: jdom_1_0_b8 $";
    private static final Map attrNameToTypeMap = new HashMap(13);
    private Document document;
    protected Stack stack;
    protected boolean atRoot;
    protected boolean inDTD = false;
    protected boolean inInternalSubset = false;
    protected boolean previousCDATA = false;
    protected boolean inCDATA = false;
    private boolean expand = true;
    protected boolean suppress = false;
    private int entityDepth = 0;
    protected LinkedList declaredNamespaces;
    protected LinkedList availableNamespaces;
    private StringBuffer buffer = new StringBuffer();
    private StringBuffer textBuffer = new StringBuffer(4096);
    private Map externalEntities;
    private JDOMFactory factory;
    private boolean ignoringWhite = false;
    private Locator locator;
    static {
        attrNameToTypeMap.put("CDATA", new Integer(1));
        attrNameToTypeMap.put("ID", new Integer(2));
        attrNameToTypeMap.put("IDREF", new Integer(3));
        attrNameToTypeMap.put("IDREFS", new Integer(4));
        attrNameToTypeMap.put("ENTITY", new Integer(5));
        attrNameToTypeMap.put("ENTITIES", new Integer(6));
        attrNameToTypeMap.put("NMTOKEN", new Integer(7));
        attrNameToTypeMap.put("NMTOKENS", new Integer(8));
        attrNameToTypeMap.put("NOTATION", new Integer(9));
        attrNameToTypeMap.put("ENUMERATION", new Integer(10));
    }

    public SAXHandler(Document document) throws IOException {
        this(new DefaultJDOMFactory());
        this.document = document;
    }

    public SAXHandler() throws IOException {
        this((JDOMFactory) null);
    }

    public SAXHandler(JDOMFactory factory) throws IOException {
        if (factory != null) {
            this.factory = factory;
        } else {
            this.factory = new DefaultJDOMFactory();
        }
        this.atRoot = true;
        this.stack = new Stack();
        this.declaredNamespaces = new LinkedList();
        this.availableNamespaces = new LinkedList();
        this.availableNamespaces.add(Namespace.XML_NAMESPACE);
        this.externalEntities = new HashMap();
        this.document = this.factory.document(null);
    }

    public Document getDocument() {
        return this.document;
    }

    public JDOMFactory getFactory() {
        return this.factory;
    }

    public void setExpandEntities(boolean expand) {
        this.expand = expand;
    }

    public boolean getExpandEntities() {
        return this.expand;
    }

    public void setIgnoringElementContentWhitespace(boolean ignoringWhite) {
        this.ignoringWhite = ignoringWhite;
    }

    public boolean getIgnoringElementContentWhitespace() {
        return this.ignoringWhite;
    }

    public void externalEntityDecl(String name, String publicID, String systemID) throws SAXException {
        this.externalEntities.put(name, new String[]{publicID, systemID});
        if (!this.inInternalSubset)
            return;
        this.buffer.append("  <!ENTITY ").append(name);
        appendExternalId(publicID, systemID);
        this.buffer.append(">\n");
    }

    public void attributeDecl(String eName, String aName, String type, String valueDefault, String value) throws SAXException {
        if (!this.inInternalSubset)
            return;
        this.buffer.append("  <!ATTLIST ").append(eName).append(" ").append(aName).append(" ").append(type).append(" ");
        if (valueDefault != null) {
            this.buffer.append(valueDefault);
        } else {
            this.buffer.append("\"").append(value).append("\"");
        }
        if (valueDefault != null && valueDefault.equals("#FIXED"))
            this.buffer.append(" \"").append(value).append("\"");
        this.buffer.append(">\n");
    }

    public void elementDecl(String name, String model) throws SAXException {
        if (!this.inInternalSubset)
            return;
        this.buffer.append("  <!ELEMENT ").append(name).append(" ").append(model).append(">\n");
    }

    public void internalEntityDecl(String name, String value) throws SAXException {
        if (!this.inInternalSubset)
            return;
        this.buffer.append("  <!ENTITY ");
        if (name.startsWith("%")) {
            this.buffer.append("% ").append(name.substring(1));
        } else {
            this.buffer.append(name);
        }
        this.buffer.append(" \"").append(value).append("\">\n");
    }

    public void processingInstruction(String target, String data) throws SAXException {
        if (this.suppress)
            return;
        flushCharacters();
        if (this.atRoot) {
            this.document.addContent(this.factory.processingInstruction(target, data));
        } else {
            getCurrentElement().addContent(this.factory.processingInstruction(target, data));
        }
    }

    public void skippedEntity(String name) throws SAXException {
        if (name.startsWith("%"))
            return;
        flushCharacters();
        getCurrentElement().addContent(this.factory.entityRef(name));
    }

    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        if (this.suppress)
            return;
        Namespace ns = Namespace.getNamespace(prefix, uri);
        this.declaredNamespaces.add(ns);
    }

    public void endPrefixMapping(String prefix) throws SAXException {
        if (this.suppress)
            return;
        Iterator itr = this.availableNamespaces.iterator();
        while (itr.hasNext()) {
            Namespace ns = itr.next();
            if (prefix.equals(ns.getPrefix())) {
                itr.remove();
                return;
            }
        }
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        if (this.suppress)
            return;
        Element element = null;
        if (namespaceURI != null && !namespaceURI.equals("")) {
            String prefix = "";
            if (!qName.equals(localName)) {
                int split = qName.indexOf(":");
                prefix = qName.substring(0, split);
            }
            Namespace elementNamespace = Namespace.getNamespace(prefix, namespaceURI);
            element = this.factory.element(localName, elementNamespace);
        } else {
            element = this.factory.element(localName);
        }
        if (this.declaredNamespaces.size() > 0)
            transferNamespaces(element);
        for (int i = 0, len = atts.getLength(); i < len; i++) {
            Attribute attribute = null;
            String attLocalName = atts.getLocalName(i);
            String attQName = atts.getQName(i);
            int attType = getAttributeType(atts.getType(i));
            if (!attQName.startsWith("xmlns:") && !attQName.equals("xmlns")) {
                if (!attQName.equals(attLocalName)) {
                    String attPrefix = attQName.substring(0, attQName.indexOf(":"));
                    attribute = this.factory.attribute(attLocalName, atts.getValue(i), attType, getNamespace(attPrefix));
                } else {
                    attribute = this.factory.attribute(attLocalName, atts.getValue(i), attType);
                }
                element.setAttribute(attribute);
            }
        }
        flushCharacters();
        if (this.atRoot) {
            this.document.setRootElement(element);
            this.stack.push(element);
            this.atRoot = false;
        } else {
            getCurrentElement().addContent(element);
            this.stack.push(element);
        }
    }

    private void transferNamespaces(Element element) {
        Iterator i = this.declaredNamespaces.iterator();
        while (i.hasNext()) {
            Namespace ns = i.next();
            this.availableNamespaces.addFirst(ns);
            element.addNamespaceDeclaration(ns);
        }
        this.declaredNamespaces.clear();
    }

    private Namespace getNamespace(String prefix) {
        Iterator i = this.availableNamespaces.iterator();
        while (i.hasNext()) {
            Namespace ns = i.next();
            if (prefix.equals(ns.getPrefix()))
                return ns;
        }
        return Namespace.NO_NAMESPACE;
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        if (this.suppress || length == 0)
            return;
        if (this.previousCDATA != this.inCDATA)
            flushCharacters();
        this.textBuffer.append(ch, start, length);
    }

    protected void flushCharacters() throws SAXException {
        if (this.textBuffer.length() == 0) {
            this.previousCDATA = this.inCDATA;
            return;
        }
        String data = this.textBuffer.toString();
        this.textBuffer.setLength(0);
        if (this.previousCDATA) {
            getCurrentElement().addContent(this.factory.cdata(data));
        } else {
            getCurrentElement().addContent(this.factory.text(data));
        }
        this.previousCDATA = this.inCDATA;
    }

    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        if (this.suppress)
            return;
        if (this.ignoringWhite)
            return;
        if (length == 0)
            return;
        this.textBuffer.append(ch, start, length);
    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        if (this.suppress)
            return;
        flushCharacters();
        try {
            Element element = this.stack.pop();
            List addl = element.getAdditionalNamespaces();
            if (addl.size() > 0)
                this.availableNamespaces.removeAll(addl);
        } catch (EmptyStackException emptyStackException) {
            throw new SAXException("Ill-formed XML document (missing opening tag for " + localName + ")");
        }
        if (this.stack.empty())
            this.atRoot = true;
    }

    public void startDTD(String name, String publicID, String systemID) throws SAXException {
        flushCharacters();
        this.document.setDocType(this.factory.docType(name, publicID, systemID));
        this.inDTD = true;
        this.inInternalSubset = true;
    }

    public void endDTD() throws SAXException {
        this.document.getDocType().setInternalSubset(this.buffer.toString());
        this.inDTD = false;
        this.inInternalSubset = false;
    }

    public void startEntity(String name) throws SAXException {
        this.entityDepth++;
        if (this.expand || this.entityDepth > 1)
            return;
        if (name.equals("[dtd]")) {
            this.inInternalSubset = false;
            return;
        }
        if (!this.inDTD && !name.equals("amp") && !name.equals("lt") && !name.equals("gt") && !name.equals("apos") && !name.equals("quot"))
            if (!this.expand) {
                String pub = null;
                String sys = null;
                String[] ids = (String[]) this.externalEntities.get(name);
                if (ids != null) {
                    pub = ids[0];
                    sys = ids[1];
                }
                if (!this.atRoot && !this.stack.isEmpty()) {
                    flushCharacters();
                    EntityRef entity = this.factory.entityRef(name, pub, sys);
                    getCurrentElement().addContent(entity);
                }
                this.suppress = true;
            }
    }

    public void endEntity(String name) throws SAXException {
        this.entityDepth--;
        if (this.entityDepth == 0)
            this.suppress = false;
        if (name.equals("[dtd]"))
            this.inInternalSubset = true;
    }

    public void startCDATA() throws SAXException {
        if (this.suppress)
            return;
        this.inCDATA = true;
    }

    public void endCDATA() throws SAXException {
        if (this.suppress)
            return;
        this.previousCDATA = true;
        this.inCDATA = false;
    }

    public void comment(char[] ch, int start, int length) throws SAXException {
        if (this.suppress)
            return;
        flushCharacters();
        String commentText = new String(ch, start, length);
        if (this.inDTD && this.inInternalSubset && this.expand == false) {
            this.buffer.append("  <!--").append(commentText).append("-->\n");
            return;
        }
        if (!this.inDTD && !commentText.equals(""))
            if (this.stack.empty()) {
                this.document.addContent(this.factory.comment(commentText));
            } else {
                getCurrentElement().addContent(this.factory.comment(commentText));
            }
    }

    public void notationDecl(String name, String publicID, String systemID) throws SAXException {
        if (!this.inInternalSubset)
            return;
        this.buffer.append("  <!NOTATION ").append(name);
        appendExternalId(publicID, systemID);
        this.buffer.append(">\n");
    }

    public void unparsedEntityDecl(String name, String publicID, String systemID, String notationName) throws SAXException {
        if (!this.inInternalSubset)
            return;
        this.buffer.append("  <!ENTITY ").append(name);
        appendExternalId(publicID, systemID);
        this.buffer.append(" NDATA ").append(notationName);
        this.buffer.append(">\n");
    }

    protected void appendExternalId(String publicID, String systemID) {
        if (publicID != null)
            this.buffer.append(" PUBLIC \"").append(publicID).append("\"");
        if (systemID != null) {
            if (publicID == null) {
                this.buffer.append(" SYSTEM ");
            } else {
                this.buffer.append(" ");
            }
            this.buffer.append("\"").append(systemID).append("\"");
        }
    }

    protected Element getCurrentElement() throws SAXException {
        try {
            return this.stack.peek();
        } catch (EmptyStackException emptyStackException) {
            throw new SAXException("Ill-formed XML document (multiple root elements detected)");
        }
    }

    private int getAttributeType(String typeName) {
        Integer type = (Integer) attrNameToTypeMap.get(typeName);
        if (type == null) {
            if (typeName != null && typeName.length() > 0 && typeName.charAt(0) == '(')
                return 10;
            return 0;
        }
        return type.intValue();
    }

    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }

    public Locator getDocumentLocator() {
        return this.locator;
    }
}
