package gov.nasa.arc.ase.util.graph;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Graph {
    public static final int SM_FORMAT = 0;
    public static final int FSP_FORMAT = 1;
    private List nodes;
    private Node init;
    private Attributes attributes;

    public Graph(Attributes paramAttributes) {
        init(paramAttributes);
    }

    public Graph() {
        init(null);
    }

    public List getNodes() {
        return new LinkedList(this.nodes);
    }

    public int getNodeCount() {
        return this.nodes.size();
    }

    public int getEdgeCount() {
        int i = 0;
        for (Iterator iterator = (new LinkedList(this.nodes)).iterator(); iterator.hasNext();)
            i += ((Node) iterator.next()).getOutgoingEdgeCount();
        return i;
    }

    public Node getInit() {
        return this.init;
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
        this.attributes.setInt(paramString, paramInt);
    }

    public synchronized void setStringAttribute(String paramString1, String paramString2) {
        this.attributes.setString(paramString1, paramString2);
    }

    public synchronized void setBooleanAttribute(String paramString, boolean paramBoolean) {
        this.attributes.setBoolean(paramString, paramBoolean);
    }

    public synchronized void save(int paramInt) {
        save(System.out, paramInt);
    }

    public synchronized void save() {
        save(System.out, 0);
    }

    public synchronized void save(String paramString, int paramInt) throws IOException {
        save(new PrintStream(new FileOutputStream(paramString)), paramInt);
    }

    public synchronized void save(String paramString) throws IOException {
        save(new PrintStream(new FileOutputStream(paramString)), 0);
    }

    public synchronized void setInit(Node paramNode) {
        if (this.nodes.contains(paramNode)) {
            this.init = paramNode;
            number();
        }
    }

    synchronized void removeNode(Node paramNode) {
        this.nodes.remove(paramNode);
        if (this.init == paramNode)
            if (this.nodes.size() != 0) {
                this.init = this.nodes.get(0);
            } else {
                this.init = null;
            }
        number();
    }

    synchronized void addNode(Node paramNode) {
        this.nodes.add(paramNode);
        if (this.init == null)
            this.init = paramNode;
        number();
    }

    public synchronized void setAttributes(Attributes paramAttributes) {
        this.attributes = new Attributes(paramAttributes);
    }

    private void init(Attributes paramAttributes) {
        if (paramAttributes == null) {
            this.attributes = new Attributes();
        } else {
            this.attributes = paramAttributes;
        }
        this.nodes = new LinkedList();
        this.init = null;
    }

    private synchronized void save(PrintStream paramPrintStream, int paramInt) {
        switch (paramInt) {
            case 0 :
                save_sm(paramPrintStream);
                break;
            case 1 :
                save_fsp(paramPrintStream);
                break;
        }
    }

    private synchronized void save_fsp(PrintStream paramPrintStream) {
        boolean bool = false;
        if (this.init != null) {
            paramPrintStream.print("RES = S" + this.init.getId());
        } else {
            paramPrintStream.print("Empty");
            bool = true;
        }
        for (Iterator iterator = this.nodes.iterator(); iterator.hasNext();) {
            paramPrintStream.println(",");
            Node node = iterator.next();
            node.save(paramPrintStream, 1);
        }
        paramPrintStream.println(".");
        int i = getIntAttribute("nsets");
        if (i == 0 && !bool) {
            boolean bool1 = true;
            paramPrintStream.print("AS = { ");
            for (Node node : this.nodes) {
                if (node.getBooleanAttribute("accepting")) {
                    if (!bool1) {
                        paramPrintStream.print(", ");
                    } else {
                        bool1 = false;
                    }
                    paramPrintStream.print("S" + node.getId());
                }
            }
            paramPrintStream.println(" }");
        } else if (!bool) {
            for (byte b = 0; b < i; b++) {
                boolean bool1 = true;
                paramPrintStream.print("AS" + b + " = { ");
                for (Node node : this.nodes) {
                    if (node.getBooleanAttribute("acc" + b)) {
                        if (!bool1) {
                            paramPrintStream.print(", ");
                        } else {
                            bool1 = false;
                        }
                        paramPrintStream.print("S" + node.getId());
                    }
                }
                paramPrintStream.println(" }");
            }
        }
        if (paramPrintStream != System.out)
            paramPrintStream.close();
    }

    private synchronized void save_sm(PrintStream paramPrintStream) {
        paramPrintStream.println(this.nodes.size());
        paramPrintStream.println(this.attributes);
        if (this.init != null)
            this.init.save(paramPrintStream, 0);
        for (Node node : this.nodes) {
            if (node != this.init)
                node.save(paramPrintStream, 0);
        }
    }

    private synchronized void number() {
        byte b;
        if (this.init != null) {
            this.init.setId(0);
            b = 1;
        } else {
            b = 0;
        }
        for (Node node : this.nodes) {
            if (node != this.init)
                node.setId(b++);
        }
    }

    public Node getNode(int paramInt) {
        for (Node node : this.nodes) {
            if (node.getId() == paramInt)
                return node;
        }
        return null;
    }

    public static Graph load() throws IOException {
        return load(new BufferedReader(new InputStreamReader(System.in)));
    }

    public static Graph load(String paramString) throws IOException {
        return load(new BufferedReader(new FileReader(paramString)));
    }

    private static String readLine(BufferedReader paramBufferedReader) throws IOException {
        String str;
        do {
            str = paramBufferedReader.readLine();
            int i = str.indexOf('#');
            if (i != -1)
                str = str.substring(0, i);
            str = str.trim();
        } while (str.length() == 0);
        return str;
    }

    private static String readString(BufferedReader paramBufferedReader) throws IOException {
        return readLine(paramBufferedReader);
    }

    private static int readInt(BufferedReader paramBufferedReader) throws IOException {
        return Integer.parseInt(readLine(paramBufferedReader));
    }

    private static Attributes readAttributes(BufferedReader paramBufferedReader) throws IOException {
        return new Attributes(readLine(paramBufferedReader));
    }

    private static Graph load(BufferedReader paramBufferedReader) throws IOException {
        int i = readInt(paramBufferedReader);
        Node[] arrayOfNode = new Node[i];
        Graph graph = new Graph(readAttributes(paramBufferedReader));
        for (byte b = 0; b < i; b++) {
            int j = readInt(paramBufferedReader);
            if (arrayOfNode[b] == null) {
                arrayOfNode[b] = new Node(graph, readAttributes(paramBufferedReader));
            } else {
                arrayOfNode[b].setAttributes(readAttributes(paramBufferedReader));
            }
            for (byte b1 = 0; b1 < j; b1++) {
                int k = readInt(paramBufferedReader);
                String str1 = readString(paramBufferedReader);
                String str2 = readString(paramBufferedReader);
                if (arrayOfNode[k] == null)
                    arrayOfNode[k] = new Node(graph);
                new Edge(arrayOfNode[b], arrayOfNode[k], str1, str2, readAttributes(paramBufferedReader));
            }
        }
        graph.number();
        return graph;
    }

    public synchronized void dfs(Visitor paramVisitor) {
        if (this.init == null)
            return;
        forAllNodes(new EmptyVisitor(this) {
            private final Graph this$0;

            public void visitNode(Node param1Node) {
                param1Node.setBooleanAttribute("_reached", false);
            }
        });
        dfs(this.init, paramVisitor);
        forAllNodes(new EmptyVisitor(this) {
            private final Graph this$0;

            public void visitNode(Node param1Node) {
                param1Node.setBooleanAttribute("_reached", false);
            }
        });
    }

    public synchronized void forAllNodes(Visitor paramVisitor) {
        for (Node node : new LinkedList(this.nodes))
            paramVisitor.visitNode(node);
    }

    public synchronized void forAllEdges(Visitor paramVisitor) {
        for (Node node : new LinkedList(this.nodes))
            node.forAllEdges(paramVisitor);
    }

    public synchronized void forAll(Visitor paramVisitor) {
        for (Node node : new LinkedList(this.nodes)) {
            paramVisitor.visitNode(node);
            node.forAllEdges(paramVisitor);
        }
    }

    private synchronized void dfs(Node paramNode, Visitor paramVisitor) {
        Visitor visitor = paramVisitor;
        if (paramNode.getBooleanAttribute("_reached"))
            return;
        paramNode.setBooleanAttribute("_reached", true);
        paramVisitor.visitNode(paramNode);
        paramNode.forAllEdges(new EmptyVisitor(this, visitor) {
            private final Visitor val$visitor;
            private final Graph this$0;

            public void visitEdge(Edge param1Edge) {
                this.this$0.dfs(param1Edge.getNext(), this.val$visitor);
            }
        });
    }
}
