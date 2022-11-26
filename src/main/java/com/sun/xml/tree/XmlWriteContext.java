package com.sun.xml.tree;

import java.io.IOException;
import java.io.Writer;

public class XmlWriteContext {
    private Writer writer;
    private int indentLevel;
    private boolean prettyOutput;

    public XmlWriteContext(Writer paramWriter) {
        this.writer = paramWriter;
    }

    public XmlWriteContext(Writer paramWriter, int paramInt) {
        this.writer = paramWriter;
        this.prettyOutput = true;
        this.indentLevel = paramInt;
    }

    public Writer getWriter() {
        return this.writer;
    }

    public boolean isEntityDeclared(String paramString) {
        return !(!"amp".equals(paramString) && !"lt".equals(paramString) && !"gt".equals(paramString) && !"quot".equals(paramString) && !"apos".equals(paramString));
    }

    public int getIndentLevel() {
        return this.indentLevel;
    }

    public void setIndentLevel(int paramInt) {
        this.indentLevel = paramInt;
    }

    public void printIndent() throws IOException {
        int i = this.indentLevel;
        if (!this.prettyOutput)
            return;
        this.writer.write(XmlDocument.eol);
        while (i >= 8) {
            this.writer.write(9);
            i -= 8;
        }
        while (i-- > 0)
            this.writer.write(32);
    }

    public boolean isPrettyOutput() {
        return this.prettyOutput;
    }
}
