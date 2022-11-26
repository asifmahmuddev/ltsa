package gov.nasa.arc.ase.util.graph;

import java.io.PrintStream;

public class Edge {
    private Node source;
    private Node next;
    private String guard;
    private String action;
    private Attributes attributes;

    public Edge(Node paramNode1, Node paramNode2, String paramString1, String paramString2, Attributes paramAttributes) {
        init(paramNode1, paramNode2, paramString1, paramString2, paramAttributes);
    }

    public Edge(Node paramNode1, Node paramNode2, String paramString1, String paramString2) {
        init(paramNode1, paramNode2, paramString1, paramString2, null);
    }

    public Edge(Node paramNode1, Node paramNode2, String paramString) {
        init(paramNode1, paramNode2, paramString, "-", null);
    }

    public Edge(Node paramNode1, Node paramNode2) {
        init(paramNode1, paramNode2, "-", "-", null);
    }

    public Edge(Node paramNode, Edge paramEdge) {
        init(paramNode, paramEdge.next, new String(paramEdge.guard), new String(paramEdge.action), new Attributes(paramEdge.attributes));
    }

    public Edge(Edge paramEdge, Node paramNode) {
        init(paramEdge.source, paramNode, new String(paramEdge.guard), new String(paramEdge.action), new Attributes(paramEdge.attributes));
    }

    public Edge(Edge paramEdge) {
        init(paramEdge.source, paramEdge.next, new String(paramEdge.guard), new String(paramEdge.action), new Attributes(paramEdge.attributes));
    }

    public Node getSource() {
        return this.source;
    }

    public Node getNext() {
        return this.next;
    }

    public String getGuard() {
        return this.guard;
    }

    public String getAction() {
        return this.action;
    }

    public Attributes getAttributes() {
        return this.attributes;
    }

    public int getIntAttribute(String paramString) {
        return this.attributes.getInt(paramString);
    }

    public String getStringAttribute(String paramString) {
        return this.attributes.getString(paramString);
    }

    public boolean getBooleanAttribute(String paramString) {
        return this.attributes.getBoolean(paramString);
    }

    public void setIntAttribute(String paramString, int paramInt) {
        this.attributes.setInt(paramString, paramInt);
    }

    public void setStringAttribute(String paramString1, String paramString2) {
        this.attributes.setString(paramString1, paramString2);
    }

    public void setBooleanAttribute(String paramString, boolean paramBoolean) {
        this.attributes.setBoolean(paramString, paramBoolean);
    }

    public synchronized void remove() {
        this.source.removeOutgoingEdge(this);
        this.next.removeIncomingEdge(this);
    }

    public synchronized void setAttributes(Attributes paramAttributes) {
        this.attributes = new Attributes(paramAttributes);
    }

    private void init(Node paramNode1, Node paramNode2, String paramString1, String paramString2, Attributes paramAttributes) {
        this.source = paramNode1;
        this.next = paramNode2;
        this.guard = paramString1;
        this.action = paramString2;
        if (paramAttributes == null) {
            this.attributes = new Attributes();
        } else {
            this.attributes = paramAttributes;
        }
        paramNode1.addOutgoingEdge(this);
        paramNode2.addIncomingEdge(this);
    }

    void save(PrintStream paramPrintStream, int paramInt) {
        switch (paramInt) {
            case 0 :
                save_sm(paramPrintStream);
                break;
            case 1 :
                save_fsp(paramPrintStream);
                break;
        }
    }

    private void save_fsp(PrintStream paramPrintStream) {
        String str1, str2 = "";
        if (this.guard.equals("-")) {
            str1 = "TRUE";
        } else {
            str1 = this.guard;
        }
        int i = this.source.getGraph().getIntAttribute("nsets");
        if (i == 0) {
            if (getBooleanAttribute("accepting"))
                str2 = "@";
        } else {
            boolean bool = true;
            StringBuffer stringBuffer = new StringBuffer();
            for (byte b = 0; b < i; b++) {
                if (getBooleanAttribute("acc" + b)) {
                    if (bool) {
                        bool = false;
                    } else {
                        stringBuffer.append(",");
                    }
                    stringBuffer.append(b);
                }
            }
            if (!bool)
                str2 = "{" + stringBuffer.toString() + "}";
        }
        paramPrintStream.print(str1 + str2 + "-> S" + this.next.getId());
    }

    private void save_sm(PrintStream paramPrintStream) {
        paramPrintStream.print("    ");
        paramPrintStream.println(this.next.getId());
        paramPrintStream.print("    ");
        paramPrintStream.println(this.guard);
        paramPrintStream.print("    ");
        paramPrintStream.println(this.action);
        paramPrintStream.print("    ");
        paramPrintStream.println(this.attributes);
    }
}
