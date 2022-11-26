package org.jdom;

import java.io.Serializable;
import org.jdom.output.XMLOutputter;

public class DocType implements Serializable, Cloneable {
    private static final String CVS_ID = "@(#) $RCSfile: DocType.java,v $ $Revision: 1.18 $ $Date: 2002/02/05 08:03:18 $ $Name: jdom_1_0_b8 $";
    protected String elementName;
    protected String publicID;
    protected String systemID;
    protected Document document;
    protected String internalSubset;

    protected DocType() {
    }

    public DocType(String elementName, String publicID, String systemID) {
        setElementName(elementName);
        setPublicID(publicID);
        setSystemID(systemID);
    }

    public DocType(String elementName, String systemID) {
        this(elementName, null, systemID);
    }

    public DocType(String elementName) {
        this(elementName, null, null);
    }

    public String getElementName() {
        return this.elementName;
    }

    public DocType setElementName(String elementName) {
        String reason = Verifier.checkXMLName(elementName);
        if (reason != null)
            throw new IllegalNameException(elementName, "DocType", reason);
        this.elementName = elementName;
        return this;
    }

    public String getPublicID() {
        return this.publicID;
    }

    public DocType setPublicID(String publicID) {
        String reason = Verifier.checkPublicID(publicID);
        if (reason != null)
            throw new IllegalDataException(publicID, "DocType", reason);
        this.publicID = publicID;
        return this;
    }

    public String getSystemID() {
        return this.systemID;
    }

    public DocType setSystemID(String systemID) {
        String reason = Verifier.checkSystemLiteral(systemID);
        if (reason != null)
            throw new IllegalDataException(systemID, "DocType", reason);
        this.systemID = systemID;
        return this;
    }

    public Document getDocument() {
        return this.document;
    }

    protected DocType setDocument(Document document) {
        this.document = document;
        return this;
    }

    public void setInternalSubset(String newData) {
        this.internalSubset = newData;
    }

    public String getInternalSubset() {
        return this.internalSubset;
    }

    public String toString() {
        return "[DocType: " + (new XMLOutputter()).outputString(this) + "]";
    }

    public final boolean equals(Object ob) {
        if (ob instanceof DocType) {
            DocType dt = (DocType) ob;
            return !(!stringEquals(dt.elementName, this.elementName) || !stringEquals(dt.publicID, this.publicID) || !stringEquals(dt.systemID, this.systemID));
        }
        return false;
    }

    private boolean stringEquals(String s1, String s2) {
        if (s1 == null && s2 == null)
            return true;
        if (s1 == null && s2 != null)
            return false;
        return s1.equals(s2);
    }

    public final int hashCode() {
        return super.hashCode();
    }

    public Object clone() {
        DocType docType = null;
        try {
            docType = (DocType) super.clone();
        } catch (CloneNotSupportedException cloneNotSupportedException) {
        }
        docType.document = null;
        return docType;
    }
}
