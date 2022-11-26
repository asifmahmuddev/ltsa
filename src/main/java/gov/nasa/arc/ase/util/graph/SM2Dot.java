package gov.nasa.arc.ase.util.graph;

import java.io.IOException;

public class SM2Dot {
    public static void main(String[] paramArrayOfString) {
        if (paramArrayOfString.length != 1) {
            System.err.println("usage:");
            System.err.println("\tSM2Dot <filename>");
            System.err.println();
            System.exit(1);
        }
        try {
            Graph graph = Graph.load(paramArrayOfString[0]);
            startDigraph(paramArrayOfString[0]);
            printInit(graph.getInit());
            graph.forAllNodes(new EmptyVisitor() {
                public void visitNode(Node param1Node) {
                    SM2Dot.printNode(param1Node);
                    param1Node.forAllEdges((Visitor) new Object(this));
                }
            });
            endDigraph();
        } catch (IOException iOException) {
            System.err.println("Can't load file: " + paramArrayOfString[0]);
            System.exit(1);
        }
    }

    public static void startDigraph(String paramString) {
        if (paramString.lastIndexOf('/') != -1)
            paramString = paramString.substring(paramString.lastIndexOf('/') + 1);
        paramString = paramString.replace('.', '_');
        paramString = paramString.replace('-', '_');
        System.out.println("digraph " + paramString + " {");
    }

    public static void endDigraph() {
        System.out.println("}");
    }

    public static void printInit(Node paramNode) {
        System.out.println("\tinit [color=white, label=\"\"];");
        System.out.println("\tinit -> " + paramNode.getId() + ";");
    }

    public static void printNode(Node paramNode) {
        int i = paramNode.getId();
        if (paramNode.getBooleanAttribute("accepting")) {
            System.out.println("\t" + i + " [shape=doublecircle];");
        } else {
            System.out.println("\t" + i + " [shape=circle];");
        }
        String str = paramNode.getStringAttribute("label");
        StringBuffer stringBuffer = new StringBuffer();
        if (str != null) {
            stringBuffer.append(str);
            stringBuffer.append("\\n");
        }
        stringBuffer.append(i + "\\n");
        int j = paramNode.getGraph().getIntAttribute("nsets");
        boolean bool = true;
        for (byte b = 0; b < j; b++) {
            if (paramNode.getBooleanAttribute("acc" + b)) {
                if (bool) {
                    stringBuffer.append("{");
                    bool = false;
                } else {
                    stringBuffer.append(",");
                }
                stringBuffer.append(b);
            }
        }
        if (!bool)
            stringBuffer.append("}");
        System.out.println("\t" + i + " [label=\"" + stringBuffer.toString() + "\"];");
    }

    public static void printEdge(Edge paramEdge) {
        int i = paramEdge.getSource().getId();
        int j = paramEdge.getNext().getId();
        String str1 = paramEdge.getGuard();
        String str2 = paramEdge.getAction();
        String str3 = paramEdge.getStringAttribute("label");
        StringBuffer stringBuffer = new StringBuffer();
        if (str3 != null) {
            stringBuffer.append(str3);
            stringBuffer.append("\\n");
        }
        if (!str1.equals("-")) {
            if (!str2.equals("-")) {
                stringBuffer.append(str1 + "/" + str2 + "\\n");
            } else {
                stringBuffer.append(str1 + "\\n");
            }
        } else if (!str2.equals("-")) {
            stringBuffer.append(str1 + "/" + str2 + "\\n");
        } else {
            stringBuffer.append("true\\n");
        }
        int k = paramEdge.getSource().getGraph().getIntAttribute("nsets");
        boolean bool = true;
        for (byte b = 0; b < k; b++) {
            if (paramEdge.getBooleanAttribute("acc" + b)) {
                if (bool) {
                    stringBuffer.append("{");
                    bool = false;
                } else {
                    stringBuffer.append(",");
                }
                stringBuffer.append(b);
            }
        }
        if (!bool)
            stringBuffer.append("}");
        System.out.println("\t" + i + " -> " + j + " [label=\"" + stringBuffer.toString() + "\"]");
    }
}
