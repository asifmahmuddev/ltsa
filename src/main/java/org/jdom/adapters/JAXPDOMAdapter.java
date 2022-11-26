package org.jdom.adapters;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.jdom.input.BuilderErrorHandler;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;

public class JAXPDOMAdapter extends AbstractDOMAdapter {
    private static final String CVS_ID = "@(#) $RCSfile: JAXPDOMAdapter.java,v $ $Revision: 1.5 $ $Date: 2002/01/08 09:17:10 $ $Name: jdom_1_0_b8 $";

    public Document getDocument(InputStream in, boolean validate) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Class.forName("javax.xml.transform.Transformer");
        Class factoryClass = Class.forName("javax.xml.parsers.DocumentBuilderFactory");
        Method newParserInstance = factoryClass.getMethod("newInstance", null);
        Object factory = newParserInstance.invoke(null, null);
        Method setValidating = factoryClass.getMethod("setValidating", new Class[]{boolean.class});
        setValidating.invoke(factory, new Object[]{new Boolean(validate)});
        Method setNamespaceAware = factoryClass.getMethod("setNamespaceAware", new Class[]{boolean.class});
        setNamespaceAware.invoke(factory, new Object[]{Boolean.TRUE});
        Method newDocBuilder = factoryClass.getMethod("newDocumentBuilder", null);
        Object jaxpParser = newDocBuilder.invoke(factory, null);
        Class parserClass = jaxpParser.getClass();
        Method setErrorHandler = parserClass.getMethod("setErrorHandler", new Class[]{ErrorHandler.class});
        setErrorHandler.invoke(jaxpParser, new Object[]{new BuilderErrorHandler()});
        Method parse = parserClass.getMethod("parse", new Class[]{InputStream.class});
        Document domDoc = (Document) parse.invoke(jaxpParser, new Object[]{in});
        return domDoc;
    }

    public Document createDocument() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Class.forName("javax.xml.transform.Transformer");
        Class factoryClass = Class.forName("javax.xml.parsers.DocumentBuilderFactory");
        Method newParserInstance = factoryClass.getMethod("newInstance", null);
        Object factory = newParserInstance.invoke(null, null);
        Method newDocBuilder = factoryClass.getMethod("newDocumentBuilder", null);
        Object jaxpParser = newDocBuilder.invoke(factory, null);
        Class parserClass = jaxpParser.getClass();
        Method newDoc = parserClass.getMethod("newDocument", null);
        Document domDoc = (Document) newDoc.invoke(jaxpParser, null);
        return domDoc;
    }
}
