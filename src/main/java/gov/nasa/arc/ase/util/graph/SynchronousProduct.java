package gov.nasa.arc.ase.util.graph;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class SynchronousProduct {
    public static Graph product(Graph paramGraph1, Graph paramGraph2) {
        int i = paramGraph1.getIntAttribute("nsets");
        if (i != paramGraph2.getIntAttribute("nsets")) {
            System.err.println("Different number of accepting sets");
            System.exit(1);
        }
        Graph graph = new Graph();
        graph.setStringAttribute("type", "ba");
        graph.setStringAttribute("ac", "nodes");
        Node[][] arrayOfNode = new Node[paramGraph1.getNodeCount()][paramGraph2.getNodeCount()];
        dfs(graph, arrayOfNode, i, paramGraph1.getInit(), paramGraph2.getInit());
        return graph;
    }

    public static void main(String[] paramArrayOfString) {
        Graph graph1, graph2;
        try {
            graph1 = Graph.load(paramArrayOfString[0]);
            graph2 = Graph.load(paramArrayOfString[1]);
        } catch (IOException iOException) {
            System.err.println("Can't load automata");
            System.exit(1);
            return;
        }
        Graph graph3 = product(graph1, graph2);
        graph3.save();
    }

    public static void dfs(Graph paramGraph, Node[][] paramArrayOfNode, int paramInt, Node paramNode1, Node paramNode2) {
        Node node = get(paramGraph, paramArrayOfNode, paramNode1, paramNode2);
        List list1 = paramNode1.getOutgoingEdges();
        List list2 = paramNode2.getOutgoingEdges();
        for (Edge edge1 : list1) {
            Node node1 = edge1.getNext();
            Edge edge2 = null;
            boolean bool = false;
            for (Iterator iterator = list2.iterator(); iterator.hasNext() && !bool;) {
                Edge edge = iterator.next();
                if (edge.getBooleanAttribute("else")) {
                    if (edge2 == null)
                        edge2 = edge;
                } else {
                    bool = true;
                    for (byte b = 0; b < paramInt; b++) {
                        boolean bool1 = edge1.getBooleanAttribute("acc" + b);
                        boolean bool2 = edge.getBooleanAttribute("acc" + b);
                        if (bool2 && !bool1) {
                            bool = false;
                            break;
                        }
                    }
                }
                if (bool)
                    edge2 = edge;
            }
            if (edge2 != null) {
                Node node2 = edge2.getNext();
                boolean bool1 = isNew(paramArrayOfNode, node1, node2);
                Node node3 = get(paramGraph, paramArrayOfNode, node1, node2);
                Edge edge = new Edge(node, node3, edge1.getGuard(), edge2.getAction(), null);
                if (bool1)
                    dfs(paramGraph, paramArrayOfNode, paramInt, node1, node2);
            }
        }
    }

    private static boolean isNew(Node[][] paramArrayOfNode, Node paramNode1, Node paramNode2) {
        return (paramArrayOfNode[paramNode1.getId()][paramNode2.getId()] == null);
    }

    private static Node get(Graph paramGraph, Node[][] paramArrayOfNode, Node paramNode1, Node paramNode2) {
        if (paramArrayOfNode[paramNode1.getId()][paramNode2.getId()] == null) {
            Node node = new Node(paramGraph);
            String str1 = paramNode1.getStringAttribute("label");
            String str2 = paramNode2.getStringAttribute("label");
            if (str1 == null)
                str1 = Integer.toString(paramNode1.getId());
            if (str2 == null)
                str2 = Integer.toString(paramNode2.getId());
            node.setStringAttribute("label", str1 + "+" + str2);
            if (paramNode2.getBooleanAttribute("accepting"))
                node.setBooleanAttribute("accepting", true);
            paramArrayOfNode[paramNode1.getId()][paramNode2.getId()] = node;
            return node;
        }
        return paramArrayOfNode[paramNode1.getId()][paramNode2.getId()];
    }
}
