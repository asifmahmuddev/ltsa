package com.sun.xml.tree;

import java.io.IOException;
import java.io.Writer;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;

final class PINode extends NodeBase implements ProcessingInstruction {
    private String target;
    private char[] data;

    public PINode() {
    }

    public PINode(String paramString1, String paramString2) {
        this.data = paramString2.toCharArray();
        this.target = paramString1;
    }

    PINode(String paramString, char[] paramArrayOfchar, int paramInt1, int paramInt2) {
        this.data = new char[paramInt2];
        System.arraycopy(paramArrayOfchar, paramInt1, this.data, 0, paramInt2);
        this.target = paramString;
    }

    public short getNodeType() {
        return 7;
    }

    public String getTarget() {
        return this.target;
    }

    public void setTarget(String paramString) {
        this.target = paramString;
    }

    public String getData() {
        return new String(this.data);
    }

    public void setData(String paramString) {
        if (isReadonly())
            throw new DomEx((short) 7);
        this.data = paramString.toCharArray();
    }

    public String getNodeValue() {
        return getData();
    }

    public void setNodeValue(String paramString) {
        setData(paramString);
    }

    public void writeXml(XmlWriteContext paramXmlWriteContext) throws IOException {
        Writer writer = paramXmlWriteContext.getWriter();
        writer.write("<?");
        writer.write(this.target);
        if (this.data != null) {
            writer.write(32);
            writer.write(this.data);
        }
        writer.write("?>");
    }

    public Node cloneNode(boolean paramBoolean) {
        PINode pINode = new PINode(this.target, this.data, 0, this.data.length);
        pINode.setOwnerDocument((XmlDocument) getOwnerDocument());
        return pINode;
    }

    public String getNodeName() {
        return this.target;
    }
}
