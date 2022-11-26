package org.jdom.adapters;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

public class OracleV2DOMAdapter extends AbstractDOMAdapter {
    private static final String CVS_ID = "@(#) $RCSfile: OracleV2DOMAdapter.java,v $ $Revision: 1.9 $ $Date: 2002/01/08 09:17:10 $ $Name: jdom_1_0_b8 $";

    public Document getDocument(InputStream in, boolean validate) throws IOException {
        try {
            Class parserClass = Class.forName("oracle.xml.parser.v2.DOMParser");
            Object parser = parserClass.newInstance();
            Method parse = parserClass.getMethod("parse", new Class[]{InputSource.class});
            parse.invoke(parser, new Object[]{new InputSource(in)});
            Method getDocument = parserClass.getMethod("getDocument", null);
            Document doc = (Document) getDocument.invoke(parser, null);
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
            return (Document) Class.forName("oracle.xml.parser.v2.XMLDocument").newInstance();
        } catch (Exception e) {
            throw new IOException(String.valueOf(e.getClass().getName()) + ": " + e.getMessage());
        }
    }
}
