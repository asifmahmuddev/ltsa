package org.jdom.input;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import org.jdom.Attribute;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.EntityRef;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.adapters.DOMAdapter;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXParseException;

public class DOMBuilder {
    private static final String CVS_ID = "@(#) $RCSfile: DOMBuilder.java,v $ $Revision: 1.42 $ $Date: 2002/02/12 06:15:20 $ $Name: jdom_1_0_b8 $";
    private static final String DEFAULT_ADAPTER_CLASS = "org.jdom.adapters.XercesDOMAdapter";
    private boolean validate;
    private String adapterClass;
    private JDOMFactory factory = new DefaultJDOMFactory();

    public DOMBuilder() {
        this(false);
    }

    public DOMBuilder(boolean validate) {
        setValidation(validate);
    }

    public DOMBuilder(String adapterClass) {
        this(adapterClass, false);
    }

    public DOMBuilder(String adapterClass, boolean validate) {
        this.adapterClass = adapterClass;
        setValidation(validate);
    }

    public void setFactory(JDOMFactory factory) {
        this.factory = factory;
    }

    public void setValidation(boolean validate) {
        this.validate = validate;
    }

    public Document build(InputStream in) throws JDOMException {
        Document doc = this.factory.document(null);
        Document domDoc = null;
        try {
            if (this.adapterClass != null) {
                try {
                    DOMAdapter adapter = (DOMAdapter) Class.forName(this.adapterClass).newInstance();
                    domDoc = adapter.getDocument(in, this.validate);
                } catch (ClassNotFoundException classNotFoundException) {
                }
            } else {
                try {
                    DOMAdapter adapter = (DOMAdapter) Class.forName("org.jdom.adapters.JAXPDOMAdapter").newInstance();
                    domDoc = adapter.getDocument(in, this.validate);
                } catch (ClassNotFoundException classNotFoundException) {
                } catch (NoSuchMethodException noSuchMethodException) {
                } catch (IllegalAccessException illegalAccessException) {
                } catch (InvocationTargetException ite) {
                    throw ite.getTargetException();
                }
            }
            if (domDoc == null && this.adapterClass == null)
                try {
                    DOMAdapter adapter = (DOMAdapter) Class.forName("org.jdom.adapters.XercesDOMAdapter").newInstance();
                    domDoc = adapter.getDocument(in, this.validate);
                } catch (ClassNotFoundException classNotFoundException) {
                }
            buildTree(domDoc, doc, null, true);
        } catch (Throwable e) {
            if (e instanceof SAXParseException) {
                SAXParseException p = (SAXParseException) e;
                String systemId = p.getSystemId();
                if (systemId != null)
                    throw new JDOMException("Error on line " + p.getLineNumber() + " of document " + systemId, e);
                throw new JDOMException("Error on line " + p.getLineNumber(), e);
            }
            throw new JDOMException("Error in building from stream", e);
        }
        return doc;
    }

    public Document build(File file) throws JDOMException {
        try {
            FileInputStream in = new FileInputStream(file);
            return build(in);
        } catch (FileNotFoundException e) {
            throw new JDOMException("Error in building from " + file, e);
        }
    }

    public Document build(URL url) throws JDOMException {
        try {
            return build(url.openStream());
        } catch (IOException e) {
            throw new JDOMException("Error in building from " + url, e);
        }
    }

    public Document build(Document domDocument) {
        Document doc = this.factory.document(null);
        buildTree(domDocument, doc, null, true);
        return doc;
    }

    public Element build(Element domElement) {
        Document doc = this.factory.document(null);
        buildTree(domElement, doc, null, true);
        return doc.getRootElement();
    }

    private void buildTree(Node node, Document doc, Element current, boolean atRoot) {
        NodeList nodes;
        int i;
        String nodeName;
        int size;
        String prefix;
        String localName;
        int colon;
        Namespace ns;
        String uri;
        Element element;
        NamedNodeMap attributeList;
        int attsize;
        int j;
        NodeList children;
        String data;
        String cdata;
        EntityRef entity;
        DocumentType domDocType;
        String publicID;
        String systemID;
        String internalDTD;
        DocType docType;
        switch (node.getNodeType()) {
            case 9 :
                nodes = node.getChildNodes();
                for (i = 0, size = nodes.getLength(); i < size; i++)
                    buildTree(nodes.item(i), doc, current, true);
                break;
            case 1 :
                nodeName = node.getNodeName();
                prefix = "";
                localName = nodeName;
                colon = nodeName.indexOf(':');
                if (colon >= 0) {
                    prefix = nodeName.substring(0, colon);
                    localName = nodeName.substring(colon + 1);
                }
                ns = null;
                uri = node.getNamespaceURI();
                if (uri == null) {
                    ns = (current == null) ? Namespace.NO_NAMESPACE : current.getNamespace(prefix);
                } else {
                    ns = Namespace.getNamespace(prefix, uri);
                }
                element = this.factory.element(localName, ns);
                attributeList = node.getAttributes();
                attsize = attributeList.getLength();
                for (j = 0; j < attsize; j++) {
                    Attr att = (Attr) attributeList.item(j);
                    String attname = att.getName();
                    if (attname.startsWith("xmlns")) {
                        String attPrefix = "";
                        colon = attname.indexOf(':');
                        if (colon >= 0)
                            attPrefix = attname.substring(colon + 1);
                        String attvalue = att.getValue();
                        Namespace declaredNS = Namespace.getNamespace(attPrefix, attvalue);
                        if (prefix.equals(attPrefix)) {
                            element.setNamespace(declaredNS);
                        } else {
                            element.addNamespaceDeclaration(declaredNS);
                        }
                    }
                }
                for (j = 0; j < attsize; j++) {
                    Attr att = (Attr) attributeList.item(j);
                    String attname = att.getName();
                    if (!attname.startsWith("xmlns")) {
                        String attPrefix = "";
                        String attLocalName = attname;
                        colon = attname.indexOf(':');
                        if (colon >= 0) {
                            attPrefix = attname.substring(0, colon);
                            attLocalName = attname.substring(colon + 1);
                        }
                        String attvalue = att.getValue();
                        Namespace attns = null;
                        if ("".equals(attPrefix)) {
                            attns = Namespace.NO_NAMESPACE;
                        } else {
                            attns = element.getNamespace(attPrefix);
                        }
                        Attribute attribute = this.factory.attribute(attLocalName, attvalue, attns);
                        element.setAttribute(attribute);
                    }
                }
                if (atRoot) {
                    doc.setRootElement(element);
                } else {
                    current.addContent(element);
                }
                children = node.getChildNodes();
                if (children != null) {
                    int k = children.getLength();
                    for (int m = 0; m < k; m++) {
                        Node item = children.item(m);
                        if (item != null)
                            buildTree(item, doc, element, false);
                    }
                }
                break;
            case 3 :
                data = node.getNodeValue();
                current.addContent(this.factory.text(data));
                break;
            case 4 :
                cdata = node.getNodeValue();
                current.addContent(this.factory.cdata(cdata));
                break;
            case 7 :
                if (atRoot) {
                    doc.addContent(this.factory.processingInstruction(node.getNodeName(), node.getNodeValue()));
                    break;
                }
                current.addContent(this.factory.processingInstruction(node.getNodeName(), node.getNodeValue()));
                break;
            case 8 :
                if (atRoot) {
                    doc.addContent(this.factory.comment(node.getNodeValue()));
                    break;
                }
                current.addContent(this.factory.comment(node.getNodeValue()));
                break;
            case 5 :
                entity = this.factory.entityRef(node.getNodeName());
                current.addContent(entity);
                break;
            case 10 :
                domDocType = (DocumentType) node;
                publicID = domDocType.getPublicId();
                systemID = domDocType.getSystemId();
                internalDTD = domDocType.getInternalSubset();
                docType = this.factory.docType(domDocType.getName());
                docType.setPublicID(publicID);
                docType.setSystemID(systemID);
                docType.setInternalSubset(internalDTD);
                doc.setDocType(docType);
                break;
        }
    }
}
