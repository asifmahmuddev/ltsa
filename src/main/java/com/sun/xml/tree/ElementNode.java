package com.sun.xml.tree;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Writer;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class ElementNode extends ParentNode implements ElementEx {
    private String tag;
    private AttributeSet attributes;
    private String idAttributeName;
    private Object userObject;
    private static final char[] tagStart = new char[]{'<', '/'};
    private static final char[] tagEnd = new char[]{' ', '/', '>'};

    public void trimToSize() {
        super.trimToSize();
        if (this.attributes != null)
            this.attributes.trimToSize();
    }

    protected void setTag(String paramString) {
        this.tag = paramString;
    }

    void setAttributes(AttributeSet paramAttributeSet) {
        AttributeSet attributeSet = this.attributes;
        if (attributeSet != null && attributeSet.isReadonly())
            throw new DomEx((short) 7);
        if (paramAttributeSet != null)
            paramAttributeSet.setNameScope(this);
        this.attributes = paramAttributeSet;
        if (attributeSet != null)
            attributeSet.setNameScope(null);
    }

    void checkChildType(int paramInt) throws DOMException {
        switch (paramInt) {
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

    public void setReadonly(boolean paramBoolean) {
        if (this.attributes != null)
            this.attributes.setReadonly();
        super.setReadonly(paramBoolean);
    }

    public String getNamespace() {
        String str1;
        if ((str1 = getPrefix()) == null)
            return getInheritedAttribute("xmlns");
        if ("xml".equals(str1) || "xmlns".equals(str1))
            return null;
        String str2 = getInheritedAttribute("xmlns:" + str1);
        if (str2 == null)
            throw new IllegalStateException(getMessage("EN-000", new Object[]{str1}));
        return str2;
    }

    public String getLocalName() {
        int i = this.tag.indexOf(':');
        if (i < 0)
            return this.tag;
        return this.tag.substring(i + 1);
    }

    public String getPrefix() {
        int i = this.tag.indexOf(':');
        return (i < 0) ? null : this.tag.substring(0, i);
    }

    public void setPrefix(String paramString) {
        int i = this.tag.indexOf(':');
        if (paramString == null) {
            if (i < 0)
                return;
            this.tag = this.tag.substring(i + 1);
            return;
        }
        StringBuffer stringBuffer = new StringBuffer(paramString);
        stringBuffer.append(':');
        if (i < 0) {
            stringBuffer.append(this.tag);
        } else {
            stringBuffer.append(this.tag.substring(i + 1));
        }
        this.tag = stringBuffer.toString();
    }

    public NamedNodeMap getAttributes() {
        if (this.attributes == null)
            this.attributes = new AttributeSet(this);
        return this.attributes;
    }

    public String toString() {
        try {
            CharArrayWriter charArrayWriter = new CharArrayWriter();
            XmlWriteContext xmlWriteContext = new XmlWriteContext(charArrayWriter);
            writeXml(xmlWriteContext);
            return charArrayWriter.toString();
        } catch (Exception exception) {
            return super.toString();
        }
    }

    public void writeXml(XmlWriteContext paramXmlWriteContext) throws IOException {
        Writer writer = paramXmlWriteContext.getWriter();
        if (this.tag == null)
            throw new IllegalStateException(getMessage("EN-002"));
        writer.write(tagStart, 0, 1);
        writer.write(this.tag);
        if (this.attributes != null)
            this.attributes.writeXml(paramXmlWriteContext);
        if (!hasChildNodes()) {
            writer.write(tagEnd, 0, 3);
        } else {
            writer.write(tagEnd, 2, 1);
            writeChildrenXml(paramXmlWriteContext);
            writer.write(tagStart, 0, 2);
            writer.write(this.tag);
            writer.write(tagEnd, 2, 1);
        }
    }

    public void setIdAttributeName(String paramString) {
        if (this.readonly)
            throw new DomEx((short) 7);
        this.idAttributeName = paramString;
    }

    public String getIdAttributeName() {
        return this.idAttributeName;
    }

    public void setUserObject(Object paramObject) {
        this.userObject = paramObject;
    }

    public Object getUserObject() {
        return this.userObject;
    }

    public short getNodeType() {
        return 1;
    }

    public String getTagName() {
        return this.tag;
    }

    public String getNodeName() {
        return this.tag;
    }

    public String getAttribute(String paramString) {
        return (this.attributes == null) ? "" : this.attributes.getValue(paramString);
    }

    public String getAttribute(String paramString1, String paramString2) {
        if (this.attributes == null)
            return "";
        Attr attr = getAttributeNode(paramString1, paramString2);
        if (attr == null)
            return "";
        return attr.getValue();
    }

    public Attr getAttributeNode(String paramString1, String paramString2) {
        if (paramString2 == null)
            return null;
        if (this.attributes != null)
            for (byte b = 0;; b++) {
                AttributeNode attributeNode = (AttributeNode) this.attributes.item(b);
                if (attributeNode == null)
                    return null;
                if (paramString2.equals(attributeNode.getName())) {
                    String str = attributeNode.getNamespace();
                    if (str != null && str.equals(paramString1))
                        return attributeNode;
                }
            }
        return null;
    }

    public void setAttribute(String paramString1, String paramString2) throws DOMException {
        if (this.readonly)
            throw new DomEx((short) 7);
        if (this.attributes == null)
            this.attributes = new AttributeSet(this);
        AttributeNode attributeNode;
        if ((attributeNode = (AttributeNode) this.attributes.getNamedItem(paramString1)) != null) {
            attributeNode.setNodeValue(paramString2);
        } else {
            attributeNode = new AttributeNode(paramString1, paramString2, true, null);
            attributeNode.setOwnerDocument((XmlDocument) getOwnerDocument());
            this.attributes.setNamedItem(attributeNode);
        }
    }

    public void removeAttribute(String paramString) throws DOMException {
        if (this.readonly)
            throw new DomEx((short) 7);
        if (this.attributes == null)
            throw new DomEx((short) 8);
        this.attributes.removeNamedItem(paramString);
    }

    public Attr getAttributeNode(String paramString) {
        if (this.attributes != null)
            return (Attr) this.attributes.getNamedItem(paramString);
        return null;
    }

    public Attr setAttributeNode(Attr paramAttr) throws DOMException {
        if (this.readonly)
            throw new DomEx((short) 7);
        if (!(paramAttr instanceof AttributeNode))
            throw new DomEx((short) 4);
        if (this.attributes == null)
            this.attributes = new AttributeSet(this);
        return (Attr) this.attributes.setNamedItem(paramAttr);
    }

    public Attr removeAttributeNode(Attr paramAttr) throws DOMException {
        if (isReadonly())
            throw new DomEx((short) 7);
        Attr attr = getAttributeNode(paramAttr.getNodeName());
        if (attr == null)
            throw new DomEx((short) 8);
        removeAttribute(attr.getNodeName());
        return attr;
    }

    public void normalize() {
        boolean bool = false;
        boolean bool1 = false;
        if (this.readonly)
            throw new DomEx((short) 7);
        byte b = 0;
        Node node;
        for (; (node = item(b)) != null; b++) {
            Node node1;
            switch (node.getNodeType()) {
                case 1 :
                    ((Element) node).normalize();
                    break;
                case 3 :
                    node1 = item(b + 1);
                    if (node1 == null || node1.getNodeType() != 3) {
                        if (!bool1) {
                            bool = "preserve".equals(getInheritedAttribute("xml:space"));
                            bool1 = true;
                        }
                        if (!bool) {
                            char[] arrayOfChar = ((TextNode) node).data;
                            if (arrayOfChar == null || arrayOfChar.length == 0) {
                                removeChild(node);
                                b--;
                                break;
                            }
                            int i = removeWhiteSpaces(arrayOfChar);
                            if (i != arrayOfChar.length) {
                                char[] arrayOfChar1 = new char[i];
                                System.arraycopy(arrayOfChar, 0, arrayOfChar1, 0, i);
                                ((TextNode) node).data = arrayOfChar1;
                            }
                        }
                        break;
                    }
                    ((TextNode) node).joinNextText();
                    b--;
                    break;
            }
        }
    }

    public int removeWhiteSpaces(char[] paramArrayOfchar) {
        byte b1 = 0, b2 = 0;
        while (b2 < paramArrayOfchar.length) {
            boolean bool = false;
            char c = paramArrayOfchar[b2++];
            if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
                c = ' ';
                bool = true;
            }
            paramArrayOfchar[b1++] = c;
            if (bool)
                while (b2 < paramArrayOfchar.length) {
                    c = paramArrayOfchar[b2];
                    if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
                        b2++;
                        continue;
                    }
                    break;
                }
        }
        return b1;
    }

    public Node cloneNode(boolean paramBoolean) {
        try {
            ElementNode elementNode = (ElementNode) getOwnerDocument().createElement(this.tag);
            if (this.attributes != null)
                elementNode.setAttributes(new AttributeSet(this.attributes, true));
            if (paramBoolean) {
                byte b = 0;
                while (true) {
                    Node node = item(b);
                    if (node != null) {
                        elementNode.appendChild(node.cloneNode(true));
                        b++;
                    }
                    break;
                }
            }
            return elementNode;
        } catch (DOMException dOMException) {
            throw new RuntimeException(getMessage("EN-001"));
        }
    }

    public void write(Writer paramWriter) throws IOException {
        writeXml(new XmlWriteContext(paramWriter));
    }
}
