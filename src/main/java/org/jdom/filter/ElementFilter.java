package org.jdom.filter;

import org.jdom.Element;
import org.jdom.Namespace;

public class ElementFilter implements Filter {
    private static final String CVS_ID = "@(#) $RCSfile: ElementFilter.java,v $ $Revision: 1.2 $ $Date: 2002/03/13 06:25:33 $ $Name: jdom_1_0_b8 $";
    protected String name;
    protected Namespace namespace;

    public ElementFilter() {
    }

    public ElementFilter(String name) {
        this.name = name;
    }

    public ElementFilter(Namespace namespace) {
        this.namespace = namespace;
    }

    public ElementFilter(String name, Namespace namespace) {
        this.name = name;
        this.namespace = namespace;
    }

    public boolean canAdd(Object obj) {
        return matches(obj);
    }

    public boolean canRemove(Object obj) {
        if (obj instanceof Element)
            return true;
        return false;
    }

    public boolean matches(Object obj) {
        if (obj instanceof Element) {
            Element element = (Element) obj;
            if (this.name == null) {
                if (this.namespace == null)
                    return true;
                return this.namespace.equals(element.getNamespace());
            }
            if (this.name.equals(element.getName())) {
                if (this.namespace == null)
                    return true;
                return this.namespace.equals(element.getNamespace());
            }
        }
        return false;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj instanceof ElementFilter) {
            ElementFilter filter = (ElementFilter) obj;
            if (this.name == filter.name && this.namespace == filter.namespace)
                return true;
        }
        return false;
    }
}
