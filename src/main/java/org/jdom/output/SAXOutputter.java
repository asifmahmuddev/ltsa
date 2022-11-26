package org.jdom.output;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import org.jdom.Attribute;
import org.jdom.CDATA;
import org.jdom.Comment;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.ProcessingInstruction;
import org.jdom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.LocatorImpl;
import org.xml.sax.helpers.XMLReaderFactory;

public class SAXOutputter {
    private static final String CVS_ID = "@(#) $RCSfile: SAXOutputter.java,v $ $Revision: 1.18 $ $Date: 2002/01/08 09:17:10 $ $Name: jdom_1_0_b8 $";
    private static final String NAMESPACES_SAX_FEATURE = "http://xml.org/sax/features/namespaces";
    private static final String NS_PREFIXES_SAX_FEATURE = "http://xml.org/sax/features/namespace-prefixes";
    private static final String LEXICAL_HANDLER_SAX_PROPERTY = "http://xml.org/sax/properties/lexical-handler";
    private static final String DECL_HANDLER_SAX_PROPERTY = "http://xml.org/sax/properties/declaration-handler";
    private static final String LEXICAL_HANDLER_ALT_PROPERTY = "http://xml.org/sax/handlers/LexicalHandler";
    private static final String DECL_HANDLER_ALT_PROPERTY = "http://xml.org/sax/handlers/DeclHandler";
    private static final String[] attrTypeToNameMap = new String[]{"CDATA", "CDATA", "ID", "IDREF", "IDREFS", "ENTITY", "ENTITIES", "NMTOKEN", "NMTOKENS", "NOTATION", "NMTOKEN"};
    private ContentHandler contentHandler;
    private ErrorHandler errorHandler;
    private DTDHandler dtdHandler;
    private EntityResolver entityResolver;
    private LexicalHandler lexicalHandler;
    private DeclHandler declHandler;
    private boolean declareNamespaces = false;

    public SAXOutputter(ContentHandler contentHandler) {
        this(contentHandler, null, null, null, null);
    }

    public SAXOutputter(ContentHandler contentHandler, ErrorHandler errorHandler, DTDHandler dtdHandler, EntityResolver entityResolver) {
        this(contentHandler, errorHandler, dtdHandler, entityResolver, null);
    }

    public SAXOutputter(ContentHandler contentHandler, ErrorHandler errorHandler, DTDHandler dtdHandler, EntityResolver entityResolver, LexicalHandler lexicalHandler) {
        this.contentHandler = contentHandler;
        this.errorHandler = errorHandler;
        this.dtdHandler = dtdHandler;
        this.entityResolver = entityResolver;
        this.lexicalHandler = lexicalHandler;
    }

    public void setContentHandler(ContentHandler contentHandler) {
        this.contentHandler = contentHandler;
    }

    public ContentHandler getContentHandler() {
        return this.contentHandler;
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public ErrorHandler getErrorHandler() {
        return this.errorHandler;
    }

    public void setDTDHandler(DTDHandler dtdHandler) {
        this.dtdHandler = dtdHandler;
    }

    public DTDHandler getDTDHandler() {
        return this.dtdHandler;
    }

    public void setEntityResolver(EntityResolver entityResolver) {
        this.entityResolver = entityResolver;
    }

    public EntityResolver getEntityResolver() {
        return this.entityResolver;
    }

    public void setLexicalHandler(LexicalHandler lexicalHandler) {
        this.lexicalHandler = lexicalHandler;
    }

    public LexicalHandler getLexicalHandler() {
        return this.lexicalHandler;
    }

    public void setDeclHandler(DeclHandler declHandler) {
        this.declHandler = declHandler;
    }

    public DeclHandler getDeclHandler() {
        return this.declHandler;
    }

    public void setReportNamespaceDeclarations(boolean declareNamespaces) {
        this.declareNamespaces = declareNamespaces;
    }

    public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
        if ("http://xml.org/sax/features/namespace-prefixes".equals(name)) {
            setReportNamespaceDeclarations(value);
        } else if ("http://xml.org/sax/features/namespaces".equals(name)) {
            if (value != true)
                throw new SAXNotSupportedException(name);
        } else {
            throw new SAXNotRecognizedException(name);
        }
    }

    public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        if ("http://xml.org/sax/features/namespace-prefixes".equals(name))
            return this.declareNamespaces;
        if ("http://xml.org/sax/features/namespaces".equals(name))
            return true;
        throw new SAXNotRecognizedException(name);
    }

    public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        if ("http://xml.org/sax/properties/lexical-handler".equals(name) || "http://xml.org/sax/handlers/LexicalHandler".equals(name)) {
            setLexicalHandler((LexicalHandler) value);
        } else if ("http://xml.org/sax/properties/declaration-handler".equals(name) || "http://xml.org/sax/handlers/DeclHandler".equals(name)) {
            setDeclHandler((DeclHandler) value);
        } else {
            throw new SAXNotRecognizedException(name);
        }
    }

    public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        if ("http://xml.org/sax/properties/lexical-handler".equals(name) || "http://xml.org/sax/handlers/LexicalHandler".equals(name))
            return getLexicalHandler();
        if ("http://xml.org/sax/properties/declaration-handler".equals(name) || "http://xml.org/sax/handlers/DeclHandler".equals(name))
            return getDeclHandler();
        throw new SAXNotRecognizedException(name);
    }

    public void output(Document document) throws JDOMException {
        if (document == null)
            return;
        documentLocator(document);
        startDocument();
        dtdEvents(document);
        Iterator i = document.getContent().iterator();
        while (i.hasNext()) {
            Object obj = i.next();
            if (obj instanceof Element) {
                element(document.getRootElement(), new NamespaceStack());
                continue;
            }
            if (obj instanceof ProcessingInstruction) {
                processingInstruction((ProcessingInstruction) obj);
                continue;
            }
            if (obj instanceof CDATA) {
                characters(((CDATA) obj).getText());
                continue;
            }
            if (obj instanceof Comment)
                comment(((Comment) obj).getText());
        }
        endDocument();
    }

    private void dtdEvents(Document document) throws JDOMException {
        DocType docType = document.getDocType();
        if (docType != null && (this.dtdHandler != null || this.declHandler != null)) {
            String publicID = docType.getPublicID();
            String systemID = docType.getSystemID();
            String intSubset = docType.getInternalSubset();
            if (intSubset != null)
                intSubset = intSubset.trim();
            StringBuffer buf = new StringBuffer(64);
            buf.append("<!DOCTYPE ").append(docType.getElementName());
            if (intSubset != null && intSubset.length() != 0) {
                buf.append(" [\n").append(intSubset).append(']');
            } else if (publicID != null || systemID != null) {
                if (publicID != null) {
                    buf.append(" PUBLIC ");
                    buf.append('"').append(publicID).append('"');
                } else {
                    buf.append(" SYSTEM ");
                }
                buf.append('"').append(systemID).append('"');
            } else {
                buf.setLength(0);
            }
            if (buf.length() != 0)
                try {
                    String dtdDoc = buf.append('>').toString();
                    createDTDParser().parse(new InputSource(new StringReader(dtdDoc)));
                } catch (SAXParseException sAXParseException) {
                } catch (SAXException ex2) {
                    throw new JDOMException("DTD parsing error", ex2);
                } catch (IOException ex3) {
                    throw new JDOMException("DTD parsing error", ex3);
                }
        }
    }

    private void documentLocator(Document document) {
        LocatorImpl locator = new LocatorImpl();
        String publicID = null;
        String systemID = null;
        DocType docType = document.getDocType();
        if (docType != null) {
            publicID = docType.getPublicID();
            systemID = docType.getSystemID();
        }
        locator.setPublicId(publicID);
        locator.setSystemId(systemID);
        locator.setLineNumber(-1);
        locator.setColumnNumber(-1);
        this.contentHandler.setDocumentLocator(locator);
    }

    private void startDocument() throws JDOMException {
        try {
            this.contentHandler.startDocument();
        } catch (SAXException se) {
            throw new JDOMException("Exception in startDocument", se);
        }
    }

    private void endDocument() throws JDOMException {
        try {
            this.contentHandler.endDocument();
        } catch (SAXException se) {
            throw new JDOMException("Exception in endDocument", se);
        }
    }

    private void processingInstruction(ProcessingInstruction pi) throws JDOMException {
        if (pi != null) {
            String target = pi.getTarget();
            String data = pi.getData();
            try {
                this.contentHandler.processingInstruction(target, data);
            } catch (SAXException se) {
                throw new JDOMException("Exception in processingInstruction", se);
            }
        }
    }

    private void element(Element element, NamespaceStack namespaces) throws JDOMException {
        int previouslyDeclaredNamespaces = namespaces.size();
        Attributes nsAtts = startPrefixMapping(element, namespaces);
        startElement(element, nsAtts);
        elementContent(element, namespaces);
        endElement(element);
        endPrefixMapping(namespaces, previouslyDeclaredNamespaces);
    }

    private Attributes startPrefixMapping(Element element, NamespaceStack namespaces) throws JDOMException {
        AttributesImpl nsAtts = null;
        Namespace ns = element.getNamespace();
        if (ns != Namespace.NO_NAMESPACE && ns != Namespace.XML_NAMESPACE) {
            String prefix = ns.getPrefix();
            String uri = namespaces.getURI(prefix);
            if (!ns.getURI().equals(uri)) {
                namespaces.push(ns);
                nsAtts = addNsAttribute(nsAtts, ns);
                try {
                    this.contentHandler.startPrefixMapping(prefix, ns.getURI());
                } catch (SAXException se) {
                    throw new JDOMException("Exception in startPrefixMapping", se);
                }
            }
        }
        List additionalNamespaces = element.getAdditionalNamespaces();
        if (additionalNamespaces != null) {
            Iterator itr = additionalNamespaces.iterator();
            while (itr.hasNext()) {
                ns = itr.next();
                String prefix = ns.getPrefix();
                String uri = namespaces.getURI(prefix);
                if (!ns.getURI().equals(uri)) {
                    namespaces.push(ns);
                    nsAtts = addNsAttribute(nsAtts, ns);
                    try {
                        this.contentHandler.startPrefixMapping(prefix, ns.getURI());
                    } catch (SAXException se) {
                        throw new JDOMException("Exception in startPrefixMapping", se);
                    }
                }
            }
        }
        return nsAtts;
    }

    private void endPrefixMapping(NamespaceStack namespaces, int previouslyDeclaredNamespaces) throws JDOMException {
        while (namespaces.size() > previouslyDeclaredNamespaces) {
            String prefix = namespaces.pop();
            try {
                this.contentHandler.endPrefixMapping(prefix);
            } catch (SAXException se) {
                throw new JDOMException("Exception in endPrefixMapping", se);
            }
        }
    }

    private void startElement(Element element, Attributes nsAtts) throws JDOMException {
        String namespaceURI = element.getNamespaceURI();
        String localName = element.getName();
        String rawName = element.getQualifiedName();
        AttributesImpl atts = (nsAtts != null) ? new AttributesImpl(nsAtts) : new AttributesImpl();
        List attributes = element.getAttributes();
        Iterator i = attributes.iterator();
        while (i.hasNext()) {
            Attribute a = i.next();
            atts.addAttribute(a.getNamespaceURI(), a.getName(), a.getQualifiedName(), getAttributeTypeName(a.getAttributeType()), a.getValue());
        }
        try {
            this.contentHandler.startElement(namespaceURI, localName, rawName, atts);
        } catch (SAXException se) {
            throw new JDOMException("Exception in startElement", se);
        }
    }

    private void endElement(Element element) throws JDOMException {
        String namespaceURI = element.getNamespaceURI();
        String localName = element.getName();
        String rawName = element.getQualifiedName();
        try {
            this.contentHandler.endElement(namespaceURI, localName, rawName);
        } catch (SAXException se) {
            throw new JDOMException("Exception in endElement", se);
        }
    }

    private void elementContent(Element element, NamespaceStack namespaces) throws JDOMException {
        List eltContent = element.getContent();
        boolean empty = !(eltContent.size() != 0);
        boolean stringOnly = !(empty || eltContent.size() != 1 || !(eltContent.get(0) instanceof Text));
        if (stringOnly) {
            characters(element.getText());
        } else {
            Object content = null;
            for (int i = 0, size = eltContent.size(); i < size; i++) {
                content = eltContent.get(i);
                if (content instanceof Element) {
                    element((Element) content, namespaces);
                } else if (content instanceof Text) {
                    characters(((Text) content).getText());
                } else if (content instanceof CDATA) {
                    characters(((CDATA) content).getText());
                } else if (content instanceof ProcessingInstruction) {
                    processingInstruction((ProcessingInstruction) content);
                }
            }
        }
    }

    private void characters(String elementText) throws JDOMException {
        char[] c = elementText.toCharArray();
        try {
            this.contentHandler.characters(c, 0, c.length);
        } catch (SAXException se) {
            throw new JDOMException("Exception in characters", se);
        }
    }

    private void comment(String commentText) throws JDOMException {
        if (this.lexicalHandler != null) {
            char[] c = commentText.toCharArray();
            try {
                this.lexicalHandler.comment(c, 0, c.length);
            } catch (SAXException se) {
                throw new JDOMException("Exception in comment", se);
            }
        }
    }

    private AttributesImpl addNsAttribute(AttributesImpl atts, Namespace ns) {
        if (this.declareNamespaces) {
            if (atts == null)
                atts = new AttributesImpl();
            atts.addAttribute("", "", "xmlns:" + ns.getPrefix(), "CDATA", ns.getURI());
        }
        return atts;
    }

    private String getAttributeTypeName(int type) {
        if (type < 0 || type >= attrTypeToNameMap.length)
            type = 0;
        return attrTypeToNameMap[type];
    }

    protected XMLReader createParser() throws Exception {
        XMLReader parser = null;
        try {
            Class factoryClass = Class.forName("javax.xml.parsers.SAXParserFactory");
            Method newParserInstance = factoryClass.getMethod("newInstance", null);
            Object factory = newParserInstance.invoke(null, null);
            Method newSAXParser = factoryClass.getMethod("newSAXParser", null);
            Object jaxpParser = newSAXParser.invoke(factory, null);
            Class parserClass = jaxpParser.getClass();
            Method getXMLReader = parserClass.getMethod("getXMLReader", null);
            parser = (XMLReader) getXMLReader.invoke(jaxpParser, null);
        } catch (ClassNotFoundException classNotFoundException) {
        } catch (InvocationTargetException invocationTargetException) {
        } catch (NoSuchMethodException noSuchMethodException) {
        } catch (IllegalAccessException illegalAccessException) {
        }
        if (parser == null)
            parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
        return parser;
    }

    private XMLReader createDTDParser() throws JDOMException {
        XMLReader parser = null;
        try {
            parser = createParser();
        } catch (Exception ex1) {
            throw new JDOMException("Error in SAX parser allocation", ex1);
        }
        if (getDTDHandler() != null)
            parser.setDTDHandler(getDTDHandler());
        if (getEntityResolver() != null)
            parser.setEntityResolver(getEntityResolver());
        if (getLexicalHandler() != null)
            try {
                parser.setProperty("http://xml.org/sax/properties/lexical-handler", getLexicalHandler());
            } catch (SAXException sAXException) {
                try {
                    parser.setProperty("http://xml.org/sax/handlers/LexicalHandler", getLexicalHandler());
                } catch (SAXException sAXException1) {
                }
            }
        if (getDeclHandler() != null)
            try {
                parser.setProperty("http://xml.org/sax/properties/declaration-handler", getDeclHandler());
            } catch (SAXException sAXException) {
                try {
                    parser.setProperty("http://xml.org/sax/handlers/DeclHandler", getDeclHandler());
                } catch (SAXException sAXException1) {
                }
            }
        return parser;
    }

    public SAXOutputter() {
    }
}
