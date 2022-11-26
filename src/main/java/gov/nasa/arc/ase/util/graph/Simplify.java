package gov.nasa.arc.ase.util.graph;

import java.io.IOException;
import java.util.Iterator;

public class Simplify {
    public static void main(String[] paramArrayOfString) {
        if (paramArrayOfString.length > 1) {
            System.out.println("usage:");
            System.out.println("\tjava gov.nasa.arc.ase.util.graph.Simplify [<filename>]");
            return;
        }
        Graph graph = null;
        try {
            if (paramArrayOfString.length == 0) {
                graph = Graph.load();
            } else {
                graph = Graph.load(paramArrayOfString[0]);
            }
        } catch (IOException iOException) {
            System.out.println("Can't load the graph.");
            return;
        }
        graph = simplify(graph);
        graph.save();
    }

    public static Graph simplify(Graph paramGraph) {
        boolean bool;
        do {
            bool = false;
            for (Node node : paramGraph.getNodes()) {
                for (Node node1 : paramGraph.getNodes()) {
                    if (node1.getId() <= node.getId())
                        continue;
                    if (node1.getBooleanAttribute("accepting") != node.getBooleanAttribute("accepting"))
                        continue;
                    boolean bool1 = true;
                    for (Iterator iterator1 = node.getOutgoingEdges().iterator(); bool1 && iterator1.hasNext();) {
                        Edge edge = iterator1.next();
                        bool1 = false;
                        for (Iterator iterator = node1.getOutgoingEdges().iterator(); !bool1 && iterator.hasNext();) {
                            Edge edge1 = iterator.next();
                            if (edge.getNext() == edge1.getNext() || ((edge.getNext() == node || edge.getNext() == node1) && (edge1.getNext() == node || edge1.getNext() == node1)))
                                if (edge.getGuard().equals(edge1.getGuard()) && edge.getAction().equals(edge1.getAction()))
                                    bool1 = true;
                        }
                    }
                    for (Iterator iterator2 = node1.getOutgoingEdges().iterator(); bool1 && iterator2.hasNext();) {
                        Edge edge = iterator2.next();
                        bool1 = false;
                        for (Iterator iterator = node.getOutgoingEdges().iterator(); !bool1 && iterator.hasNext();) {
                            Edge edge1 = iterator.next();
                            if (edge1.getNext() == edge.getNext() || ((edge1.getNext() == node || edge1.getNext() == node1) && (edge.getNext() == node || edge.getNext() == node1)))
                                if (edge1.getGuard().equals(edge.getGuard()) && edge1.getAction().equals(edge.getAction()))
                                    bool1 = true;
                        }
                    }
                    if (bool1) {
                        for (Iterator iterator = node1.getIncomingEdges().iterator(); bool1 && iterator.hasNext();) {
                            Edge edge = iterator.next();
                            new Edge(edge.getSource(), node, edge.getGuard(), edge.getAction(), edge.getAttributes());
                        }
                        node1.remove();
                        bool = true;
                    }
                }
            }
        } while (bool);
        return paramGraph;
    }
}
