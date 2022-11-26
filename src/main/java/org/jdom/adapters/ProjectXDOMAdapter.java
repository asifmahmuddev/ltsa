package org.jdom.adapters;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.w3c.dom.Document;
import org.xml.sax.SAXParseException;

public class ProjectXDOMAdapter extends AbstractDOMAdapter {
    private static final String CVS_ID = "@(#) $RCSfile: ProjectXDOMAdapter.java,v $ $Revision: 1.11 $ $Date: 2002/01/08 09:17:10 $ $Name: jdom_1_0_b8 $";

    public Document getDocument(InputStream in, boolean validate) throws IOException {
        try {
            Class[] parameterTypes = new Class[2];
            parameterTypes[0] = Class.forName("java.io.InputStream");
            parameterTypes[1] = boolean.class;
            Object[] args = new Object[2];
            args[0] = in;
            args[1] = new Boolean(false);
            Class parserClass = Class.forName("com.sun.xml.tree.XmlDocument");
            Method createXmlDocument = parserClass.getMethod("createXmlDocument", parameterTypes);
            Document doc = (Document) createXmlDocument.invoke(null, args);
            return doc;
        } catch (InvocationTargetException e) {
            Throwable targetException = e.getTargetException();
            if (targetException instanceof SAXParseException) {
                SAXParseException parseException = (SAXParseException) targetException;
                throw new IOException("Error on line " + parseException.getLineNumber() + " of XML document: " + parseException.getMessage());
            }
            throw new IOException(targetException.getMessage());
        } catch (Exception e) {
            throw new IOException(String.valueOf(e.getClass().getName()) + ": " + e.getMessage());
        }
    }

    public Document createDocument() throws IOException {
        try {
            return (Document) Class.forName("com.sun.xml.tree.XmlDocument").newInstance();
        } catch (Exception e) {
            throw new IOException(String.valueOf(e.getClass().getName()) + ": " + e.getMessage());
        }
    }
}
