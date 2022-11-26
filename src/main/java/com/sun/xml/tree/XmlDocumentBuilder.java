package com.sun.xml.tree;

import com.sun.xml.parser.AttributeListEx;
import com.sun.xml.parser.DtdEventListener;
import com.sun.xml.parser.LexicalEventListener;
import com.sun.xml.parser.Parser;
import java.util.Locale;
import java.util.Vector;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.EntityReference;
import org.xml.sax.AttributeList;
import org.xml.sax.DTDHandler;
import org.xml.sax.DocumentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XmlDocumentBuilder implements LexicalEventListener {
    private static final String xmlURI = "http://www.w3.com/XML/1998/namespace";
    private XmlDocument document;
    private Locator locator;
    private ParseContextImpl context = new ParseContextImpl(this);
    private Locale locale = Locale.getDefault();
    private ElementFactory factory;
    private Parser parser;
    private Vector attrTmp = new Vector();
    private ParentNode[] elementStack;
    private int topOfStack;
    private boolean inDTD;
    private boolean inCDataSection;
    private boolean ignoringLexicalInfo = true;
    private boolean disableNamespaces = true;

    public boolean isIgnoringLexicalInfo() {
        return this.ignoringLexicalInfo;
    }

    public void setIgnoringLexicalInfo(boolean paramBoolean) {
        this.ignoringLexicalInfo = paramBoolean;
    }

    public boolean getDisableNamespaces() {
        return this.disableNamespaces;
    }

    public void setDisableNamespaces(boolean paramBoolean) {
        this.disableNamespaces = paramBoolean;
    }

    public void setParser(Parser paramParser) {
        paramParser.setDocumentHandler((DocumentHandler) this);
        if (paramParser instanceof Parser) {
            this.parser = (Parser) paramParser;
            this.parser.setDTDHandler((DTDHandler) new DtdListener(this));
        } else {
            this.parser = null;
        }
    }

    public Parser getParser() {
        return this.parser;
    }

    public XmlDocument getDocument() {
        return this.document;
    }

    public Locale getLocale() {
        if (this.parser != null)
            return this.parser.getLocale();
        return this.locale;
    }

    public void setLocale(Locale paramLocale) throws SAXException {
        if (paramLocale == null)
            paramLocale = Locale.getDefault();
        if (this.parser != null)
            this.parser.setLocale(paramLocale);
        this.locale = paramLocale;
    }

    public Locale chooseLocale(String[] paramArrayOfString) throws SAXException {
        Locale locale = XmlDocument.catalog.chooseLocale(paramArrayOfString);
        if (locale != null)
            setLocale(locale);
        return locale;
    }

    public void setDocumentLocator(Locator paramLocator) {
        this.locator = paramLocator;
    }

    public Locator getDocumentLocator() {
        return this.locator;
    }

    public XmlDocument createDocument() {
        XmlDocument xmlDocument = new XmlDocument();
        if (this.factory != null)
            xmlDocument.setElementFactory(this.factory);
        return xmlDocument;
    }

    public final void setElementFactory(ElementFactory paramElementFactory) {
        this.factory = paramElementFactory;
    }

    public final ElementFactory getElementFactory() {
        return this.factory;
    }

    public void startDocument() throws SAXException {
        this.document = createDocument();
        if (this.locator != null)
            this.document.setSystemId(this.locator.getSystemId());
        this.elementStack = new ParentNode[200];
        this.topOfStack = 0;
        this.elementStack[this.topOfStack] = this.document;
        this.inDTD = false;
        this.document.startParse(this.context);
    }

    public void endDocument() throws SAXException {
        if (this.topOfStack != 0)
            throw new IllegalStateException(getMessage("XDB-000"));
        this.document.doneParse(this.context);
        this.document.trimToSize();
    }

    private String getNamespaceURI(String paramString) {
        if ("xml".equals(paramString))
            return "http://www.w3.com/XML/1998/namespace";
        if ("xmlns".equals(paramString))
            return null;
        return this.elementStack[this.topOfStack].getInheritedAttribute("xmlns:" + paramString);
    }

    public void startElement(String paramString, AttributeList paramAttributeList) throws SAXException {
        AttributeSet attributeSet = null;
        ElementNode elementNode = null;
        byte b = (paramAttributeList == null) ? 0 : paramAttributeList.getLength();
        if (b)
            try {
                if (!this.disableNamespaces)
                    for (byte b1 = 0; b1 < b; b1++) {
                        String str = paramAttributeList.getType(b1);
                        if (!"CDATA".equals(str) && !str.startsWith("NMTOKEN"))
                            if (paramAttributeList.getValue(b1).indexOf(':') != -1)
                                error(new SAXParseException(getMessage("XDB-001", new Object[]{paramAttributeList.getName(b1)}), this.locator));
                    }
                attributeSet = new AttributeSet(paramAttributeList);
            } catch (DOMException dOMException) {
                fatal(new SAXParseException(getMessage("XDB-002", new Object[]{dOMException.getMessage()}), this.locator, dOMException));
            }
        try {
            if (this.disableNamespaces) {
                elementNode = (ElementNode) this.document.createElementEx(paramString);
            } else {
                int i = paramString.indexOf(':');
                String str1 = "xmlns";
                String str2 = "";
                String str3 = paramString;
                if (i != -1) {
                    str1 = "xmlns:" + paramString.substring(0, i);
                    str3 = paramString.substring(i + 1);
                    if (paramString.lastIndexOf(':') != i)
                        error(new SAXParseException(getMessage("XDB-003", new Object[]{paramString}), this.locator));
                }
                if (attributeSet != null)
                    str2 = attributeSet.getValue(str1);
                if ("".equals(str2))
                    str2 = this.elementStack[this.topOfStack].getInheritedAttribute(str1);
                elementNode = (ElementNode) this.document.createElementEx(str2, str3);
                if (str3 != paramString)
                    elementNode.setTag(paramString);
            }
        } catch (DOMException dOMException) {
            fatal(new SAXParseException(getMessage("XDB-004", new Object[]{dOMException.getMessage()}), this.locator, dOMException));
        }
        if (paramAttributeList != null && paramAttributeList instanceof AttributeListEx)
            elementNode.setIdAttributeName(((AttributeListEx) paramAttributeList).getIdAttributeName());
        if (b != 0)
            elementNode.setAttributes(attributeSet);
        this.elementStack[this.topOfStack++].appendChild(elementNode);
        this.elementStack[this.topOfStack] = elementNode;
        elementNode.startParse(this.context);
        if (!this.disableNamespaces) {
            int i = paramString.indexOf(':');
            if (i > 0) {
                String str = paramString.substring(0, i);
                if (getNamespaceURI(str) == null)
                    error(new SAXParseException(getMessage("XDB-005", new Object[]{str}), this.locator));
            }
            if (b != 0) {
                for (byte b1 = 0; b1 < b; b1++) {
                    String str = attributeSet.item(b1).getNodeName();
                    i = str.indexOf(':');
                    if (i > 0) {
                        String str1 = str.substring(0, i);
                        if (!"xmlns".equals(str1)) {
                            String str2 = getNamespaceURI(str1);
                            if (str2 == null) {
                                error(new SAXParseException(getMessage("XDB-006", new Object[]{str1}), this.locator));
                            } else {
                                if (str.lastIndexOf(':') != i)
                                    error(new SAXParseException(getMessage("XDB-007", new Object[]{str}), this.locator));
                                str = str.substring(i + 1);
                                str = String.valueOf(str2) + Character.MAX_VALUE + str;
                                if (this.attrTmp.contains(str)) {
                                    error(new SAXParseException(getMessage("XDB-008", new Object[]{attributeSet.item(b1).getNodeName()}), this.locator));
                                } else {
                                    this.attrTmp.addElement(str);
                                }
                            }
                        }
                    }
                }
                this.attrTmp.setSize(0);
            }
        }
    }

    public void endElement(String paramString) throws SAXException {
        ElementNode elementNode = (ElementNode) this.elementStack[this.topOfStack];
        this.elementStack[this.topOfStack--] = null;
        try {
            elementNode.doneParse(this.context);
            elementNode.reduceWaste();
            this.elementStack[this.topOfStack].doneChild(elementNode, this.context);
        } catch (DOMException dOMException) {
            fatal(new SAXParseException(getMessage("XDB-004", new Object[]{dOMException.getMessage()}), this.locator, dOMException));
        }
    }

    public void comment(String paramString) throws SAXException {
        if (this.ignoringLexicalInfo || this.inDTD)
            return;
        Comment comment = this.document.createComment(paramString);
        ParentNode parentNode = this.elementStack[this.topOfStack];
        try {
            parentNode.appendChild(comment);
            parentNode.doneChild((NodeEx) comment, this.context);
        } catch (DOMException dOMException) {
            fatal(new SAXParseException(getMessage("XDB-004", new Object[]{dOMException.getMessage()}), this.locator, dOMException));
        }
    }

    public void startCDATA() throws SAXException {
        if (this.ignoringLexicalInfo)
            return;
        CDATASection cDATASection = this.document.createCDATASection("");
        ParentNode parentNode = this.elementStack[this.topOfStack];
        try {
            this.inCDataSection = true;
            parentNode.appendChild(cDATASection);
        } catch (DOMException dOMException) {
            fatal(new SAXParseException(getMessage("XDB-004", new Object[]{dOMException.getMessage()}), this.locator, dOMException));
        }
    }

    public void endCDATA() throws SAXException {
        if (!this.inCDataSection)
            return;
        ParentNode parentNode = this.elementStack[this.topOfStack];
        try {
            this.inCDataSection = false;
            parentNode.doneChild((NodeEx) parentNode.getLastChild(), this.context);
        } catch (DOMException dOMException) {
            fatal(new SAXParseException(getMessage("XDB-004", new Object[]{dOMException.getMessage()}), this.locator, dOMException));
        }
    }

    public void characters(char[] paramArrayOfchar, int paramInt1, int paramInt2) throws SAXException {
        ParentNode parentNode = this.elementStack[this.topOfStack];
        if (this.inCDataSection) {
            String str = new String(paramArrayOfchar, paramInt1, paramInt2);
            CDATASection cDATASection = (CDATASection) parentNode.getLastChild();
            cDATASection.appendData(str);
            return;
        }
        try {
            NodeBase nodeBase = (NodeBase) parentNode.getLastChild();
            if (nodeBase instanceof TextNode) {
                String str = new String(paramArrayOfchar, paramInt1, paramInt2);
                ((TextNode) nodeBase).appendData(str);
            } else {
                TextNode textNode = this.document.newText(paramArrayOfchar, paramInt1, paramInt2);
                parentNode.appendChild(textNode);
                parentNode.doneChild(textNode, this.context);
            }
        } catch (DOMException dOMException) {
            fatal(new SAXParseException(getMessage("XDB-004", new Object[]{dOMException.getMessage()}), this.locator, dOMException));
        }
    }

    public void ignorableWhitespace(char[] paramArrayOfchar, int paramInt1, int paramInt2) throws SAXException {
        if (this.ignoringLexicalInfo)
            return;
        ParentNode parentNode = this.elementStack[this.topOfStack];
        if (this.inCDataSection) {
            String str = new String(paramArrayOfchar, paramInt1, paramInt2);
            CDATASection cDATASection = (CDATASection) parentNode.getLastChild();
            cDATASection.appendData(str);
            return;
        }
        TextNode textNode = this.document.newText(paramArrayOfchar, paramInt1, paramInt2);
        try {
            parentNode.appendChild(textNode);
            parentNode.doneChild(textNode, this.context);
        } catch (DOMException dOMException) {
            fatal(new SAXParseException(getMessage("XDB-004", new Object[]{dOMException.getMessage()}), this.locator, dOMException));
        }
    }

    public void processingInstruction(String paramString1, String paramString2) throws SAXException {
        if (!this.disableNamespaces && paramString1.indexOf(':') != -1)
            error(new SAXParseException(getMessage("XDB-010"), this.locator));
        if (this.inDTD)
            return;
        ParentNode parentNode = this.elementStack[this.topOfStack];
        try {
            PINode pINode = (PINode) this.document.createProcessingInstruction(paramString1, paramString2);
            parentNode.appendChild(pINode);
            parentNode.doneChild(pINode, this.context);
        } catch (DOMException dOMException) {
            fatal(new SAXParseException(getMessage("XDB-004", new Object[]{dOMException.getMessage()}), this.locator, dOMException));
        }
    }

    public void startParsedEntity(String paramString) throws SAXException {
        if (this.ignoringLexicalInfo)
            return;
        EntityReference entityReference = this.document.createEntityReference(paramString);
        this.elementStack[this.topOfStack++].appendChild(entityReference);
        this.elementStack[this.topOfStack] = (ParentNode) entityReference;
    }

    public void endParsedEntity(String paramString, boolean paramBoolean) throws SAXException {
        ParentNode parentNode = this.elementStack[this.topOfStack];
        if (!(parentNode instanceof EntityReference))
            return;
        parentNode.setReadonly(true);
        this.elementStack[this.topOfStack--] = null;
        if (!paramString.equals(parentNode.getNodeName()))
            fatal(new SAXParseException(getMessage("XDB-011", new Object[]{paramString, parentNode.getNodeName()}), this.locator));
        try {
            this.elementStack[this.topOfStack].doneChild(parentNode, this.context);
        } catch (DOMException dOMException) {
            fatal(new SAXParseException(getMessage("XDB-004", new Object[]{dOMException.getMessage()}), this.locator, dOMException));
        }
    }

    private void error(SAXParseException paramSAXParseException) throws SAXException {
        if (this.parser != null) {
            this.parser.getErrorHandler().error(paramSAXParseException);
        } else {
            throw paramSAXParseException;
        }
    }

    private void fatal(SAXParseException paramSAXParseException) throws SAXException {
        if (this.parser != null)
            this.parser.getErrorHandler().fatalError(paramSAXParseException);
        throw paramSAXParseException;
    }

    class ParseContextImpl implements ParseContext {
        private final XmlDocumentBuilder this$0;

        ParseContextImpl(XmlDocumentBuilder this$0) {
            this.this$0 = this$0;
        }

        public ErrorHandler getErrorHandler() {
            return (this.this$0.parser != null) ? this.this$0.parser.getErrorHandler() : null;
        }

        public Locale getLocale() {
            return this.this$0.getLocale();
        }

        public Locator getLocator() {
            return this.this$0.locator;
        }
    }

    class DtdListener implements DtdEventListener {
        private final XmlDocumentBuilder this$0;
        private Doctype doctype;
        private String publicId;
        private String systemId;
        private String internalSubset;

        DtdListener(XmlDocumentBuilder this$0) {
            this.this$0 = this$0;
        }

        public void startDtd(String param1String) {
            this.doctype = this.this$0.document.createDoctype(param1String);
            this.this$0.inDTD = true;
        }

        public void externalDtdDecl(String param1String1, String param1String2) throws SAXException {
            this.publicId = param1String1;
            this.systemId = param1String2;
        }

        public void internalDtdDecl(String param1String) throws SAXException {
            this.internalSubset = param1String;
        }

        public void externalEntityDecl(String param1String1, String param1String2, String param1String3) throws SAXException {
            if (!this.this$0.disableNamespaces && param1String1.indexOf(':') != -1)
                this.this$0.error(new SAXParseException(this.this$0.getMessage("XDB-012"), this.this$0.locator));
            this.doctype.addEntityNode(param1String1, param1String2, param1String3, null);
        }

        public void internalEntityDecl(String param1String1, String param1String2) throws SAXException {
            if (!this.this$0.disableNamespaces && param1String1.indexOf(':') != -1)
                this.this$0.error(new SAXParseException(this.this$0.getMessage("XDB-012"), this.this$0.locator));
            this.doctype.addEntityNode(param1String1, param1String2);
        }

        public void notationDecl(String param1String1, String param1String2, String param1String3) throws SAXException {
            if (!this.this$0.disableNamespaces && param1String1.indexOf(':') != -1)
                this.this$0.error(new SAXParseException(this.this$0.getMessage("XDB-013"), this.this$0.locator));
            this.doctype.addNotation(param1String1, param1String2, param1String3);
        }

        public void unparsedEntityDecl(String param1String1, String param1String2, String param1String3, String param1String4) throws SAXException {
            if (!this.this$0.disableNamespaces && param1String1.indexOf(':') != -1)
                this.this$0.error(new SAXParseException(this.this$0.getMessage("XDB-012"), this.this$0.locator));
            this.doctype.addEntityNode(param1String1, param1String2, param1String3, param1String4);
        }

        public void elementDecl(String param1String1, String param1String2) throws SAXException {
        }

        public void attributeDecl(String param1String1, String param1String2, String param1String3, String[] param1ArrayOfString, String param1String4, boolean param1Boolean1, boolean param1Boolean2)
            throws SAXException {
        }

        public void endDtd() {
            this.doctype.setPrintInfo(this.publicId, this.systemId, this.internalSubset);
            this.this$0.document.appendChild(this.doctype);
            this.this$0.inDTD = false;
        }
    }

    String getMessage(String paramString) {
        return getMessage(paramString, null);
    }

    String getMessage(String paramString, Object[] paramArrayOfObject) {
        if (this.locale == null)
            getLocale();
        return XmlDocument.catalog.getMessage(this.locale, paramString, paramArrayOfObject);
    }
}
