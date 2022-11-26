package gov.nasa.arc.ase.util.graph;

public class Generate {
    public static Graph generate(int paramInt) {
        int i = paramInt + 1;
        Node[] arrayOfNode = new Node[i];
        Graph graph = new Graph();
        graph.setIntAttribute("nsets", paramInt);
        graph.setStringAttribute("type", "ba");
        graph.setStringAttribute("ac", "nodes");
        for (byte b1 = 0; b1 < i; b1++) {
            arrayOfNode[b1] = new Node(graph);
            StringBuffer stringBuffer = new StringBuffer();
            for (byte b = 0; b < b1; b++)
                stringBuffer.append("acc" + b + "+");
            arrayOfNode[b1].setStringAttribute("label", stringBuffer.toString());
        }
        for (byte b2 = 0; b2 < paramInt; b2++) {
            Node node1 = arrayOfNode[b2];
            for (int k = paramInt; k > b2; k--) {
                Edge edge2 = new Edge(arrayOfNode[b2], arrayOfNode[k], "-", "-", null);
                for (byte b = b2; b < k; b++)
                    edge2.setBooleanAttribute("acc" + b, true);
            }
            Edge edge1 = new Edge(arrayOfNode[b2], arrayOfNode[b2], "-", "-", null);
            edge1.setBooleanAttribute("else", true);
        }
        Node node = arrayOfNode[i - 1];
        node.setBooleanAttribute("accepting", true);
        Edge edge = new Edge(node, node, "-", "-", null);
        for (byte b3 = 0; b3 < paramInt; b3++)
            edge.setBooleanAttribute("acc" + b3, true);
        for (int j = paramInt - 1; j >= 0; j--) {
            edge = new Edge(node, arrayOfNode[j], "-", "-", null);
            if (j == 0) {
                edge.setBooleanAttribute("else", true);
            } else {
                for (byte b = 0; b < j; b++)
                    edge.setBooleanAttribute("acc" + b, true);
            }
        }
        graph.setInit(node);
        return graph;
    }

    public static void main(String[] paramArrayOfString) {
        Graph graph = generate(5);
        graph.save(1);
    }
}
