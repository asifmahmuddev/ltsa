package com.sun.xml.tree;

import java.io.IOException;
import java.io.Writer;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

class TextNode extends DataNode implements Text {
    public TextNode() {
    }

    public TextNode(char[] paramArrayOfchar, int paramInt1, int paramInt2) {
        super(paramArrayOfchar, paramInt1, paramInt2);
    }

    public TextNode(String paramString) {
        super(paramString);
    }

    public void writeXml(XmlWriteContext paramXmlWriteContext) throws IOException {
        Writer writer = paramXmlWriteContext.getWriter();
        int i = 0;
        byte b = 0;
        if (this.data == null) {
            System.err.println("Null text data??");
            return;
        }
        while (b < this.data.length) {
            char c = this.data[b];
            if (c == '<') {
                writer.write(this.data, i, b - i);
                i = b + 1;
                writer.write("&lt;");
            } else if (c == '>') {
                writer.write(this.data, i, b - i);
                i = b + 1;
                writer.write("&gt;");
            } else if (c == '&') {
                writer.write(this.data, i, b - i);
                i = b + 1;
                writer.write("&amp;");
            }
            b++;
        }
        writer.write(this.data, i, b - i);
    }

    public void joinNextText() {
        Node node = getNextSibling();
        if (node == null || node.getNodeType() != 3)
            return;
        getParentNode().removeChild(node);
        char[] arrayOfChar2 = ((TextNode) node).getText();
        char[] arrayOfChar1 = new char[this.data.length + arrayOfChar2.length];
        System.arraycopy(this.data, 0, arrayOfChar1, 0, this.data.length);
        System.arraycopy(arrayOfChar2, 0, arrayOfChar1, this.data.length, arrayOfChar2.length);
        this.data = arrayOfChar1;
    }

    public short getNodeType() {
        return 3;
    }

    public Text splitText(int paramInt) throws DOMException {
        TextNode textNode;
        if (isReadonly())
            throw new DomEx((short) 7);
        try {
            textNode = new TextNode(this.data, paramInt, this.data.length - paramInt);
        } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
            throw new DomEx((short) 1);
        } catch (NegativeArraySizeException negativeArraySizeException) {
            throw new DomEx((short) 1);
        }
        getParentNode().insertBefore(textNode, getNextSibling());
        char[] arrayOfChar = new char[paramInt];
        System.arraycopy(this.data, 0, arrayOfChar, 0, paramInt);
        this.data = arrayOfChar;
        return textNode;
    }

    public Node cloneNode(boolean paramBoolean) {
        TextNode textNode = new TextNode(this.data, 0, this.data.length);
        textNode.setOwnerDocument((XmlDocument) getOwnerDocument());
        return textNode;
    }

    public String getNodeName() {
        return "#text";
    }
}
