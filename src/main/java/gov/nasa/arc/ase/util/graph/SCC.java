package gov.nasa.arc.ase.util.graph;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class SCC {
    public static void help() {
        System.err.println("usage:");
        System.err.println("\tDegenalize [-join|-degeneralize] [outfile]");
        System.exit(1);
    }

    public static void main(String[] paramArrayOfString) {
        String str = null;
        byte b;
        int i;
        for (b = 0, i = paramArrayOfString.length; b < i; b++) {
            if (str == null) {
                str = paramArrayOfString[b];
            } else {
                help();
            }
        }
        try {
            Graph graph = Graph.load("out.sm");
            List list = scc(graph);
            for (List list1 : list) {
                System.out.println("component:");
                for (Node node : list1)
                    System.out.println("  " + node.getStringAttribute("label"));
                System.out.println();
            }
            if (str == null) {
                graph.save();
            } else {
                graph.save(str);
            }
        } catch (IOException iOException) {
            iOException.printStackTrace();
            return;
        }
    }

    private static class SCCState {
        private SCCState() {
        }

        public int N = 0;
        public int SCC = 0;
        public List L = new LinkedList();
    }

    public static List scc(Graph paramGraph) {
        Node node = paramGraph.getInit();
        if (node == null)
            return new LinkedList();
        node.setBooleanAttribute("_reached", true);
        SCCState sCCState = new SCCState();
        visit(node, sCCState);
        List[] arrayOfList = new List[sCCState.SCC];
        for (byte b1 = 0; b1 < sCCState.SCC; b1++)
            arrayOfList[b1] = new LinkedList();
        paramGraph.forAllNodes(new EmptyVisitor(arrayOfList) {
            private final List[] val$scc;

            public void visitNode(Node param1Node) {
                this.val$scc[param1Node.getIntAttribute("_scc")].add(param1Node);
                param1Node.setBooleanAttribute("_reached", false);
                param1Node.setBooleanAttribute("_dfsnum", false);
                param1Node.setBooleanAttribute("_low", false);
                param1Node.setBooleanAttribute("_scc", false);
            }
        });
        LinkedList linkedList = new LinkedList();
        for (byte b2 = 0; b2 < sCCState.SCC; b2++)
            linkedList.add(arrayOfList[b2]);
        return linkedList;
    }

    private static void visit(Node paramNode, SCCState paramSCCState) {
        paramSCCState.L.add(0, paramNode);
        paramNode.setIntAttribute("_dfsnum", paramSCCState.N);
        paramNode.setIntAttribute("_low", paramSCCState.N);
        paramSCCState.N++;
        for (Edge edge : paramNode.getOutgoingEdges()) {
            Node node = edge.getNext();
            if (!node.getBooleanAttribute("_reached")) {
                node.setBooleanAttribute("_reached", true);
                visit(node, paramSCCState);
                paramNode.setIntAttribute("_low", Math.min(paramNode.getIntAttribute("_low"), node.getIntAttribute("_low")));
                continue;
            }
            if (node.getIntAttribute("_dfsnum") < paramNode.getIntAttribute("_dfsnum") && paramSCCState.L.contains(node))
                paramNode.setIntAttribute("_low", Math.min(paramNode.getIntAttribute("_low"), node.getIntAttribute("_dfsnum")));
        }
        if (paramNode.getIntAttribute("_low") == paramNode.getIntAttribute("_dfsnum"))
            while (true) {
                Node node = paramSCCState.L.remove(0);
                node.setIntAttribute("_scc", paramSCCState.SCC);
                if (node == paramNode) {
                    paramSCCState.SCC++;
                    break;
                }
            }
    }

    public static void print(List paramList) {
        System.out.println("Strongly connected components:");
        byte b = 0;
        for (List list : paramList) {
            System.out.println("\tSCC #" + b++);
            for (Node node : list)
                System.out.println("\t\t" + node.getId() + " - " + node.getStringAttribute("label"));
        }
    }
}
