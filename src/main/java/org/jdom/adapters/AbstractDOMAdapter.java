package org.jdom.adapters;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import org.jdom.DocType;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;

public abstract class AbstractDOMAdapter implements DOMAdapter {
    private static final String CVS_ID = "@(#) $RCSfile: AbstractDOMAdapter.java,v $ $Revision: 1.13 $ $Date: 2002/02/14 09:16:38 $ $Name: jdom_1_0_b8 $";

    public Document getDocument(File filename, boolean validate) throws Exception {
        return getDocument(new FileInputStream(filename), validate);
    }

    public Document createDocument(DocType doctype) throws Exception {
        if (doctype == null)
            return createDocument();
        DOMImplementation domImpl = createDocument().getImplementation();
        DocumentType domDocType = domImpl.createDocumentType(doctype.getElementName(), doctype.getPublicID(), doctype.getSystemID());
        setInternalSubset(domDocType, doctype.getInternalSubset());
        return domImpl.createDocument("http://temporary", doctype.getElementName(), domDocType);
    }

    protected void setInternalSubset(DocumentType dt, String s) {
        if (dt == null || s == null)
            return;
        try {
            Class dtclass = dt.getClass();
            Method setInternalSubset = dtclass.getMethod("setInternalSubset", new Class[]{String.class});
            setInternalSubset.invoke(dt, new Object[]{s});
        } catch (Exception exception) {
        }
    }

    public abstract Document createDocument() throws Exception;

    public abstract Document getDocument(InputStream paramInputStream, boolean paramBoolean) throws Exception;
}
