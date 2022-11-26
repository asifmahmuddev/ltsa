package org.jdom;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import org.jdom.filter.ElementFilter;
import org.jdom.filter.Filter;

public class Element implements Serializable, Cloneable {
    private static final String CVS_ID = "@(#) $RCSfile: Element.java,v $ $Revision: 1.115 $ $Date: 2002/03/28 11:08:12 $ $Name: jdom_1_0_b8 $";
    private static final int INITIAL_ARRAY_SIZE = 5;
    protected String name;
    protected transient Namespace namespace;
    protected transient List additionalNamespaces;
    protected Object parent;
    protected AttributeList attributes = new AttributeList(this);
    protected ContentList content = new ContentList(this);

    public Element(String name, Namespace namespace) {
        setName(name);
        setNamespace(namespace);
    }

    public Element(String name) {
        this(name, (Namespace) null);
    }

    public Element(String name, String uri) {
        this(name, Namespace.getNamespace("", uri));
    }

    public Element(String name, String prefix, String uri) {
        this(name, Namespace.getNamespace(prefix, uri));
    }

    public String getName() {
        return this.name;
    }

    public Element setName(String name) {
        String reason = Verifier.checkElementName(name);
        if (reason != null)
            throw new IllegalNameException(name, "element", reason);
        this.name = name;
        return this;
    }

    public Namespace getNamespace() {
        return this.namespace;
    }

    public Element setNamespace(Namespace namespace) {
        if (namespace == null)
            namespace = Namespace.NO_NAMESPACE;
        this.namespace = namespace;
        return this;
    }

    public String getNamespacePrefix() {
        return this.namespace.getPrefix();
    }

    public String getNamespaceURI() {
        return this.namespace.getURI();
    }

    public Namespace getNamespace(String prefix) {
        if (prefix == null)
            return null;
        if (prefix.equals(getNamespacePrefix()))
            return getNamespace();
        if (this.additionalNamespaces != null)
            for (int i = 0; i < this.additionalNamespaces.size(); i++) {
                Namespace ns = this.additionalNamespaces.get(i);
                if (prefix.equals(ns.getPrefix()))
                    return ns;
            }
        if (this.parent instanceof Element)
            return ((Element) this.parent).getNamespace(prefix);
        return null;
    }

    public String getQualifiedName() {
        if (this.namespace.getPrefix().equals(""))
            return getName();
        return this.namespace.getPrefix() + ":" + this.name;
    }

    public void addNamespaceDeclaration(Namespace additional) {
        String reason = Verifier.checkNamespaceCollision(additional, this);
        if (reason != null)
            throw new IllegalAddException(this, additional, reason);
        if (this.additionalNamespaces == null)
            this.additionalNamespaces = new ArrayList(5);
        this.additionalNamespaces.add(additional);
    }

    public void removeNamespaceDeclaration(Namespace additionalNamespace) {
        if (this.additionalNamespaces == null)
            return;
        this.additionalNamespaces.remove(additionalNamespace);
    }

    public List getAdditionalNamespaces() {
        if (this.additionalNamespaces == null)
            return Collections.EMPTY_LIST;
        return Collections.unmodifiableList(this.additionalNamespaces);
    }

    public Element getParent() {
        if (this.parent instanceof Element)
            return (Element) this.parent;
        return null;
    }

    protected Element setParent(Element parent) {
        this.parent = parent;
        return this;
    }

    public Element detach() {
        if (this.parent instanceof Element) {
            ((Element) this.parent).removeContent(this);
        } else if (this.parent instanceof Document) {
            ((Document) this.parent).detachRootElement();
        }
        return this;
    }

    public boolean isRootElement() {
        return this.parent instanceof Document;
    }

    protected Element setDocument(Document document) {
        this.parent = document;
        return this;
    }

    public Document getDocument() {
        if (this.parent instanceof Document)
            return (Document) this.parent;
        if (this.parent instanceof Element)
            return ((Element) this.parent).getDocument();
        return null;
    }

    public String getText() {
        if (this.content.size() == 0)
            return "";
        if (this.content.size() == 1) {
            Object obj = this.content.get(0);
            if (obj instanceof Text)
                return ((Text) obj).getText();
            if (obj instanceof CDATA)
                return ((CDATA) obj).getText();
            return "";
        }
        StringBuffer textContent = new StringBuffer();
        boolean hasText = false;
        for (int i = 0; i < this.content.size(); i++) {
            Object obj = this.content.get(i);
            if (obj instanceof Text) {
                textContent.append(((Text) obj).getText());
                hasText = true;
            } else if (obj instanceof CDATA) {
                textContent.append(((CDATA) obj).getText());
                hasText = true;
            }
        }
        if (!hasText)
            return "";
        return textContent.toString();
    }

    public String getTextTrim() {
        return getText().trim();
    }

    public String getTextNormalize() {
        return Text.normalizeString(getText());
    }

    public String getChildText(String name) {
        Element child = getChild(name);
        if (child == null)
            return null;
        return child.getText();
    }

    public String getChildTextTrim(String name) {
        Element child = getChild(name);
        if (child == null)
            return null;
        return child.getTextTrim();
    }

    public String getChildTextNormalize(String name) {
        Element child = getChild(name);
        if (child == null)
            return null;
        return child.getTextNormalize();
    }

    public String getChildText(String name, Namespace ns) {
        Element child = getChild(name, ns);
        if (child == null)
            return null;
        return child.getText();
    }

    public String getChildTextTrim(String name, Namespace ns) {
        Element child = getChild(name, ns);
        if (child == null)
            return null;
        return child.getTextTrim();
    }

    public String getChildTextNormalize(String name, Namespace ns) {
        Element child = getChild(name, ns);
        if (child == null)
            return null;
        return child.getTextNormalize();
    }

    public Element setText(String text) {
        this.content.clear();
        if (text != null)
            addContent(new Text(text));
        return this;
    }

    public List getContent() {
        return this.content;
    }

    public List getContent(Filter filter) {
        return this.content.getView(filter);
    }

    public Element setContent(List newContent) {
        this.content.clearAndSet(newContent);
        return this;
    }

    public boolean hasChildren() {
        for (int i = 0; i < this.content.size(); i++) {
            if (this.content.get(i) instanceof Element)
                return true;
        }
        return false;
    }

    public List getChildren() {
        return this.content.getView((Filter) new ElementFilter());
    }

    public Element setChildren(List children) {
        List list = this.content.getView((Filter) new ElementFilter());
        int size = list.size();
        try {
            list.addAll(children);
        } catch (RuntimeException exception) {
            removeRange(list, size, list.size());
            throw exception;
        }
        removeRange(list, 0, size);
        return this;
    }

    private void removeRange(List list, int start, int end) {
        ListIterator i = list.listIterator(start);
        for (int j = 0; j < end - start; j++) {
            i.next();
            i.remove();
        }
    }

    public List getChildren(String name) {
        return getChildren(name, Namespace.NO_NAMESPACE);
    }

    public List getChildren(String name, Namespace ns) {
        return this.content.getView((Filter) new ElementFilter(name, ns));
    }

    public Element getChild(String name, Namespace ns) {
        List elements = this.content.getView((Filter) new ElementFilter(name, ns));
        Iterator i = elements.iterator();
        if (i.hasNext())
            return i.next();
        return null;
    }

    public Element getChild(String name) {
        return getChild(name, Namespace.NO_NAMESPACE);
    }

    public Element addContent(String str) {
        return addContent(new Text(str));
    }

    public Element addContent(Text text) {
        this.content.add(text);
        return this;
    }

    public Element addContent(CDATA cdata) {
        this.content.add(cdata);
        return this;
    }

    public Element addContent(Element element) {
        this.content.add(element);
        return this;
    }

    public Element addContent(ProcessingInstruction pi) {
        this.content.add(pi);
        return this;
    }

    public Element addContent(EntityRef entity) {
        this.content.add(entity);
        return this;
    }

    public Element addContent(Comment comment) {
        this.content.add(comment);
        return this;
    }

    public boolean isAncestor(Element element) {
        Object p = this.parent;
        while (p instanceof Element) {
            if (p == element)
                return true;
            p = ((Element) p).getParent();
        }
        return false;
    }

    public boolean removeChild(String name) {
        return removeChild(name, Namespace.NO_NAMESPACE);
    }

    public boolean removeChild(String name, Namespace ns) {
        List old = this.content.getView((Filter) new ElementFilter(name, ns));
        Iterator i = old.iterator();
        if (i.hasNext()) {
            i.next();
            i.remove();
            return true;
        }
        return false;
    }

    public boolean removeChildren(String name) {
        return removeChildren(name, Namespace.NO_NAMESPACE);
    }

    public boolean removeChildren(String name, Namespace ns) {
        boolean deletedSome = false;
        List old = this.content.getView((Filter) new ElementFilter(name, ns));
        Iterator i = old.iterator();
        while (i.hasNext()) {
            i.next();
            i.remove();
            deletedSome = true;
        }
        return deletedSome;
    }

    public boolean removeChildren() {
        boolean deletedSome = false;
        List old = this.content.getView((Filter) new ElementFilter());
        Iterator i = old.iterator();
        while (i.hasNext()) {
            i.next();
            i.remove();
            deletedSome = true;
        }
        return deletedSome;
    }

    public List getAttributes() {
        return this.attributes;
    }

    public Attribute getAttribute(String name) {
        return (Attribute) this.attributes.get(name, Namespace.NO_NAMESPACE);
    }

    public Attribute getAttribute(String name, Namespace ns) {
        return (Attribute) this.attributes.get(name, ns);
    }

    public String getAttributeValue(String name) {
        Attribute attribute = (Attribute) this.attributes.get(name, Namespace.NO_NAMESPACE);
        return (attribute == null) ? null : attribute.getValue();
    }

    public String getAttributeValue(String name, Namespace ns, String def) {
        Attribute attribute = (Attribute) this.attributes.get(name, ns);
        return (attribute == null) ? def : attribute.getValue();
    }

    public String getAttributeValue(String name, String def) {
        Attribute attribute = (Attribute) this.attributes.get(name, Namespace.NO_NAMESPACE);
        return (attribute == null) ? def : attribute.getValue();
    }

    public String getAttributeValue(String name, Namespace ns) {
        Attribute attribute = (Attribute) this.attributes.get(name, ns);
        return (attribute == null) ? null : attribute.getValue();
    }

    public Element setAttributes(List newAttributes) {
        this.attributes.clearAndSet(newAttributes);
        return this;
    }

    public Element setAttribute(String name, String value) {
        return setAttribute(new Attribute(name, value));
    }

    public Element setAttribute(String name, String value, Namespace ns) {
        return setAttribute(new Attribute(name, value, ns));
    }

    public Element setAttribute(Attribute attribute) {
        this.attributes.add(attribute);
        return this;
    }

    public boolean removeAttribute(String name) {
        return this.attributes.remove(name, Namespace.NO_NAMESPACE);
    }

    public boolean removeAttribute(String name, Namespace ns) {
        return this.attributes.remove(name, ns);
    }

    public boolean removeAttribute(Attribute attribute) {
        return this.attributes.remove(attribute);
    }

    public boolean removeContent(Element element) {
        return this.content.remove(element);
    }

    public boolean removeContent(ProcessingInstruction pi) {
        return this.content.remove(pi);
    }

    public boolean removeContent(Comment comment) {
        return this.content.remove(comment);
    }

    public boolean removeContent(CDATA cdata) {
        return this.content.remove(cdata);
    }

    public boolean removeContent(Text text) {
        return this.content.remove(text);
    }

    public boolean removeContent(EntityRef entity) {
        return this.content.remove(entity);
    }

    public String toString() {
        StringBuffer stringForm = (new StringBuffer(64)).append("[Element: <").append(getQualifiedName());
        String nsuri = getNamespaceURI();
        if (!nsuri.equals(""))
            stringForm.append(" [Namespace: ").append(nsuri).append("]");
        stringForm.append("/>]");
        return stringForm.toString();
    }

    public final boolean equals(Object ob) {
        return !(this != ob);
    }

    public final int hashCode() {
        return super.hashCode();
    }

    public Object clone() {
        Element element = null;
        try {
            element = (Element) super.clone();
        } catch (CloneNotSupportedException cloneNotSupportedException) {
        }
        element.parent = null;
        element.content = new ContentList(element);
        element.attributes = new AttributeList(element);
        if (this.attributes != null)
            for (int i = 0; i < this.attributes.size(); i++) {
                Object obj = this.attributes.get(i);
                Attribute attribute = (Attribute) ((Attribute) obj).clone();
                element.attributes.add(attribute);
            }
        if (this.content != null)
            for (int i = 0; i < this.content.size(); i++) {
                Object obj = this.content.get(i);
                if (obj instanceof Element) {
                    Element elt = (Element) ((Element) obj).clone();
                    element.content.add(elt);
                } else if (obj instanceof Text) {
                    Text text = (Text) ((Text) obj).clone();
                    element.content.add(text);
                } else if (obj instanceof Comment) {
                    Comment comment = (Comment) ((Comment) obj).clone();
                    element.content.add(comment);
                } else if (obj instanceof CDATA) {
                    CDATA cdata = (CDATA) ((CDATA) obj).clone();
                    element.content.add(cdata);
                } else if (obj instanceof ProcessingInstruction) {
                    ProcessingInstruction pi = (ProcessingInstruction) ((ProcessingInstruction) obj).clone();
                    element.content.add(pi);
                } else if (obj instanceof EntityRef) {
                    EntityRef entity = (EntityRef) ((EntityRef) obj).clone();
                    element.content.add(entity);
                }
            }
        if (this.additionalNamespaces != null) {
            element.additionalNamespaces = new ArrayList();
            element.additionalNamespaces.addAll(this.additionalNamespaces);
        }
        return element;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.namespace.getPrefix());
        out.writeObject(this.namespace.getURI());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.namespace = Namespace.getNamespace((String) in.readObject(), (String) in.readObject());
    }

    protected Element() {
    }
}
