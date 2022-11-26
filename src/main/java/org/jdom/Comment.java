package org.jdom;

import java.io.Serializable;
import org.jdom.output.XMLOutputter;

public class Comment implements Serializable, Cloneable {
    private static final String CVS_ID = "@(#) $RCSfile: Comment.java,v $ $Revision: 1.22 $ $Date: 2002/03/20 15:16:32 $ $Name: jdom_1_0_b8 $";
    protected String text;
    protected Object parent;

    protected Comment() {
    }

    public Comment(String text) {
        setText(text);
    }

    public Element getParent() {
        if (this.parent instanceof Element)
            return (Element) this.parent;
        return null;
    }

    protected Comment setParent(Element parent) {
        this.parent = parent;
        return this;
    }

    public Comment detach() {
        if (this.parent instanceof Element) {
            ((Element) this.parent).removeContent(this);
        } else if (this.parent instanceof Document) {
            ((Document) this.parent).removeContent(this);
        }
        return this;
    }

    public Document getDocument() {
        if (this.parent instanceof Document)
            return (Document) this.parent;
        if (this.parent instanceof Element)
            return ((Element) this.parent).getDocument();
        return null;
    }

    protected Comment setDocument(Document document) {
        this.parent = document;
        return this;
    }

    public String getText() {
        return this.text;
    }

    public Comment setText(String text) {
        String reason;
        if ((reason = Verifier.checkCommentData(text)) != null)
            throw new IllegalDataException(text, "comment", reason);
        this.text = text;
        return this;
    }

    public String toString() {
        return "[Comment: " + (new XMLOutputter()).outputString(this) + "]";
    }

    public final boolean equals(Object ob) {
        return !(ob != this);
    }

    public final int hashCode() {
        return super.hashCode();
    }

    public Object clone() {
        Comment comment = null;
        try {
            comment = (Comment) super.clone();
        } catch (CloneNotSupportedException cloneNotSupportedException) {
        }
        comment.parent = null;
        return comment;
    }
}
