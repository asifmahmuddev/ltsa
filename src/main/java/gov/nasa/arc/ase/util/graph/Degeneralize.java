package gov.nasa.arc.ase.util.graph;

import java.io.IOException;

public class Degeneralize {
    public static void help() {
        System.err.println("usage:");
        System.err.println("\tDegenalize [outfile]");
        System.exit(1);
    }

    public static void main(String[] paramArrayOfString) {
        if (paramArrayOfString.length > 1) {
            System.out.println("usage:");
            System.out.println("\tjava gov.nasa.arc.ase.util.graph.Degeneralize [<filename>]");
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
        graph = degeneralize(graph);
        graph.save();
    }

    private static void accept(Graph paramGraph) {
        paramGraph.setBooleanAttribute("nsets", false);
        paramGraph.forAllNodes(new EmptyVisitor() {
            public void visitNode(Node param1Node) {
                if (param1Node.getBooleanAttribute("acc0")) {
                    param1Node.setBooleanAttribute("accepting", true);
                    param1Node.setBooleanAttribute("acc0", false);
                }
            }
        });
    }

    public static Graph degeneralize(Graph paramGraph) {
        int i = paramGraph.getIntAttribute("nsets");
        String str = paramGraph.getStringAttribute("type");
        if (str.equals("gba")) {
            String str1 = paramGraph.getStringAttribute("ac");
            if (str1.equals("nodes")) {
                if (i == 1) {
                    accept(paramGraph);
                } else {
                    Label.label(paramGraph);
                    Graph graph = Generate.generate(i);
                    paramGraph = SynchronousProduct.product(paramGraph, graph);
                }
            } else if (str1.equals("edges")) {
                Graph graph = Generate.generate(i);
                paramGraph = SynchronousProduct.product(paramGraph, graph);
            }
        } else if (!str.equals("ba")) {
            throw new RuntimeException("invalid graph type: " + str);
        }
        return paramGraph;
    }
}
