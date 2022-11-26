package org.jdom.input;

import java.util.Map;
import org.jdom.Attribute;
import org.jdom.CDATA;
import org.jdom.Comment;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.EntityRef;
import org.jdom.Namespace;
import org.jdom.ProcessingInstruction;
import org.jdom.Text;

public class DefaultJDOMFactory implements JDOMFactory {
    private static final String CVS_ID = "@(#) $RCSfile: DefaultJDOMFactory.java,v $ $Revision: 1.6 $ $Date: 2002/03/12 07:57:06 $ $Name: jdom_1_0_b8 $";

    public Attribute attribute(String name, String value, Namespace namespace) {
        return new Attribute(name, value, namespace);
    }

    public Attribute attribute(String name, String value, int type, Namespace namespace) {
        return new Attribute(name, value, type, namespace);
    }

    public Attribute attribute(String name, String value) {
        return new Attribute(name, value);
    }

    public Attribute attribute(String name, String value, int type) {
        return new Attribute(name, value, type);
    }

    public CDATA cdata(String text) {
        return new CDATA(text);
    }

    public Text text(String text) {
        return new Text(text);
    }

    public Comment comment(String text) {
        return new Comment(text);
    }

    public DocType docType(String elementName, String publicID, String systemID) {
        return new DocType(elementName, publicID, systemID);
    }

    public DocType docType(String elementName, String systemID) {
        return new DocType(elementName, systemID);
    }

    public DocType docType(String elementName) {
        return new DocType(elementName);
    }

    public Document document(Element rootElement, DocType docType) {
        return new Document(rootElement, docType);
    }

    public Document document(Element rootElement) {
        return new Document(rootElement);
    }

    public Element element(String name, Namespace namespace) {
        return new Element(name, namespace);
    }

    public Element element(String name) {
        return new Element(name);
    }

    public Element element(String name, String uri) {
        return new Element(name, uri);
    }

    public Element element(String name, String prefix, String uri) {
        return new Element(name, prefix, uri);
    }

    public ProcessingInstruction processingInstruction(String target, Map data) {
        return new ProcessingInstruction(target, data);
    }

    public ProcessingInstruction processingInstruction(String target, String data) {
        return new ProcessingInstruction(target, data);
    }

    public EntityRef entityRef(String name) {
        return new EntityRef(name);
    }

    public EntityRef entityRef(String name, String publicID, String systemID) {
        return new EntityRef(name, publicID, systemID);
    }
}
