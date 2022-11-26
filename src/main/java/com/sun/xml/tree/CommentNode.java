package com.sun.xml.tree;

import java.io.IOException;
import java.io.Writer;
import org.w3c.dom.Comment;
import org.w3c.dom.Node;

class CommentNode extends DataNode implements Comment {
    public CommentNode() {
    }

    public CommentNode(String paramString) {
        super(paramString);
    }

    CommentNode(char[] paramArrayOfchar, int paramInt1, int paramInt2) {
        super(paramArrayOfchar, paramInt1, paramInt2);
    }

    public short getNodeType() {
        return 8;
    }

    public void writeXml(XmlWriteContext paramXmlWriteContext) throws IOException {
        Writer writer = paramXmlWriteContext.getWriter();
        writer.write("<!--");
        if (this.data != null) {
            boolean bool = false;
            int i = this.data.length;
            for (byte b = 0; b < i; b++) {
                if (this.data[b] == '-')
                    if (bool) {
                        writer.write(32);
                    } else {
                        bool = true;
                        writer.write(45);
                        b++;
                    }
                bool = false;
                writer.write(this.data[b]);
            }
            if (this.data[this.data.length - 1] == '-')
                writer.write(32);
        }
        writer.write("-->");
    }

    public Node cloneNode(boolean paramBoolean) {
        CommentNode commentNode = new CommentNode(this.data, 0, this.data.length);
        commentNode.setOwnerDocument((XmlDocument) getOwnerDocument());
        return commentNode;
    }

    public String getNodeName() {
        return "#comment";
    }
}
