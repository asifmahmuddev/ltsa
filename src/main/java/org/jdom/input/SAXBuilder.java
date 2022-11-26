package org.jdom.input;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class SAXBuilder {
    private static final String CVS_ID = "@(#) $RCSfile: SAXBuilder.java,v $ $Revision: 1.64 $ $Date: 2002/02/26 04:10:33 $ $Name: jdom_1_0_b8 $";
    private static final String DEFAULT_SAX_DRIVER = "org.apache.xerces.parsers.SAXParser";
    private boolean validate;
    private boolean expand = true;
    private String saxDriverClass;
    private ErrorHandler saxErrorHandler = null;
    private EntityResolver saxEntityResolver = null;
    private DTDHandler saxDTDHandler = null;
    private XMLFilter saxXMLFilter = null;
    protected JDOMFactory factory = null;
    private boolean ignoringWhite = false;
    private HashMap features = new HashMap(5);
    private HashMap properties = new HashMap(5);

    public SAXBuilder() {
        this(false);
    }

    public SAXBuilder(boolean validate) {
        this.validate = validate;
    }

    public SAXBuilder(String saxDriverClass) {
        this(saxDriverClass, false);
    }

    public SAXBuilder(String saxDriverClass, boolean validate) {
        this.saxDriverClass = saxDriverClass;
        this.validate = validate;
    }

    public void setFactory(JDOMFactory factory) {
        this.factory = factory;
    }

    public void setValidation(boolean validate) {
        this.validate = validate;
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        this.saxErrorHandler = errorHandler;
    }

    public void setEntityResolver(EntityResolver entityResolver) {
        this.saxEntityResolver = entityResolver;
    }

    public void setDTDHandler(DTDHandler dtdHandler) {
        this.saxDTDHandler = dtdHandler;
    }

    public void setXMLFilter(XMLFilter xmlFilter) {
        this.saxXMLFilter = xmlFilter;
    }

    public void setIgnoringElementContentWhitespace(boolean ignoringWhite) {
        this.ignoringWhite = ignoringWhite;
    }

    public void setFeature(String name, boolean value) {
        this.features.put(name, new Boolean(value));
    }

    public void setProperty(String name, Object value) {
        this.properties.put(name, value);
    }

    public Document build(InputSource in) throws JDOMException {
        SAXHandler contentHandler = null;
        try {
            contentHandler = createContentHandler();
            configureContentHandler(contentHandler);
            XMLReader parser = createParser();
            if (this.saxXMLFilter != null) {
                XMLFilter root = this.saxXMLFilter;
                while (root.getParent() instanceof XMLFilter)
                    root = (XMLFilter) root.getParent();
                root.setParent(parser);
                parser = this.saxXMLFilter;
            }
            configureParser(parser, contentHandler);
            parser.parse(in);
            return contentHandler.getDocument();
        } catch (Exception e) {
            if (e instanceof SAXParseException) {
                SAXParseException p = (SAXParseException) e;
                String systemId = p.getSystemId();
                if (systemId != null)
                    throw new JDOMException("Error on line " + p.getLineNumber() + " of document " + systemId, e);
                throw new JDOMException("Error on line " + p.getLineNumber(), e);
            }
            if (e instanceof JDOMException)
                throw (JDOMException) e;
            throw new JDOMException("Error in building", e);
        } finally {
            contentHandler = null;
        }
    }

    protected SAXHandler createContentHandler() throws Exception {
        SAXHandler contentHandler = new SAXHandler(this.factory);
        return contentHandler;
    }

    protected void configureContentHandler(SAXHandler contentHandler) throws Exception {
        contentHandler.setExpandEntities(this.expand);
        contentHandler.setIgnoringElementContentWhitespace(this.ignoringWhite);
    }

    protected XMLReader createParser() throws Exception {
        XMLReader parser = null;
        if (this.saxDriverClass != null) {
            parser = XMLReaderFactory.createXMLReader(this.saxDriverClass);
        } else {
            try {
                Class factoryClass = Class.forName("javax.xml.parsers.SAXParserFactory");
                Method newParserInstance = factoryClass.getMethod("newInstance", null);
                Object factory = newParserInstance.invoke(null, null);
                Method setValidating = factoryClass.getMethod("setValidating", new Class[]{boolean.class});
                setValidating.invoke(factory, new Object[]{new Boolean(this.validate)});
                Method newSAXParser = factoryClass.getMethod("newSAXParser", null);
                Object jaxpParser = newSAXParser.invoke(factory, null);
                Class parserClass = jaxpParser.getClass();
                Method getXMLReader = parserClass.getMethod("getXMLReader", null);
                parser = (XMLReader) getXMLReader.invoke(jaxpParser, null);
            } catch (ClassNotFoundException classNotFoundException) {
            } catch (InvocationTargetException invocationTargetException) {
            } catch (NoSuchMethodException noSuchMethodException) {
            } catch (IllegalAccessException illegalAccessException) {
            }
        }
        if (parser == null) {
            parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
            this.saxDriverClass = parser.getClass().getName();
        }
        return parser;
    }

    protected void configureParser(XMLReader parser, SAXHandler contentHandler) throws Exception {
        parser.setContentHandler(contentHandler);
        if (this.saxEntityResolver != null)
            parser.setEntityResolver(this.saxEntityResolver);
        if (this.saxDTDHandler != null) {
            parser.setDTDHandler(this.saxDTDHandler);
        } else {
            parser.setDTDHandler(contentHandler);
        }
        if (this.saxErrorHandler != null) {
            parser.setErrorHandler(this.saxErrorHandler);
        } else {
            parser.setErrorHandler(new BuilderErrorHandler());
        }
        Iterator iter = this.features.keySet().iterator();
        while (iter.hasNext()) {
            String name = iter.next();
            Boolean value = (Boolean) this.features.get(name);
            internalSetFeature(parser, name, value.booleanValue(), name);
        }
        Iterator iter2 = this.properties.keySet().iterator();
        while (iter2.hasNext()) {
            String name = iter2.next();
            Object value = this.properties.get(name);
            internalSetProperty(parser, name, value, name);
        }
        boolean lexicalReporting = false;
        try {
            parser.setProperty("http://xml.org/sax/handlers/LexicalHandler", contentHandler);
            lexicalReporting = true;
        } catch (SAXNotSupportedException sAXNotSupportedException) {
        } catch (SAXNotRecognizedException sAXNotRecognizedException) {
        }
        if (!lexicalReporting)
            try {
                parser.setProperty("http://xml.org/sax/properties/lexical-handler", contentHandler);
                lexicalReporting = true;
            } catch (SAXNotSupportedException sAXNotSupportedException) {
            } catch (SAXNotRecognizedException sAXNotRecognizedException) {
            }
        if (!this.expand)
            try {
                parser.setProperty("http://xml.org/sax/properties/declaration-handler", contentHandler);
            } catch (SAXNotSupportedException sAXNotSupportedException) {
            } catch (SAXNotRecognizedException sAXNotRecognizedException) {
            }
        try {
            internalSetFeature(parser, "http://xml.org/sax/features/validation", this.validate, "Validation");
        } catch (JDOMException e) {
            if (this.validate)
                throw e;
        }
        internalSetFeature(parser, "http://xml.org/sax/features/namespaces", true, "Namespaces");
        internalSetFeature(parser, "http://xml.org/sax/features/namespace-prefixes", false, "Namespace prefixes");
        try {
            if (parser.getFeature("http://xml.org/sax/features/external-general-entities") != this.expand)
                parser.setFeature("http://xml.org/sax/features/external-general-entities", this.expand);
        } catch (SAXNotRecognizedException sAXNotRecognizedException) {
        } catch (SAXNotSupportedException sAXNotSupportedException) {
        }
    }

    private void internalSetFeature(XMLReader parser, String feature, boolean value, String displayName) throws JDOMException {
        try {
            parser.setFeature(feature, value);
        } catch (SAXNotSupportedException sAXNotSupportedException) {
            throw new JDOMException(String.valueOf(displayName) + " feature not supported for SAX driver " + parser.getClass().getName());
        } catch (SAXNotRecognizedException sAXNotRecognizedException) {
            throw new JDOMException(String.valueOf(displayName) + " feature not recognized for SAX driver " + parser.getClass().getName());
        }
    }

    private void internalSetProperty(XMLReader parser, String property, Object value, String displayName) throws JDOMException {
        try {
            parser.setProperty(property, value);
        } catch (SAXNotSupportedException sAXNotSupportedException) {
            throw new JDOMException(String.valueOf(displayName) + " property not supported for SAX driver " + parser.getClass().getName());
        } catch (SAXNotRecognizedException sAXNotRecognizedException) {
            throw new JDOMException(String.valueOf(displayName) + " property not recognized for SAX driver " + parser.getClass().getName());
        }
    }

    public Document build(InputStream in) throws JDOMException {
        return build(new InputSource(in));
    }

    public Document build(File file) throws JDOMException {
        try {
            URL url = fileToURL(file);
            return build(url);
        } catch (MalformedURLException e) {
            throw new JDOMException("Error in building", e);
        }
    }

    public Document build(URL url) throws JDOMException {
        String systemID = url.toExternalForm();
        return build(new InputSource(systemID));
    }

    public Document build(InputStream in, String systemId) throws JDOMException {
        InputSource src = new InputSource(in);
        src.setSystemId(systemId);
        return build(src);
    }

    public Document build(Reader characterStream) throws JDOMException {
        return build(new InputSource(characterStream));
    }

    public Document build(Reader characterStream, String SystemId) throws JDOMException {
        InputSource src = new InputSource(characterStream);
        src.setSystemId(SystemId);
        return build(src);
    }

    public Document build(String systemId) throws JDOMException {
        return build(new InputSource(systemId));
    }

    protected URL fileToURL(File f) throws MalformedURLException {
        String path = f.getAbsolutePath();
        if (File.separatorChar != '/')
            path = path.replace(File.separatorChar, '/');
        if (!path.startsWith("/"))
            path = "/" + path;
        if (!path.endsWith("/") && f.isDirectory())
            path = String.valueOf(path) + "/";
        return new URL("file", "", path);
    }

    public void setExpandEntities(boolean expand) {
        this.expand = expand;
    }
}
