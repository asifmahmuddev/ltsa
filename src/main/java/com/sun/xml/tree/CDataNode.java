package com.sun.xml.tree;

import java.io.IOException;
import java.io.Writer;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Node;

class CDataNode extends TextNode implements CDATASection {
    public CDataNode() {
    }

    public CDataNode(char[] paramArrayOfchar, int paramInt1, int paramInt2) {
        super(paramArrayOfchar, paramInt1, paramInt2);
    }

    public CDataNode(String paramString) {
        super(paramString);
    }

    public void writeXml(XmlWriteContext paramXmlWriteContext) throws IOException {
        Writer writer = paramXmlWriteContext.getWriter();
        writer.write("<![CDATA[");
        for (byte b = 0; b < this.data.length; b++) {
            char c = this.data[b];
            if (c == ']' && b + 2 < this.data.length && this.data[b + 1] == ']' && this.data[b + 2] == '>') {
                writer.write("]]]]><![CDATA[>");
            } else {
                writer.write(c);
            }
        }
        writer.write("]]>");
    }

    public short getNodeType() {
        return 4;
    }

    public Node cloneNode(boolean paramBoolean) {
        CDataNode cDataNode = new CDataNode(this.data, 0, this.data.length);
        cDataNode.setOwnerDocument((XmlDocument) getOwnerDocument());
        return cDataNode;
    }

    public String getNodeName() {
        return "#cdata-section";
    }
}
