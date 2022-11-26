package org.jdom.output;

import java.util.Stack;
import org.jdom.Namespace;

class NamespaceStack {
    private static final String CVS_ID = "@(#) $RCSfile: NamespaceStack.java,v $ $Revision: 1.8 $ $Date: 2002/03/12 07:57:06 $ $Name: jdom_1_0_b8 $";
    private Stack prefixes = new Stack();
    private Stack uris = new Stack();

    public void push(Namespace ns) {
        this.prefixes.push(ns.getPrefix());
        this.uris.push(ns.getURI());
    }

    public String pop() {
        String prefix = this.prefixes.pop();
        this.uris.pop();
        return prefix;
    }

    public int size() {
        return this.prefixes.size();
    }

    public String getURI(String prefix) {
        int index = this.prefixes.lastIndexOf(prefix);
        if (index == -1)
            return null;
        String uri = this.uris.elementAt(index);
        return uri;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        String sep = System.getProperty("line.separator");
        buf.append("Stack: " + this.prefixes.size() + sep);
        for (int i = 0; i < this.prefixes.size(); i++)
            buf.append(String.valueOf(String.valueOf(this.prefixes.elementAt(i))) + "&" + this.uris.elementAt(i) + sep);
        return buf.toString();
    }
}
