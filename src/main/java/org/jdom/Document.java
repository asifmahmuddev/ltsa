package org.jdom;

import java.io.Serializable;
import java.util.List;
import org.jdom.filter.Filter;

public class Document implements Serializable, Cloneable {
    private static final String CVS_ID = "@(#) $RCSfile: Document.java,v $ $Revision: 1.55 $ $Date: 2002/03/28 11:08:12 $ $Name: jdom_1_0_b8 $";
    protected ContentList content = new ContentList(this);
    protected DocType docType;

    public Document(Element rootElement, DocType docType) {
        if (rootElement != null)
            setRootElement(rootElement);
        setDocType(docType);
    }

    public Document(Element rootElement) {
        this(rootElement, (DocType) null);
    }

    public Document(List newContent, DocType docType) {
        setContent(newContent);
        setDocType(docType);
    }

    public Document(List content) {
        this(content, (DocType) null);
    }

    public boolean hasRootElement() {
        return !(this.content.indexOfFirstElement() < 0);
    }

    public Element getRootElement() {
        int index = this.content.indexOfFirstElement();
        if (index < 0)
            throw new IllegalStateException("Root element not set");
        return (Element) this.content.get(index);
    }

    public Document setRootElement(Element rootElement) {
        int index = this.content.indexOfFirstElement();
        if (index < 0) {
            this.content.add(rootElement);
        } else {
            this.content.set(index, rootElement);
        }
        return this;
    }

    public Element detachRootElement() {
        int index = this.content.indexOfFirstElement();
        if (index < 0)
            return null;
        return (Element) removeContent(index);
    }

    private Object removeContent(int index) {
        return this.content.remove(index);
    }

    public DocType getDocType() {
        return this.docType;
    }

    public Document setDocType(DocType docType) {
        if (docType != null) {
            if (docType.getDocument() != null)
                throw new IllegalAddException(this, docType, "The docType already is attached to a document");
            docType.setDocument(this);
        }
        if (this.docType != null)
            this.docType.setDocument(null);
        this.docType = docType;
        return this;
    }

    public Document addContent(ProcessingInstruction pi) {
        this.content.add(pi);
        return this;
    }

    public Document addContent(Comment comment) {
        this.content.add(comment);
        return this;
    }

    public List getContent() {
        if (!hasRootElement())
            throw new IllegalStateException("Root element not set");
        return this.content;
    }

    public List getContent(Filter filter) {
        if (!hasRootElement())
            throw new IllegalStateException("Root element not set");
        return this.content.getView(filter);
    }

    public Document setContent(List newContent) {
        this.content.clearAndSet(newContent);
        return this;
    }

    public boolean removeContent(ProcessingInstruction pi) {
        return this.content.remove(pi);
    }

    public boolean removeContent(Comment comment) {
        return this.content.remove(comment);
    }

    public String toString() {
        StringBuffer stringForm = (new StringBuffer()).append("[Document: ");
        if (this.docType != null) {
            stringForm.append(this.docType.toString()).append(", ");
        } else {
            stringForm.append(" No DOCTYPE declaration, ");
        }
        Element rootElement = getRootElement();
        if (rootElement != null) {
            stringForm.append("Root is ").append(rootElement.toString());
        } else {
            stringForm.append(" No root element");
        }
        stringForm.append("]");
        return stringForm.toString();
    }

    public final boolean equals(Object ob) {
        return !(ob != this);
    }

    public final int hashCode() {
        return super.hashCode();
    }

    public Object clone() {
        Document doc = null;
        try {
            doc = (Document) super.clone();
        } catch (CloneNotSupportedException cloneNotSupportedException) {
        }
        if (this.docType != null)
            doc.docType = (DocType) this.docType.clone();
        doc.content = new ContentList(doc);
        for (int i = 0; i < this.content.size(); i++) {
            Object obj = this.content.get(i);
            if (obj instanceof Element) {
                Element element = (Element) ((Element) obj).clone();
                doc.content.add(element);
            } else if (obj instanceof Comment) {
                Comment comment = (Comment) ((Comment) obj).clone();
                doc.content.add(comment);
            } else if (obj instanceof ProcessingInstruction) {
                ProcessingInstruction pi = (ProcessingInstruction) ((ProcessingInstruction) obj).clone();
                doc.content.add(pi);
            }
        }
        return doc;
    }

    public Document() {
    }
}
