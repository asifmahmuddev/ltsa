package org.jdom.output;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.List;
import org.jdom.Attribute;
import org.jdom.CDATA;
import org.jdom.Comment;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.EntityRef;
import org.jdom.Namespace;
import org.jdom.ProcessingInstruction;
import org.jdom.Text;

public class XMLOutputter implements Cloneable {
    private static final String CVS_ID = "@(#) $RCSfile: XMLOutputter.java,v $ $Revision: 1.76 $ $Date: 2002/03/15 05:36:48 $ $Name: jdom_1_0_b8 $";
    private boolean omitDeclaration = false;
    private String encoding = "UTF-8";
    private boolean omitEncoding = false;
    private static final String STANDARD_INDENT = "  ";
    private static final String STANDARD_LINE_SEPARATOR = "\r\n";

    class Format implements Cloneable {
        private final XMLOutputter this$0;
        String indent;
        boolean expandEmptyElements;
        String lineSeparator;
        boolean trimAllWhite;
        boolean textTrim;
        boolean textNormalize;
        boolean newlines;

        Format(XMLOutputter this$0) {
            this.this$0 = this$0;
            this.indent = null;
            this.expandEmptyElements = false;
            this.lineSeparator = "\r\n";
            this.trimAllWhite = false;
            this.textTrim = false;
            this.textNormalize = false;
            this.newlines = false;
        }

        protected Object clone() {
            Format format = null;
            try {
                format = (Format) super.clone();
            } catch (CloneNotSupportedException cloneNotSupportedException) {
            }
            return format;
        }
    }

    Format noFormatting = new Format(this);
    Format defaultFormat = new Format(this);
    Format currentFormat = this.defaultFormat;

    public XMLOutputter(String indent) {
        setIndent(indent);
    }

    public XMLOutputter(String indent, boolean newlines) {
        setIndent(indent);
        setNewlines(newlines);
    }

    public XMLOutputter(String indent, boolean newlines, String encoding) {
        setEncoding(encoding);
        setIndent(indent);
        setNewlines(newlines);
    }

    public XMLOutputter(XMLOutputter that) {
        this.encoding = that.encoding;
        this.omitDeclaration = that.omitDeclaration;
        this.omitEncoding = that.omitEncoding;
        this.defaultFormat = (Format) that.defaultFormat.clone();
    }

    public void setLineSeparator(String separator) {
        this.defaultFormat.lineSeparator = separator;
    }

    public void setNewlines(boolean newlines) {
        this.defaultFormat.newlines = newlines;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setOmitEncoding(boolean omitEncoding) {
        this.omitEncoding = omitEncoding;
    }

    public void setOmitDeclaration(boolean omitDeclaration) {
        this.omitDeclaration = omitDeclaration;
    }

    public void setExpandEmptyElements(boolean expandEmptyElements) {
        this.defaultFormat.expandEmptyElements = expandEmptyElements;
    }

    public void setTrimAllWhite(boolean trimAllWhite) {
        this.defaultFormat.trimAllWhite = trimAllWhite;
    }

    public void setTextTrim(boolean textTrim) {
        this.defaultFormat.textTrim = textTrim;
    }

    public void setTextNormalize(boolean textNormalize) {
        this.defaultFormat.textNormalize = textNormalize;
    }

    public void setIndent(String indent) {
        if ("".equals(indent))
            indent = null;
        this.defaultFormat.indent = indent;
    }

    public void setIndent(boolean doIndent) {
        if (doIndent) {
            this.defaultFormat.indent = "  ";
        } else {
            this.defaultFormat.indent = null;
        }
    }

    public void setIndent(int size) {
        setIndentSize(size);
    }

    public void setIndentSize(int indentSize) {
        StringBuffer indentBuffer = new StringBuffer();
        for (int i = 0; i < indentSize; i++)
            indentBuffer.append(" ");
        this.defaultFormat.indent = indentBuffer.toString();
    }

    public void output(Document doc, OutputStream out) throws IOException {
        Writer writer = makeWriter(out);
        output(doc, writer);
    }

    public void output(DocType doctype, OutputStream out) throws IOException {
        Writer writer = makeWriter(out);
        output(doctype, writer);
    }

    public void output(Element element, OutputStream out) throws IOException {
        Writer writer = makeWriter(out);
        output(element, writer);
    }

    public void outputElementContent(Element element, OutputStream out) throws IOException {
        Writer writer = makeWriter(out);
        outputElementContent(element, writer);
    }

    public void output(List list, OutputStream out) throws IOException {
        Writer writer = makeWriter(out);
        output(list, writer);
    }

    public void output(CDATA cdata, OutputStream out) throws IOException {
        Writer writer = makeWriter(out);
        output(cdata, writer);
    }

    public void output(Text text, OutputStream out) throws IOException {
        Writer writer = makeWriter(out);
        output(text, writer);
    }

    public void output(String string, OutputStream out) throws IOException {
        Writer writer = makeWriter(out);
        output(string, writer);
    }

    public void output(Comment comment, OutputStream out) throws IOException {
        Writer writer = makeWriter(out);
        output(comment, writer);
    }

    public void output(ProcessingInstruction pi, OutputStream out) throws IOException {
        Writer writer = makeWriter(out);
        output(pi, writer);
    }

    public void output(EntityRef entity, OutputStream out) throws IOException {
        Writer writer = makeWriter(out);
        output(entity, writer);
    }

    protected Writer makeWriter(OutputStream out) throws UnsupportedEncodingException {
        return makeWriter(out, this.encoding);
    }

    protected Writer makeWriter(OutputStream out, String enc) throws UnsupportedEncodingException {
        if ("UTF-8".equals(enc))
            enc = "UTF8";
        Writer writer = new BufferedWriter(new OutputStreamWriter(new BufferedOutputStream(out), enc));
        return writer;
    }

    public void output(Document doc, Writer out) throws IOException {
        printDeclaration(doc, out, this.encoding);
        if (doc.getDocType() != null)
            printDocType(doc.getDocType(), out);
        List content = doc.getContent();
        for (int i = 0; i < content.size(); i++) {
            Object obj = content.get(i);
            if (obj instanceof Element) {
                printElement(doc.getRootElement(), out, 0, createNamespaceStack());
            } else if (obj instanceof Comment) {
                printComment((Comment) obj, out);
            } else if (obj instanceof ProcessingInstruction) {
                printProcessingInstruction((ProcessingInstruction) obj, out);
            }
            newline(out);
            indent(out, 0);
        }
        out.write(this.currentFormat.lineSeparator);
        out.flush();
    }

    public void output(DocType doctype, Writer out) throws IOException {
        printDocType(doctype, out);
        out.flush();
    }

    public void output(Element element, Writer out) throws IOException {
        printElement(element, out, 0, createNamespaceStack());
        out.flush();
    }

    public void outputElementContent(Element element, Writer out) throws IOException {
        printContent(element.getContent(), out, 0, createNamespaceStack());
        out.flush();
    }

    public void output(List list, Writer out) throws IOException {
        printContent(list, out, 0, createNamespaceStack());
        out.flush();
    }

    public void output(CDATA cdata, Writer out) throws IOException {
        printCDATA(cdata, out);
        out.flush();
    }

    public void output(Text text, Writer out) throws IOException {
        printText(text, out);
        out.flush();
    }

    public void output(String string, Writer out) throws IOException {
        printString(string, out);
        out.flush();
    }

    public void output(Comment comment, Writer out) throws IOException {
        printComment(comment, out);
        out.flush();
    }

    public void output(ProcessingInstruction pi, Writer out) throws IOException {
        printProcessingInstruction(pi, out);
        out.flush();
    }

    public void output(EntityRef entity, Writer out) throws IOException {
        printEntityRef(entity, out);
        out.flush();
    }

    public String outputString(Document doc) {
        StringWriter out = new StringWriter();
        try {
            output(doc, out);
        } catch (IOException iOException) {
        }
        return out.toString();
    }

    public String outputString(DocType doctype) {
        StringWriter out = new StringWriter();
        try {
            output(doctype, out);
        } catch (IOException iOException) {
        }
        return out.toString();
    }

    public String outputString(Element element) {
        StringWriter out = new StringWriter();
        try {
            output(element, out);
        } catch (IOException iOException) {
        }
        return out.toString();
    }

    public String outputString(List list) {
        StringWriter out = new StringWriter();
        try {
            output(list, out);
        } catch (IOException iOException) {
        }
        return out.toString();
    }

    public String outputString(CDATA cdata) {
        StringWriter out = new StringWriter();
        try {
            output(cdata, out);
        } catch (IOException iOException) {
        }
        return out.toString();
    }

    public String outputString(Text text) {
        StringWriter out = new StringWriter();
        try {
            output(text, out);
        } catch (IOException iOException) {
        }
        return out.toString();
    }

    public String outputString(String str) {
        StringWriter out = new StringWriter();
        try {
            output(str, out);
        } catch (IOException iOException) {
        }
        return out.toString();
    }

    public String outputString(Comment comment) {
        StringWriter out = new StringWriter();
        try {
            output(comment, out);
        } catch (IOException iOException) {
        }
        return out.toString();
    }

    public String outputString(ProcessingInstruction pi) {
        StringWriter out = new StringWriter();
        try {
            output(pi, out);
        } catch (IOException iOException) {
        }
        return out.toString();
    }

    public String outputString(EntityRef entity) {
        StringWriter out = new StringWriter();
        try {
            output(entity, out);
        } catch (IOException iOException) {
        }
        return out.toString();
    }

    protected void printDeclaration(Document doc, Writer out, String encoding) throws IOException {
        if (!this.omitDeclaration) {
            out.write("<?xml version=\"1.0\"");
            if (!this.omitEncoding)
                out.write(" encoding=\"" + encoding + "\"");
            out.write("?>");
            out.write(this.currentFormat.lineSeparator);
        }
    }

    protected void printDocType(DocType docType, Writer out) throws IOException {
        String publicID = docType.getPublicID();
        String systemID = docType.getSystemID();
        String internalSubset = docType.getInternalSubset();
        boolean hasPublic = false;
        out.write("<!DOCTYPE ");
        out.write(docType.getElementName());
        if (publicID != null) {
            out.write(" PUBLIC \"");
            out.write(publicID);
            out.write("\"");
            hasPublic = true;
        }
        if (systemID != null) {
            if (!hasPublic)
                out.write(" SYSTEM");
            out.write(" \"");
            out.write(systemID);
            out.write("\"");
        }
        if (internalSubset != null && !internalSubset.equals("")) {
            out.write(" [\n");
            out.write(docType.getInternalSubset());
            out.write("]");
        }
        out.write(">");
        out.write(this.currentFormat.lineSeparator);
    }

    protected void printComment(Comment comment, Writer out) throws IOException {
        out.write("<!--");
        out.write(comment.getText());
        out.write("-->");
    }

    protected void printProcessingInstruction(ProcessingInstruction pi, Writer out) throws IOException {
        String target = pi.getTarget();
        String rawData = pi.getData();
        if (!"".equals(rawData)) {
            out.write("<?");
            out.write(target);
            out.write(" ");
            out.write(rawData);
            out.write("?>");
        } else {
            out.write("<?");
            out.write(target);
            out.write("?>");
        }
    }

    protected void printEntityRef(EntityRef entity, Writer out) throws IOException {
        out.write("&");
        out.write(entity.getName());
        out.write(";");
    }

    protected void printCDATA(CDATA cdata, Writer out) throws IOException {
        String str = this.currentFormat.textNormalize ? cdata.getTextNormalize() : (this.currentFormat.textTrim ? cdata.getText().trim() : cdata.getText());
        out.write("<![CDATA[");
        out.write(str);
        out.write("]]>");
    }

    protected void printText(Text text, Writer out) throws IOException {
        String str = this.currentFormat.textNormalize ? text.getTextNormalize() : (this.currentFormat.textTrim ? text.getText().trim() : text.getText());
        out.write(escapeElementEntities(str));
    }

    protected void printString(String str, Writer out) throws IOException {
        if (this.currentFormat.textNormalize) {
            str = Text.normalizeString(str);
        } else if (this.currentFormat.textTrim) {
            str = str.trim();
        }
        out.write(escapeElementEntities(str));
    }

    protected void printElement(Element element, Writer out, int level, NamespaceStack namespaces) throws IOException {
        List attributes = element.getAttributes();
        List content = element.getContent();
        String space = null;
        if (attributes != null)
            space = element.getAttributeValue("space", Namespace.XML_NAMESPACE);
        Format previousFormat = this.currentFormat;
        if ("default".equals(space)) {
            this.currentFormat = this.defaultFormat;
        } else if ("preserve".equals(space)) {
            this.currentFormat = this.noFormatting;
        }
        out.write("<");
        out.write(element.getQualifiedName());
        int previouslyDeclaredNamespaces = namespaces.size();
        printElementNamespace(element, out, namespaces);
        printAdditionalNamespaces(element, out, namespaces);
        if (attributes != null)
            printAttributes(attributes, element, out, namespaces);
        int start = skipLeadingWhite(content, 0);
        if (start >= content.size()) {
            if (this.currentFormat.expandEmptyElements) {
                out.write("></");
                out.write(element.getQualifiedName());
                out.write(">");
            } else {
                out.write(" />");
            }
        } else {
            out.write(">");
            if (nextNonText(content, start) < content.size()) {
                newline(out);
                printContentRange(content, start, content.size(), out, level + 1, namespaces);
                newline(out);
                indent(out, level);
            } else {
                printTextRange(content, start, content.size(), out);
            }
            out.write("</");
            out.write(element.getQualifiedName());
            out.write(">");
        }
        while (namespaces.size() > previouslyDeclaredNamespaces)
            namespaces.pop();
        this.currentFormat = previousFormat;
    }

    protected void printElementContent(Element element, Writer out, int level, NamespaceStack namespaces) throws IOException {
        printContent(element.getContent(), out, level, namespaces);
    }

    protected void printContent(List content, Writer out, int level, NamespaceStack namespaces) throws IOException {
        printContentRange(content, 0, content.size(), out, level, namespaces);
    }

    protected void printContentRange(List content, int start, int end, Writer out, int level, NamespaceStack namespaces) throws IOException {
        int index = start;
        while (index < end) {
            boolean firstNode = (index == start);
            Object next = content.get(index);
            if (next instanceof CDATA || next instanceof Text) {
                int first = skipLeadingWhite(content, index);
                index = nextNonText(content, first);
                if (first < index) {
                    if (!firstNode)
                        newline(out);
                    indent(out, level);
                    printTextRange(content, first, index, out);
                }
                continue;
            }
            if (!firstNode)
                newline(out);
            indent(out, level);
            if (next instanceof Comment) {
                printComment((Comment) next, out);
            } else if (next instanceof Element) {
                printElement((Element) next, out, level, namespaces);
            } else if (next instanceof EntityRef) {
                printEntityRef((EntityRef) next, out);
            } else if (next instanceof ProcessingInstruction) {
                printProcessingInstruction((ProcessingInstruction) next, out);
            }
            index++;
        }
    }

    protected void printTextRange(List content, int start, int end, Writer out) throws IOException {
        String previous = null;
        start = skipLeadingWhite(content, start);
        if (start < content.size()) {
            end = skipTrialingWhite(content, end);
            for (int i = start; i < end; i++) {
                String next;
                Object node = content.get(i);
                if (node instanceof CDATA) {
                    next = ((CDATA) node).getText();
                } else {
                    next = ((Text) node).getText();
                }
                if (next != null && !"".equals(next)) {
                    if (previous != null && (this.currentFormat.textNormalize || this.currentFormat.textTrim) && (endsWithWhite(previous) || startsWithWhite(next)))
                        out.write(" ");
                    if (node instanceof CDATA) {
                        printCDATA((CDATA) node, out);
                    } else {
                        printString(next, out);
                    }
                    previous = next;
                }
            }
        }
    }

    private void printNamespace(Namespace ns, Writer out, NamespaceStack namespaces) throws IOException {
        String prefix = ns.getPrefix();
        String uri = ns.getURI();
        if (uri.equals(namespaces.getURI(prefix)))
            return;
        out.write(" xmlns");
        if (!prefix.equals("")) {
            out.write(":");
            out.write(prefix);
        }
        out.write("=\"");
        out.write(uri);
        out.write("\"");
        namespaces.push(ns);
    }

    protected void printAttributes(List attributes, Element parent, Writer out, NamespaceStack namespaces) throws IOException {
        for (int i = 0; i < attributes.size(); i++) {
            Attribute attribute = attributes.get(i);
            Namespace ns = attribute.getNamespace();
            if (ns != Namespace.NO_NAMESPACE && ns != Namespace.XML_NAMESPACE)
                printNamespace(ns, out, namespaces);
            out.write(" ");
            out.write(attribute.getQualifiedName());
            out.write("=");
            out.write("\"");
            out.write(escapeAttributeEntities(attribute.getValue()));
            out.write("\"");
        }
    }

    private void printElementNamespace(Element element, Writer out, NamespaceStack namespaces) throws IOException {
        Namespace ns = element.getNamespace();
        if (ns == Namespace.XML_NAMESPACE)
            return;
        if (ns != Namespace.NO_NAMESPACE || namespaces.getURI("") != null)
            printNamespace(ns, out, namespaces);
    }

    private void printAdditionalNamespaces(Element element, Writer out, NamespaceStack namespaces) throws IOException {
        List list = element.getAdditionalNamespaces();
        if (list != null)
            for (int i = 0; i < list.size(); i++) {
                Namespace additional = list.get(i);
                printNamespace(additional, out, namespaces);
            }
    }

    protected void newline(Writer out) throws IOException {
        if (this.currentFormat.newlines)
            out.write(this.currentFormat.lineSeparator);
    }

    protected void indent(Writer out) throws IOException {
        indent(out, 0);
    }

    protected void indent(Writer out, int level) throws IOException {
        if (this.currentFormat.newlines) {
            if (this.currentFormat.indent == null || this.currentFormat.indent.equals(""))
                return;
            for (int i = 0; i < level; i++)
                out.write(this.currentFormat.indent);
        }
    }

    private int skipLeadingWhite(List content, int start) {
        if (start < 0)
            start = 0;
        int index = start;
        if (this.currentFormat.trimAllWhite || this.currentFormat.textNormalize || this.currentFormat.textTrim || this.currentFormat.newlines)
            while (index < content.size() && isAllWhitespace(content.get(index)))
                index++;
        return index;
    }

    private int skipTrialingWhite(List content, int start) {
        if (start > content.size())
            start = content.size();
        int index = start;
        if (this.currentFormat.trimAllWhite || this.currentFormat.textNormalize || this.currentFormat.textTrim || this.currentFormat.newlines)
            while (index >= 0 && isAllWhitespace(content.get(index - 1)))
                index--;
        return index;
    }

    private int nextNonText(List content, int start) {
        if (start < 0)
            start = 0;
        int index = start;
        while (index < content.size() && (content.get(index) instanceof CDATA || content.get(index) instanceof Text))
            index++;
        return index;
    }

    private boolean isAllWhitespace(Object obj) {
        String str = null;
        if (obj instanceof String) {
            str = (String) obj;
        } else if (obj instanceof CDATA) {
            str = ((CDATA) obj).getText();
        } else if (obj instanceof Text) {
            str = ((Text) obj).getText();
        } else {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!isWhitespace(str.charAt(i)))
                return false;
        }
        return true;
    }

    private boolean startsWithWhite(String str) {
        if (str != null && str.length() > 0 && isWhitespace(str.charAt(0)))
            return true;
        return false;
    }

    private boolean endsWithWhite(String str) {
        if (str != null && str.length() > 0 && isWhitespace(str.charAt(str.length() - 1)))
            return true;
        return false;
    }

    private boolean isWhitespace(char ch) {
        if (" \t\n\r".indexOf(ch) < 0)
            return false;
        return true;
    }

    public String escapeAttributeEntities(String str) {
        StringBuffer buffer = null;
        for (int i = 0; i < str.length(); i++) {
            String entity;
            char ch = str.charAt(i);
            switch (ch) {
                case '<' :
                    entity = "&lt;";
                    break;
                case '>' :
                    entity = "&gt;";
                    break;
                case '"' :
                    entity = "&quot;";
                    break;
                case '&' :
                    entity = "&amp;";
                    break;
                default :
                    entity = null;
                    break;
            }
            if (buffer == null) {
                if (entity != null) {
                    buffer = new StringBuffer(str.length() + 20);
                    buffer.append(str.substring(0, i));
                    buffer.append(entity);
                }
            } else if (entity == null) {
                buffer.append(ch);
            } else {
                buffer.append(entity);
            }
        }
        return (buffer == null) ? str : buffer.toString();
    }

    public String escapeElementEntities(String str) {
        StringBuffer buffer = null;
        for (int i = 0; i < str.length(); i++) {
            String entity;
            char ch = str.charAt(i);
            switch (ch) {
                case '<' :
                    entity = "&lt;";
                    break;
                case '>' :
                    entity = "&gt;";
                    break;
                case '&' :
                    entity = "&amp;";
                    break;
                default :
                    entity = null;
                    break;
            }
            if (buffer == null) {
                if (entity != null) {
                    buffer = new StringBuffer(str.length() + 20);
                    buffer.append(str.substring(0, i));
                    buffer.append(entity);
                }
            } else if (entity == null) {
                buffer.append(ch);
            } else {
                buffer.append(entity);
            }
        }
        return (buffer == null) ? str : buffer.toString();
    }

    public int parseArgs(String[] args, int i) {
        for (; i < args.length; i++) {
            if (args[i].equals("-omitDeclaration")) {
                setOmitDeclaration(true);
            } else if (args[i].equals("-omitEncoding")) {
                setOmitEncoding(true);
            } else if (args[i].equals("-indent")) {
                setIndent(args[++i]);
            } else if (args[i].equals("-indentSize")) {
                setIndentSize(Integer.parseInt(args[++i]));
            } else if (args[i].startsWith("-expandEmpty")) {
                setExpandEmptyElements(true);
            } else if (args[i].equals("-encoding")) {
                setEncoding(args[++i]);
            } else if (args[i].equals("-newlines")) {
                setNewlines(true);
            } else if (args[i].equals("-lineSeparator")) {
                setLineSeparator(args[++i]);
            } else if (args[i].equals("-trimAllWhite")) {
                setTrimAllWhite(true);
            } else if (args[i].equals("-textTrim")) {
                setTextTrim(true);
            } else if (args[i].equals("-textNormalize")) {
                setTextNormalize(true);
            } else {
                return i;
            }
        }
        return i;
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e.toString());
        }
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < this.defaultFormat.lineSeparator.length(); i++) {
            char ch = this.defaultFormat.lineSeparator.charAt(i);
            switch (ch) {
                case '\r' :
                    buffer.append("\\r");
                    break;
                case '\n' :
                    buffer.append("\\n");
                    break;
                case '\t' :
                    buffer.append("\\t");
                    break;
                default :
                    buffer.append("[" + ch + "]");
                    break;
            }
        }
        return "XMLOutputter[omitDeclaration = " + this.omitDeclaration + ", " + "encoding = " + this.encoding + ", " + "omitEncoding = " + this.omitEncoding + ", " + "indent = '"
            + this.defaultFormat.indent + "'" + ", " + "expandEmptyElements = " + this.defaultFormat.expandEmptyElements + ", " + "newlines = " + this.defaultFormat.newlines + ", "
            + "lineSeparator = '" + buffer.toString() + "', " + "trimAllWhite = " + this.defaultFormat.trimAllWhite + "textTrim = " + this.defaultFormat.textTrim + "textNormalize = "
            + this.defaultFormat.textNormalize + "]";
    }

    protected NamespaceStack createNamespaceStack() {
        return new NamespaceStack(this);
    }

    protected class NamespaceStack extends NamespaceStack {
        private final XMLOutputter this$0;

        protected NamespaceStack(XMLOutputter this$0) {
            this.this$0 = this$0;
        }
    }

    public void setPadText(boolean padText) {
    }

    public void setIndentLevel(int level) {
    }

    public void setSuppressDeclaration(boolean suppressDeclaration) {
        this.omitDeclaration = suppressDeclaration;
    }

    public XMLOutputter() {
    }
}
