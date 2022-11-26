package org.jdom.output;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import org.jdom.Attribute;
import org.jdom.CDATA;
import org.jdom.Comment;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.EntityRef;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.ProcessingInstruction;
import org.jdom.Text;
import org.jdom.adapters.DOMAdapter;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

public class DOMOutputter {
    private static final String CVS_ID = "@(#) $RCSfile: DOMOutputter.java,v $ $Revision: 1.28 $ $Date: 2002/02/12 06:15:21 $ $Name: jdom_1_0_b8 $";
    private static final String DEFAULT_ADAPTER_CLASS = "org.jdom.adapters.XercesDOMAdapter";
    private String adapterClass;

    public DOMOutputter() {
    }

    public DOMOutputter(String adapterClass) {
        this.adapterClass = adapterClass;
    }

    public Document output(Document document) throws JDOMException {
        NamespaceStack namespaces = new NamespaceStack();
        Document domDoc = null;
        try {
            DocType dt = document.getDocType();
            domDoc = createDOMDocument(dt);
            Iterator itr = document.getContent().iterator();
            while (itr.hasNext()) {
                Object node = itr.next();
                if (node instanceof Element) {
                    Element element = (Element) node;
                    Element domElement = output(element, domDoc, namespaces);
                    Element root = domDoc.getDocumentElement();
                    if (root == null) {
                        domDoc.appendChild(domElement);
                        continue;
                    }
                    domDoc.replaceChild(domElement, root);
                    continue;
                }
                if (node instanceof Comment) {
                    Comment comment = (Comment) node;
                    Comment domComment = domDoc.createComment(comment.getText());
                    domDoc.appendChild(domComment);
                    continue;
                }
                if (node instanceof ProcessingInstruction) {
                    ProcessingInstruction pi = (ProcessingInstruction) node;
                    ProcessingInstruction domPI = domDoc.createProcessingInstruction(pi.getTarget(), pi.getData());
                    domDoc.appendChild(domPI);
                    continue;
                }
                throw new JDOMException("Document contained top-level content with type:" + node.getClass().getName());
            }
        } catch (Throwable e) {
            throw new JDOMException("Exception outputting Document", e);
        }
        return domDoc;
    }

    public Element output(Element element) throws JDOMException {
        try {
            Document domDoc = createDOMDocument();
            return output(element, domDoc, new NamespaceStack());
        } catch (Throwable e) {
            throw new JDOMException("Exception outputting Element " + element.getQualifiedName(), e);
        }
    }

    private Document createDOMDocument() throws Throwable {
        return createDOMDocument(null);
    }

    private Document createDOMDocument(DocType dt) throws Throwable {
        if (this.adapterClass != null) {
            try {
                DOMAdapter adapter = (DOMAdapter) Class.forName(this.adapterClass).newInstance();
                return adapter.createDocument(dt);
            } catch (ClassNotFoundException classNotFoundException) {
            }
        } else {
            try {
                DOMAdapter adapter = (DOMAdapter) Class.forName("org.jdom.adapters.JAXPDOMAdapter").newInstance();
                return adapter.createDocument(dt);
            } catch (ClassNotFoundException classNotFoundException) {
            } catch (NoSuchMethodException noSuchMethodException) {
            } catch (IllegalAccessException illegalAccessException) {
            } catch (InvocationTargetException ite) {
                throw ite.getTargetException();
            }
        }
        try {
            DOMAdapter adapter = (DOMAdapter) Class.forName("org.jdom.adapters.XercesDOMAdapter").newInstance();
            return adapter.createDocument(dt);
        } catch (ClassNotFoundException classNotFoundException) {
            throw new Exception("No JAXP or default parser available");
        }
    }

    protected Element output(Element element, Document domDoc, NamespaceStack namespaces) throws JDOMException {
        try {
            int previouslyDeclaredNamespaces = namespaces.size();
            Element domElement = null;
            if (element.getNamespace() == Namespace.NO_NAMESPACE) {
                domElement = domDoc.createElement(element.getQualifiedName());
            } else {
                domElement = domDoc.createElementNS(element.getNamespaceURI(), element.getQualifiedName());
            }
            Namespace ns = element.getNamespace();
            if (ns != Namespace.XML_NAMESPACE && (ns != Namespace.NO_NAMESPACE || namespaces.getURI("") != null)) {
                String prefix = ns.getPrefix();
                String uri = namespaces.getURI(prefix);
                if (!ns.getURI().equals(uri)) {
                    namespaces.push(ns);
                    String attrName = getXmlnsTagFor(ns);
                    domElement.setAttribute(attrName, ns.getURI());
                }
            }
            Iterator itr = element.getAdditionalNamespaces().iterator();
            while (itr.hasNext()) {
                Namespace additional = itr.next();
                String prefix = additional.getPrefix();
                String uri = namespaces.getURI(prefix);
                if (!additional.getURI().equals(uri)) {
                    String attrName = getXmlnsTagFor(additional);
                    domElement.setAttribute(attrName, additional.getURI());
                    namespaces.push(additional);
                }
            }
            itr = element.getAttributes().iterator();
            while (itr.hasNext()) {
                Attribute attribute = (Attribute) itr.next();
                domElement.setAttributeNode(output(attribute, domDoc));
                Namespace ns1 = attribute.getNamespace();
                if (ns1 != Namespace.NO_NAMESPACE && ns1 != Namespace.XML_NAMESPACE) {
                    String prefix = ns1.getPrefix();
                    String uri = namespaces.getURI(prefix);
                    if (!ns.getURI().equals(uri)) {
                        String attrName = getXmlnsTagFor(ns1);
                        domElement.setAttribute(attrName, ns1.getURI());
                        namespaces.push(ns);
                    }
                }
                if (attribute.getNamespace() == Namespace.NO_NAMESPACE) {
                    domElement.setAttribute(attribute.getQualifiedName(), attribute.getValue());
                    continue;
                }
                domElement.setAttributeNS(attribute.getNamespaceURI(), attribute.getQualifiedName(), attribute.getValue());
            }
            itr = element.getContent().iterator();
            while (itr.hasNext()) {
                Object node = itr.next();
                if (node instanceof Element) {
                    Element e = (Element) node;
                    Element domElt = output(e, domDoc, namespaces);
                    domElement.appendChild(domElt);
                    continue;
                }
                if (node instanceof String) {
                    String str = (String) node;
                    Text domText = domDoc.createTextNode(str);
                    domElement.appendChild(domText);
                    continue;
                }
                if (node instanceof CDATA) {
                    CDATA cdata = (CDATA) node;
                    CDATASection domCdata = domDoc.createCDATASection(cdata.getText());
                    domElement.appendChild(domCdata);
                    continue;
                }
                if (node instanceof Text) {
                    Text text = (Text) node;
                    Text domText = domDoc.createTextNode(text.getText());
                    domElement.appendChild(domText);
                    continue;
                }
                if (node instanceof Comment) {
                    Comment comment = (Comment) node;
                    Comment domComment = domDoc.createComment(comment.getText());
                    domElement.appendChild(domComment);
                    continue;
                }
                if (node instanceof ProcessingInstruction) {
                    ProcessingInstruction pi = (ProcessingInstruction) node;
                    ProcessingInstruction domPI = domDoc.createProcessingInstruction(pi.getTarget(), pi.getData());
                    domElement.appendChild(domPI);
                    continue;
                }
                if (node instanceof EntityRef) {
                    EntityRef entity = (EntityRef) node;
                    EntityReference domEntity = domDoc.createEntityReference(entity.getName());
                    domElement.appendChild(domEntity);
                    continue;
                }
                throw new JDOMException("Element contained content with type:" + node.getClass().getName());
            }
            while (namespaces.size() > previouslyDeclaredNamespaces)
                namespaces.pop();
            return domElement;
        } catch (Exception e) {
            throw new JDOMException("Exception outputting Element " + element.getQualifiedName(), e);
        }
    }

    public Attr output(Attribute attribute) throws JDOMException {
        try {
            Document domDoc = createDOMDocument();
            return output(attribute, domDoc);
        } catch (Throwable e) {
            throw new JDOMException("Exception outputting Attribute " + attribute.getQualifiedName(), e);
        }
    }

    protected Attr output(Attribute attribute, Document domDoc) throws JDOMException {
        Attr domAttr = null;
        try {
            if (attribute.getNamespace() == Namespace.NO_NAMESPACE) {
                domAttr = domDoc.createAttribute(attribute.getQualifiedName());
            } else {
                domAttr = domDoc.createAttributeNS(attribute.getNamespaceURI(), attribute.getQualifiedName());
            }
            domAttr.setValue(attribute.getValue());
        } catch (Exception e) {
            throw new JDOMException("Exception outputting Attribute " + attribute.getQualifiedName(), e);
        }
        return domAttr;
    }

    private String getXmlnsTagFor(Namespace ns) {
        String attrName = "xmlns";
        if (!ns.getPrefix().equals("")) {
            attrName = String.valueOf(attrName) + ":";
            attrName = String.valueOf(attrName) + ns.getPrefix();
        }
        return attrName;
    }
}
