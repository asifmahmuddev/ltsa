package com.sun.xml.tree;

import com.sun.xml.parser.Parser;
import com.sun.xml.parser.Resolver;
import com.sun.xml.parser.ValidatingParser;
import com.sun.xml.util.MessageCatalog;
import com.sun.xml.util.XmlNames;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Locale;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;

public class XmlDocument extends ParentNode implements DocumentEx, DOMImplementation {
    static String eol;

    static Class class$(String paramString) {
        try {
            return Class.forName(paramString);
        } catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }

    static {
        String str;
        try {
            str = System.getProperty("line.separator", "\n");
        } catch (SecurityException securityException) {
            str = "\n";
        }
        eol = str;
    }
    static final MessageCatalog catalog = new Catalog();
    private Locale locale = Locale.getDefault();
    private String systemId;
    private ElementFactory factory;
    int mutationCount;
    boolean replaceRootElement;
    static Class class$com$sun$xml$tree$XmlDocument$Catalog;

    public static XmlDocument createXmlDocument(String paramString, boolean paramBoolean) throws IOException, SAXException {
        return createXmlDocument(new InputSource(paramString), paramBoolean);
    }

    public static XmlDocument createXmlDocument(String paramString) throws IOException, SAXException {
        return createXmlDocument(new InputSource(paramString), false);
    }

    public static XmlDocument createXmlDocument(InputStream paramInputStream, boolean paramBoolean) throws IOException, SAXException {
        return createXmlDocument(new InputSource(paramInputStream), paramBoolean);
    }

    public static XmlDocument createXmlDocument(InputSource paramInputSource, boolean paramBoolean) throws IOException, SAXException {
        Parser parser;
        XmlDocumentBuilder xmlDocumentBuilder;
        try {
            if (paramBoolean) {
                ValidatingParser validatingParser = new ValidatingParser(true);
            } else {
                parser = new Parser();
            }
            parser.setEntityResolver((EntityResolver) new Resolver());
            xmlDocumentBuilder = new XmlDocumentBuilder();
            xmlDocumentBuilder.setDisableNamespaces(true);
        } catch (Exception exception) {
            throw new SAXException(exception);
        }
        xmlDocumentBuilder.setParser((Parser) parser);
        parser.parse(paramInputSource);
        return xmlDocumentBuilder.getDocument();
    }

    ElementNode getDocument() {
        for (byte b = 0;; b++) {
            Node node = item(b);
            if (node == null)
                return null;
            if (node instanceof ElementNode)
                return (ElementNode) node;
        }
    }

    public Locale getLocale() {
        return this.locale;
    }

    public void setLocale(Locale paramLocale) {
        if (paramLocale == null)
            paramLocale = Locale.getDefault();
        this.locale = paramLocale;
    }

    public Locale chooseLocale(String[] paramArrayOfString) {
        Locale locale = catalog.chooseLocale(paramArrayOfString);
        if (locale != null)
            setLocale(locale);
        return locale;
    }

    public void write(OutputStream paramOutputStream) throws IOException {
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(paramOutputStream, "UTF8");
        write(outputStreamWriter, "UTF-8");
    }

    public void write(Writer paramWriter) throws IOException {
        String str = null;
        if (paramWriter instanceof OutputStreamWriter)
            str = java2std(((OutputStreamWriter) paramWriter).getEncoding());
        write(paramWriter, str);
    }

    static String java2std(String paramString) {
        if (paramString == null)
            return null;
        if (paramString.startsWith("ISO8859_"))
            return "ISO-8859-" + paramString.substring(8);
        if (paramString.startsWith("8859_"))
            return "ISO-8859-" + paramString.substring(5);
        if ("ASCII7".equalsIgnoreCase(paramString) || "ASCII".equalsIgnoreCase(paramString))
            return "US-ASCII";
        if ("UTF8".equalsIgnoreCase(paramString))
            return "UTF-8";
        if (paramString.startsWith("Unicode"))
            return "UTF-16";
        if ("SJIS".equalsIgnoreCase(paramString))
            return "Shift_JIS";
        if ("JIS".equalsIgnoreCase(paramString))
            return "ISO-2022-JP";
        if ("EUCJIS".equalsIgnoreCase(paramString))
            return "EUC-JP";
        return paramString;
    }

    public void write(Writer paramWriter, String paramString) throws IOException {
        paramWriter.write("<?xml version=\"1.0\"");
        if (paramString != null) {
            paramWriter.write(" encoding=\"");
            paramWriter.write(paramString);
            paramWriter.write(34);
        }
        paramWriter.write("?>");
        paramWriter.write(eol);
        paramWriter.write(eol);
        writeChildrenXml(createWriteContext(paramWriter, 0));
        paramWriter.write(eol);
        paramWriter.flush();
    }

    public XmlWriteContext createWriteContext(Writer paramWriter) {
        return new ExtWriteContext(this, paramWriter);
    }

    public XmlWriteContext createWriteContext(Writer paramWriter, int paramInt) {
        return new ExtWriteContext(this, paramWriter, paramInt);
    }

    public void writeXml(XmlWriteContext paramXmlWriteContext) throws IOException {
        Writer writer = paramXmlWriteContext.getWriter();
        String str = null;
        if (writer instanceof OutputStreamWriter)
            str = java2std(((OutputStreamWriter) writer).getEncoding());
        writer.write("<?xml version=\"1.0\"");
        if (str != null) {
            writer.write(" encoding=\"");
            writer.write(str);
            writer.write(34);
        }
        writer.write("?>");
        writer.write(eol);
        writer.write(eol);
        writeChildrenXml(paramXmlWriteContext);
    }

    public void writeChildrenXml(XmlWriteContext paramXmlWriteContext) throws IOException {
        int i = getLength();
        Writer writer = paramXmlWriteContext.getWriter();
        if (i == 0)
            return;
        for (byte b = 0; b < i; b++) {
            ((NodeBase) item(b)).writeXml(paramXmlWriteContext);
            writer.write(eol);
        }
    }

    void checkChildType(int paramInt) throws DOMException {
        switch (paramInt) {
            case 1 :
            case 7 :
            case 8 :
            case 10 :
                return;
        }
        throw new DomEx((short) 3);
    }

    public final void setSystemId(String paramString) {
        this.systemId = paramString;
    }

    public final String getSystemId() {
        return this.systemId;
    }

    public Node appendChild(Node paramNode) throws DOMException {
        if (paramNode instanceof Element && getDocument() != null)
            throw new DomEx((short) 3);
        if (paramNode instanceof DocumentType && getDoctype() != null)
            throw new DomEx((short) 3);
        return super.appendChild(paramNode);
    }

    public Node insertBefore(Node paramNode1, Node paramNode2) throws DOMException {
        if (!this.replaceRootElement && paramNode1 instanceof Element && getDocument() != null)
            throw new DomEx((short) 3);
        if (!this.replaceRootElement && paramNode1 instanceof DocumentType && getDoctype() != null)
            throw new DomEx((short) 3);
        return super.insertBefore(paramNode1, paramNode2);
    }

    public Node replaceChild(Node paramNode1, Node paramNode2) throws DOMException {
        if (paramNode1 instanceof DocumentFragment) {
            byte b1 = 0;
            byte b2 = 0;
            this.replaceRootElement = false;
            ParentNode parentNode = (ParentNode) paramNode1;
            byte b3 = 0;
            Node node;
            while ((node = parentNode.item(b3)) != null) {
                if (node instanceof Element) {
                    b1++;
                } else if (node instanceof DocumentType) {
                    b2++;
                }
                b3++;
            }
            if (b1 > 1 || b2 > 1)
                throw new DomEx((short) 3);
            this.replaceRootElement = true;
        }
        return super.replaceChild(paramNode1, paramNode2);
    }

    public final short getNodeType() {
        return 9;
    }

    public final DocumentType getDoctype() {
        for (byte b = 0;; b++) {
            Node node = item(b);
            if (node == null)
                return null;
            if (node instanceof DocumentType)
                return (DocumentType) node;
        }
    }

    Doctype createDoctype(String paramString) {
        Doctype doctype = new Doctype(paramString);
        doctype.setOwnerDocument(this);
        return doctype;
    }

    public DocumentType setDoctype(String paramString1, String paramString2, String paramString3) {
        Doctype doctype = (Doctype) getDoctype();
        if (doctype != null) {
            doctype.setPrintInfo(paramString1, paramString2, paramString3);
        } else {
            doctype = new Doctype(paramString1, paramString2, paramString3);
            doctype.setOwnerDocument(this);
            insertBefore(doctype, getFirstChild());
        }
        return doctype;
    }

    public Element getDocumentElement() {
        return getDocument();
    }

    public final void setElementFactory(ElementFactory paramElementFactory) {
        this.factory = paramElementFactory;
    }

    public final ElementFactory getElementFactory() {
        return this.factory;
    }

    public final Element createElement(String paramString) throws DOMException {
        return createElementEx(paramString);
    }

    public final ElementEx createElementEx(String paramString) throws DOMException {
        ElementNode elementNode;
        if (!XmlNames.isName(paramString))
            throw new DomEx((short) 5);
        if (this.factory != null) {
            elementNode = (ElementNode) this.factory.createElementEx(paramString);
        } else {
            elementNode = new ElementNode();
        }
        elementNode.setTag(paramString);
        elementNode.setOwnerDocument(this);
        return elementNode;
    }

    public final ElementEx createElementEx(String paramString1, String paramString2) throws DOMException {
        ElementNode elementNode;
        if (!XmlNames.isName(paramString2))
            throw new DomEx((short) 5);
        if (this.factory != null) {
            elementNode = (ElementNode) this.factory.createElementEx(paramString1, paramString2);
        } else {
            elementNode = new ElementNode();
        }
        elementNode.setTag(paramString2);
        elementNode.setOwnerDocument(this);
        return elementNode;
    }

    public Text createTextNode(String paramString) {
        TextNode textNode = new TextNode();
        textNode.setOwnerDocument(this);
        if (paramString != null)
            textNode.setText(paramString.toCharArray());
        return textNode;
    }

    public CDATASection createCDATASection(String paramString) {
        CDataNode cDataNode = new CDataNode();
        if (paramString != null)
            cDataNode.setText(paramString.toCharArray());
        cDataNode.setOwnerDocument(this);
        return cDataNode;
    }

    TextNode newText(char[] paramArrayOfchar, int paramInt1, int paramInt2) throws SAXException {
        TextNode textNode = (TextNode) createTextNode(null);
        char[] arrayOfChar = new char[paramInt2];
        System.arraycopy(paramArrayOfchar, paramInt1, arrayOfChar, 0, paramInt2);
        textNode.setText(arrayOfChar);
        return textNode;
    }

    public ProcessingInstruction createProcessingInstruction(String paramString1, String paramString2) throws DOMException {
        if (!XmlNames.isName(paramString1))
            throw new DomEx((short) 5);
        PINode pINode = new PINode(paramString1, paramString2);
        pINode.setOwnerDocument(this);
        return pINode;
    }

    public Attr createAttribute(String paramString) throws DOMException {
        if (!XmlNames.isName(paramString))
            throw new DomEx((short) 5);
        AttributeNode attributeNode = new AttributeNode(paramString, null, true, null);
        attributeNode.setOwnerDocument(this);
        return attributeNode;
    }

    public Comment createComment(String paramString) {
        CommentNode commentNode = new CommentNode(paramString);
        commentNode.setOwnerDocument(this);
        return commentNode;
    }

    public Document getOwnerDoc() {
        return null;
    }

    public DOMImplementation getImplementation() {
        return this;
    }

    public DocumentFragment createDocumentFragment() {
        DocFragNode docFragNode = new DocFragNode();
        docFragNode.setOwnerDocument(this);
        return docFragNode;
    }

    public EntityReference createEntityReference(String paramString) throws DOMException {
        if (!XmlNames.isName(paramString))
            throw new DomEx((short) 5);
        EntityRefNode entityRefNode = new EntityRefNode(paramString);
        entityRefNode.setOwnerDocument(this);
        return entityRefNode;
    }

    public final String getNodeName() {
        return "#document";
    }

    public Node cloneNode(boolean paramBoolean) {
        XmlDocument xmlDocument = new XmlDocument();
        xmlDocument.systemId = this.systemId;
        if (paramBoolean) {
            Node node;
            for (byte b = 0; (node = item(b)) != null; b++) {
                if (!(node instanceof DocumentType)) {
                    node = node.cloneNode(true);
                    xmlDocument.changeNodeOwner(node);
                    xmlDocument.appendChild(node);
                }
            }
        }
        return xmlDocument;
    }

    public final void changeNodeOwner(Node paramNode) throws DOMException {
        if (paramNode.getOwnerDocument() == this)
            return;
        if (!(paramNode instanceof NodeBase))
            throw new DomEx((short) 4);
        switch (paramNode.getNodeType()) {
            case 6 :
            case 9 :
            case 10 :
            case 12 :
                throw new DomEx((short) 3);
        }
        if (paramNode instanceof AttributeNode) {
            AttributeNode attributeNode = (AttributeNode) paramNode;
            ElementNode elementNode = attributeNode.getNameScope();
            if (elementNode != null && elementNode.getOwnerDocument() != this)
                throw new DomEx((short) 3);
        }
        NodeBase nodeBase = (NodeBase) paramNode.getParentNode();
        if (nodeBase != null)
            nodeBase.removeChild(paramNode);
        TreeWalker treeWalker = new TreeWalker(paramNode);
        nodeBase = (NodeBase) treeWalker.getCurrent();
        for (; nodeBase != null; nodeBase = (NodeBase) treeWalker.getNext()) {
            nodeBase.setOwnerDocument(this);
            if (nodeBase instanceof ElementNode) {
                NamedNodeMap namedNodeMap = nodeBase.getAttributes();
                int i = namedNodeMap.getLength();
                for (byte b = 0; b < i; b++)
                    changeNodeOwner(namedNodeMap.item(b));
            }
        }
    }

    public boolean hasFeature(String paramString1, String paramString2) {
        if (!"XML".equalsIgnoreCase(paramString1))
            return false;
        if (paramString2 != null || !"1.0".equals(paramString2))
            return false;
        return true;
    }

    public ElementEx getElementExById(String paramString) {
        if (paramString == null)
            throw new IllegalArgumentException(getMessage("XD-000"));
        TreeWalker treeWalker = new TreeWalker(this);
        ElementEx elementEx;
        while ((elementEx = (ElementEx) treeWalker.getNextElement(null)) != null) {
            String str = elementEx.getIdAttributeName();
            if (str != null) {
                String str1 = elementEx.getAttribute(str);
                if (str1.equals(paramString))
                    return elementEx;
            }
        }
        return null;
    }

    static final class DocFragNode extends ParentNode implements DocumentFragment {
        void checkChildType(int param1Int) throws DOMException {
            switch (param1Int) {
                case 1 :
                case 3 :
                case 4 :
                case 5 :
                case 7 :
                case 8 :
                    return;
            }
            throw new DomEx((short) 3);
        }

        public void writeXml(XmlWriteContext param1XmlWriteContext) throws IOException {
            writeChildrenXml(param1XmlWriteContext);
        }

        public Node getParentNode() {
            return null;
        }

        public void setParentNode(Node param1Node) {
            if (param1Node != null)
                throw new IllegalArgumentException();
        }

        public short getNodeType() {
            return 11;
        }

        public String getNodeName() {
            return "#document-fragment";
        }

        public Node cloneNode(boolean param1Boolean) {
            DocFragNode docFragNode = new DocFragNode();
            docFragNode.setOwnerDocument((XmlDocument) getOwnerDocument());
            if (param1Boolean)
                throw new RuntimeException(getMessage("XD-001"));
            return docFragNode;
        }
    }

    static final class EntityRefNode extends ParentNode implements EntityReference {
        private String entity;

        EntityRefNode(String param1String) {
            if (param1String == null)
                throw new IllegalArgumentException(getMessage("XD-002"));
            this.entity = param1String;
        }

        void checkChildType(int param1Int) throws DOMException {
            switch (param1Int) {
                case 1 :
                case 3 :
                case 4 :
                case 5 :
                case 7 :
                case 8 :
                    return;
            }
            throw new DomEx((short) 3);
        }

        public void writeXml(XmlWriteContext param1XmlWriteContext) throws IOException {
            if (!param1XmlWriteContext.isEntityDeclared(this.entity))
                throw new IOException(getMessage("XD-003", new Object[]{this.entity}));
            Writer writer = param1XmlWriteContext.getWriter();
            writer.write(38);
            writer.write(this.entity);
            writer.write(59);
        }

        public short getNodeType() {
            return 5;
        }

        public String getNodeName() {
            return this.entity;
        }

        public Node cloneNode(boolean param1Boolean) {
            EntityRefNode entityRefNode = new EntityRefNode(this.entity);
            entityRefNode.setOwnerDocument((XmlDocument) getOwnerDocument());
            if (param1Boolean)
                throw new RuntimeException(getMessage("XD-001"));
            return entityRefNode;
        }
    }

    class ExtWriteContext extends XmlWriteContext {
        private final XmlDocument this$0;

        ExtWriteContext(XmlDocument this$0, Writer param1Writer) {
            super(param1Writer);
            this.this$0 = this$0;
        }

        ExtWriteContext(XmlDocument this$0, Writer param1Writer, int param1Int) {
            super(param1Writer, param1Int);
            this.this$0 = this$0;
        }

        public boolean isEntityDeclared(String param1String) {
            if (super.isEntityDeclared(param1String))
                return true;
            DocumentType documentType = this.this$0.getDoctype();
            if (documentType == null)
                return false;
            return !(documentType.getEntities().getNamedItem(param1String) == null);
        }
    }

    static class Catalog extends MessageCatalog {
        Catalog() {
            super((XmlDocument.class$com$sun$xml$tree$XmlDocument$Catalog != null)
                ? XmlDocument.class$com$sun$xml$tree$XmlDocument$Catalog
                : (XmlDocument.class$com$sun$xml$tree$XmlDocument$Catalog = XmlDocument.class$("com.sun.xml.tree.XmlDocument$Catalog")));
        }
    }
}
