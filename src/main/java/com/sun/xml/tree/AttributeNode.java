package com.sun.xml.tree;

import com.sun.xml.util.XmlNames;
import java.io.IOException;
import java.io.Writer;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

final class AttributeNode extends NodeBase implements Attr, NamespaceScoped {
    private String name;
    private String value;
    private boolean specified;
    private String defaultValue;
    private ElementNode nameScope;

    public AttributeNode(AttributeNode paramAttributeNode) throws DOMException {
        this(paramAttributeNode.name, paramAttributeNode.value, paramAttributeNode.specified, paramAttributeNode.defaultValue);
        this.nameScope = paramAttributeNode.nameScope;
        setOwnerDocument((XmlDocument) paramAttributeNode.getOwnerDocument());
    }

    public AttributeNode(String paramString1, String paramString2, boolean paramBoolean, String paramString3) throws DOMException {
        if (!XmlNames.isName(paramString1))
            throw new DomEx((short) 5);
        this.name = paramString1;
        this.value = paramString2;
        this.specified = paramBoolean;
        this.defaultValue = paramString3;
    }

    void setNameScope(ElementNode paramElementNode) {
        if (paramElementNode != null && this.nameScope != null)
            throw new IllegalStateException(getMessage("A-000", new Object[]{paramElementNode.getTagName()}));
        this.nameScope = paramElementNode;
    }

    ElementNode getNameScope() {
        return this.nameScope;
    }

    String getDefaultValue() {
        return this.defaultValue;
    }

    public String getNamespace() {
        if (this.nameScope == null)
            throw new IllegalStateException(getMessage("A-001"));
        String str1;
        if ((str1 = getPrefix()) == null)
            return this.nameScope.getNamespace();
        if ("xml".equals(str1) || "xmlns".equals(str1))
            return null;
        String str2 = this.nameScope.getInheritedAttribute("xmlns:" + str1);
        if (str2 == null)
            throw new IllegalStateException();
        return str2;
    }

    public String getLocalName() {
        int i = this.name.indexOf(':');
        if (i < 0)
            return this.name;
        return this.name.substring(i + 1);
    }

    public String getPrefix() {
        int i = this.name.indexOf(':');
        return (i < 0) ? null : this.name.substring(0, i);
    }

    public void setPrefix(String paramString) {
        int i = this.name.indexOf(':');
        if (paramString == null) {
            if (i < 0)
                return;
            this.name = this.name.substring(i + 1);
            return;
        }
        StringBuffer stringBuffer = new StringBuffer(paramString);
        stringBuffer.append(':');
        if (i < 0) {
            stringBuffer.append(this.name);
        } else {
            stringBuffer.append(this.name.substring(i + 1));
        }
        this.name = stringBuffer.toString();
    }

    public short getNodeType() {
        return 2;
    }

    public String getName() {
        return this.name;
    }

    public String getNodeName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String paramString) {
        setNodeValue(paramString);
    }

    public String getNodeValue() {
        return this.value;
    }

    public boolean getSpecified() {
        return this.specified;
    }

    public void setNodeValue(String paramString) {
        if (isReadonly())
            throw new DomEx((short) 7);
        this.value = paramString;
        this.specified = true;
    }

    void setSpecified(boolean paramBoolean) {
        this.specified = paramBoolean;
    }

    public Node getParentNode() {
        return null;
    }

    public Node getNextSibling() {
        return null;
    }

    public Node getPreviousSibling() {
        return null;
    }

    public void writeXml(XmlWriteContext paramXmlWriteContext) throws IOException {
        Writer writer = paramXmlWriteContext.getWriter();
        writer.write(this.name);
        writer.write("=\"");
        writeChildrenXml(paramXmlWriteContext);
        writer.write(34);
    }

    public void writeChildrenXml(XmlWriteContext paramXmlWriteContext) throws IOException {
        Writer writer = paramXmlWriteContext.getWriter();
        for (byte b = 0; b < this.value.length(); b++) {
            char c = this.value.charAt(b);
            switch (c) {
                case '<' :
                    writer.write("&lt;");
                    break;
                case '>' :
                    writer.write("&gt;");
                    break;
                case '&' :
                    writer.write("&amp;");
                    break;
                case '\'' :
                    writer.write("&apos;");
                    break;
                case '"' :
                    writer.write("&quot;");
                    break;
                default :
                    writer.write(c);
                    break;
            }
        }
    }

    public Node cloneNode(boolean paramBoolean) {
        try {
            AttributeNode attributeNode = new AttributeNode(this.name, this.value, this.specified, this.defaultValue);
            attributeNode.setOwnerDocument((XmlDocument) getOwnerDocument());
            return attributeNode;
        } catch (DOMException dOMException) {
            throw new RuntimeException(getMessage("A-002"));
        }
    }
}
