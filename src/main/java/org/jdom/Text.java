package org.jdom;

import java.io.Serializable;

public class Text implements Serializable, Cloneable {
    private static final String CVS_ID = "@(#) $RCSfile: Text.java,v $ $Revision: 1.11 $ $Date: 2002/03/12 07:11:39 $ $Name: jdom_1_0_b8 $";
    private static final String EMPTY_STRING = "";
    protected String value;
    protected Object parent;

    protected Text() {
    }

    public Text(String str) {
        setText(str);
    }

    public String getText() {
        return this.value;
    }

    public String getTextTrim() {
        return getText().trim();
    }

    public String getTextNormalize() {
        return normalizeString(getText());
    }

    public static String normalizeString(String str) {
        if (str == null)
            return "";
        char[] c = str.toCharArray();
        char[] n = new char[c.length];
        boolean white = true;
        int pos = 0;
        for (int i = 0; i < c.length; i++) {
            if (" \t\n\r".indexOf(c[i]) != -1) {
                if (!white) {
                    n[pos++] = ' ';
                    white = true;
                }
            } else {
                n[pos++] = c[i];
                white = false;
            }
        }
        if (white && pos > 0)
            pos--;
        return new String(n, 0, pos);
    }

    public Text setText(String str) {
        if (str == null) {
            this.value = "";
            return this;
        }
        String reason;
        if ((reason = Verifier.checkCharacterData(str)) != null)
            throw new IllegalDataException(str, "character content", reason);
        this.value = str;
        return this;
    }

    public void append(String str) {
        if (str == null)
            return;
        String reason;
        if ((reason = Verifier.checkCharacterData(str)) != null)
            throw new IllegalDataException(str, "character content", reason);
        if (str == "") {
            this.value = str;
        } else {
            this.value = String.valueOf(this.value) + str;
        }
    }

    public void append(Text text) {
        if (text == null)
            return;
        this.value = String.valueOf(this.value) + text.getText();
    }

    public Element getParent() {
        return (Element) this.parent;
    }

    public Document getDocument() {
        if (this.parent != null)
            return ((Element) this.parent).getDocument();
        return null;
    }

    protected Text setParent(Element parent) {
        this.parent = parent;
        return this;
    }

    public Text detach() {
        if (this.parent != null)
            ((Element) this.parent).removeContent(this);
        this.parent = null;
        return this;
    }

    public String toString() {
        return (new StringBuffer(64)).append("[Text: ").append(getText()).append("]").toString();
    }

    public final int hashCode() {
        return super.hashCode();
    }

    public Object clone() {
        Text text = null;
        try {
            text = (Text) super.clone();
        } catch (CloneNotSupportedException cloneNotSupportedException) {
        }
        text.parent = null;
        text.value = this.value;
        return text;
    }

    public final boolean equals(Object ob) {
        return !(this != ob);
    }
}
