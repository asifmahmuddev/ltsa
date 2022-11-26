package gov.nasa.arc.ase.util.graph;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Node {
    private Graph graph;
    private List outgoingEdges;
    private List incomingEdges;
    private Attributes attributes;

    public Node(Graph paramGraph, Attributes paramAttributes) {
        init(paramGraph, paramAttributes);
    }

    public Node(Graph paramGraph) {
        init(paramGraph, null);
    }

    public Node(Node paramNode) {
        init(paramNode.graph, new Attributes(paramNode.attributes));
        for (Iterator iterator1 = paramNode.outgoingEdges.iterator(); iterator1.hasNext();)
            new Edge(this, iterator1.next());
        for (Iterator iterator2 = paramNode.incomingEdges.iterator(); iterator2.hasNext();)
            new Edge(iterator2.next(), this);
    }

    public int getOutgoingEdgeCount() {
        return this.outgoingEdges.size();
    }

    public int getIncomingEdgeCount() {
        return this.outgoingEdges.size();
    }

    public Graph getGraph() {
        return this.graph;
    }

    public List getOutgoingEdges() {
        return new LinkedList(this.outgoingEdges);
    }

    public List getIncomingEdges() {
        return new LinkedList(this.incomingEdges);
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

    public synchronized void setIntAttribute(String paramString, int paramInt) {
        if (paramString.equals("_id"))
            return;
        this.attributes.setInt(paramString, paramInt);
    }

    public synchronized void setStringAttribute(String paramString1, String paramString2) {
        if (paramString1.equals("_id"))
            return;
        this.attributes.setString(paramString1, paramString2);
    }

    public synchronized void setBooleanAttribute(String paramString, boolean paramBoolean) {
        if (paramString.equals("_id"))
            return;
        this.attributes.setBoolean(paramString, paramBoolean);
    }

    synchronized void setId(int paramInt) {
        this.attributes.setInt("_id", paramInt);
    }

    public synchronized int getId() {
        return this.attributes.getInt("_id");
    }

    public synchronized void remove() {
        for (Iterator iterator1 = (new LinkedList(this.outgoingEdges)).iterator(); iterator1.hasNext();)
            ((Edge) iterator1.next()).remove();
        for (Iterator iterator2 = (new LinkedList(this.incomingEdges)).iterator(); iterator2.hasNext();)
            ((Edge) iterator2.next()).remove();
        this.graph.removeNode(this);
    }

    synchronized void removeOutgoingEdge(Edge paramEdge) {
        this.outgoingEdges.remove(paramEdge);
    }

    synchronized void removeIncomingEdge(Edge paramEdge) {
        this.incomingEdges.remove(paramEdge);
    }

    synchronized void addOutgoingEdge(Edge paramEdge) {
        this.outgoingEdges.add(paramEdge);
    }

    synchronized void addIncomingEdge(Edge paramEdge) {
        this.incomingEdges.add(paramEdge);
    }

    public synchronized void setAttributes(Attributes paramAttributes) {
        int i = getId();
        this.attributes = new Attributes(paramAttributes);
        setId(i);
    }

    private void init(Graph paramGraph, Attributes paramAttributes) {
        this.graph = paramGraph;
        if (paramAttributes == null) {
            this.attributes = new Attributes();
        } else {
            this.attributes = paramAttributes;
        }
        this.incomingEdges = new LinkedList();
        this.outgoingEdges = new LinkedList();
        this.graph.addNode(this);
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
        paramPrintStream.print("S" + getId() + "=(");
        for (Iterator iterator = this.outgoingEdges.iterator(); iterator.hasNext();) {
            ((Edge) iterator.next()).save(paramPrintStream, 1);
            if (iterator.hasNext())
                paramPrintStream.print(" |");
        }
        paramPrintStream.print(")");
    }

    private void save_sm(PrintStream paramPrintStream) {
        int i = getId();
        paramPrintStream.print("  ");
        paramPrintStream.println(this.outgoingEdges.size());
        this.attributes.unset("_id");
        paramPrintStream.print("  ");
        paramPrintStream.println(this.attributes);
        setId(i);
        for (Iterator iterator = this.outgoingEdges.iterator(); iterator.hasNext();)
            ((Edge) iterator.next()).save(paramPrintStream, 0);
    }

    public synchronized void forAllEdges(Visitor paramVisitor) {
        for (Iterator iterator = (new LinkedList(this.outgoingEdges)).iterator(); iterator.hasNext();)
            paramVisitor.visitEdge((Edge) iterator.next());
    }
}
