package gov.nasa.arc.ase.util.graph;

import java.io.IOException;

public class SuperSetReduction {
    public static void main(String[] paramArrayOfString) {
        if (paramArrayOfString.length > 1) {
            System.out.println("usage:");
            System.out.println("\tjava gov.nasa.arc.ase.util.graph.SuperSetReduction [<filename>]");
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
        graph = reduce(graph);
        graph.save();
    }

    public static Graph reduce(Graph paramGraph) {
        int i = paramGraph.getIntAttribute("nsets");
        String str1 = paramGraph.getStringAttribute("type");
        String str2 = paramGraph.getStringAttribute("ac");
        if (!str1.equals("gba"))
            throw new RuntimeException("invalid graph type: " + str1);
        if (str2.equals("nodes")) {
            int j = paramGraph.getNodeCount();
            boolean[][] arrayOfBoolean1 = new boolean[i][j];
            paramGraph.forAllNodes(new EmptyVisitor(i, arrayOfBoolean1) {
                private final int val$nsets;
                private final boolean[][] val$asets;

                public void visitNode(Node param1Node) {
                    for (byte b = 0; b < this.val$nsets; b++) {
                        String str = "acc" + b;
                        if (param1Node.getBooleanAttribute(str)) {
                            this.val$asets[b][param1Node.getId()] = true;
                            param1Node.setBooleanAttribute(str, false);
                        }
                    }
                }
            });
            boolean[] arrayOfBoolean = new boolean[i];
            for (byte b1 = 0; b1 < i; b1++) {
                for (byte b = 0; b < i && !arrayOfBoolean[b1]; b++) {
                    if (b1 != b && !arrayOfBoolean[b] && included(arrayOfBoolean1[b], arrayOfBoolean1[b1]))
                        arrayOfBoolean[b1] = true;
                }
            }
            byte b2 = 0;
            for (byte b3 = 0; b3 < i; b3++) {
                if (!arrayOfBoolean[b3])
                    b2++;
            }
            boolean[][] arrayOfBoolean2 = new boolean[b2][j];
            b2 = 0;
            for (byte b4 = 0; b4 < i; b4++) {
                if (!arrayOfBoolean[b4])
                    arrayOfBoolean2[b2++] = arrayOfBoolean1[b4];
            }
            paramGraph.setIntAttribute("nsets", b2);
            for (byte b5 = 0; b5 < j; b5++) {
                Node node = paramGraph.getNode(b5);
                for (byte b = 0; b < b2; b++) {
                    if (arrayOfBoolean2[b][b5])
                        node.setBooleanAttribute("acc" + b, true);
                }
            }
            return paramGraph;
        }
        if (str2.equals("edges")) {
            int j = paramGraph.getEdgeCount();
            boolean[][] arrayOfBoolean1 = new boolean[i][j];
            Edge[] arrayOfEdge = new Edge[j];
            paramGraph.forAllEdges(new EmptyVisitor(arrayOfEdge, i, arrayOfBoolean1, new Integer(0)) {
                private final Edge[] val$edges;
                private final int val$nsets;
                private final boolean[][] val$asets;

                public void visitEdge(Edge param1Edge) {
                    int i = ((Integer) this.arg).intValue();
                    this.arg = new Integer(i + 1);
                    this.val$edges[i] = param1Edge;
                    for (byte b = 0; b < this.val$nsets; b++) {
                        String str = "acc" + b;
                        if (param1Edge.getBooleanAttribute(str)) {
                            this.val$asets[b][i] = true;
                            param1Edge.setBooleanAttribute(str, false);
                        }
                    }
                }
            });
            boolean[] arrayOfBoolean = new boolean[i];
            for (byte b1 = 0; b1 < i; b1++) {
                for (byte b = 0; b < i && !arrayOfBoolean[b1]; b++) {
                    if (b1 != b && !arrayOfBoolean[b] && included(arrayOfBoolean1[b], arrayOfBoolean1[b1]))
                        arrayOfBoolean[b1] = true;
                }
            }
            byte b2 = 0;
            for (byte b3 = 0; b3 < i; b3++) {
                if (!arrayOfBoolean[b3])
                    b2++;
            }
            boolean[][] arrayOfBoolean2 = new boolean[b2][j];
            b2 = 0;
            for (byte b4 = 0; b4 < i; b4++) {
                if (!arrayOfBoolean[b4])
                    arrayOfBoolean2[b2++] = arrayOfBoolean1[b4];
            }
            paramGraph.setIntAttribute("nsets", b2);
            for (byte b5 = 0; b5 < j; b5++) {
                Edge edge = arrayOfEdge[b5];
                for (byte b = 0; b < b2; b++) {
                    if (arrayOfBoolean2[b][b5])
                        edge.setBooleanAttribute("acc" + b, true);
                }
            }
            return paramGraph;
        }
        throw new RuntimeException("invalid accepting type: " + str2);
    }

    private static boolean included(boolean[] paramArrayOfboolean1, boolean[] paramArrayOfboolean2) {
        int i = paramArrayOfboolean1.length;
        int j = paramArrayOfboolean2.length;
        for (byte b = 0; b < i; b++) {
            if (paramArrayOfboolean1[b] && !paramArrayOfboolean2[b])
                return false;
        }
        return true;
    }
}
