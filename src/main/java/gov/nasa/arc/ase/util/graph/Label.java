package gov.nasa.arc.ase.util.graph;

import java.io.IOException;

public class Label {
    public static Graph label(Graph paramGraph) {
        String str1 = paramGraph.getStringAttribute("type");
        String str2 = paramGraph.getStringAttribute("ac");
        if (str1.equals("gba")) {
            if (str2.equals("nodes")) {
                int i = paramGraph.getIntAttribute("nsets");
                paramGraph.forAllNodes(new EmptyVisitor(i) {
                    private final int val$nsets;

                    public void visitNode(Node param1Node) {
                        param1Node.forAllEdges((Visitor) new Object(this));
                        for (byte b = 0; b < this.val$nsets; b++)
                            param1Node.setBooleanAttribute("acc" + b, false);
                    }
                });
            }
            paramGraph.setStringAttribute("ac", "edges");
        } else {
            throw new RuntimeException("invalid graph type: " + str1);
        }
        return paramGraph;
    }

    public static void main(String[] paramArrayOfString) {
        try {
            Graph graph = Graph.load(paramArrayOfString[0]);
            label(graph);
            graph.save();
        } catch (IOException iOException) {
            System.err.println("Can't load file: " + paramArrayOfString[0]);
            System.exit(1);
        }
    }
}
