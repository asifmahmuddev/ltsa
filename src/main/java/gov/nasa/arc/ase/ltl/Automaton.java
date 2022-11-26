package gov.nasa.arc.ase.ltl;

import gov.nasa.arc.ase.util.graph.Graph;
import gov.nasa.arc.ase.util.graph.Node;

class Automaton {
    private LinkNode head = this.tail = null;
    private LinkNode tail;
    private Node[] equivalence_classes = null;

    public void add(Node paramNode) {
        LinkNode linkNode = new LinkNode(paramNode, null);
        if (this.head == null) {
            this.head = this.tail = linkNode;
        } else {
            this.tail.LinkWith(linkNode);
            this.tail = linkNode;
        }
    }

    public Node alreadyThere(Node paramNode) {
        LinkNode linkNode = this.head;
        while (linkNode != null) {
            Node node = linkNode.getNode();
            if (node.getField_next().equals(paramNode.getField_next()) && node.compare_accepting(paramNode))
                return node;
            linkNode = linkNode.getNext();
        }
        return null;
    }

    public int index_equivalence(Node paramNode) {
        byte b;
        for (b = 0; b < Pool.assign(); b++) {
            if (this.equivalence_classes[b] == null)
                break;
            if (this.equivalence_classes[b].getField_next().equals(paramNode.getField_next()))
                return this.equivalence_classes[b].getNodeId();
        }
        if (b == Pool.assign())
            System.out.println("ERROR - size of equivalence classes array was incorrect");
        this.equivalence_classes[b] = paramNode;
        return this.equivalence_classes[b].getNodeId();
    }

    public State[] structForRuntAnalysis() {
        Pool.stop();
        int i = Pool.assign();
        State[] arrayOfState = new State[i];
        this.equivalence_classes = new Node[i];
        if (this.head == null)
            return arrayOfState;
        LinkNode linkNode = this.head;
        while (linkNode != null) {
            Node node = linkNode.getNode();
            node.set_equivalenceId(index_equivalence(node));
            linkNode.getNode().RTstructure(arrayOfState);
            linkNode = linkNode.getNext();
        }
        return arrayOfState;
    }

    public static void FSPoutput(State[] paramArrayOfState) {
        boolean bool = false;
        if (paramArrayOfState == null) {
            System.out.println("\n\nRES = STOP.");
            return;
        }
        System.out.println("\n\nRES = S0,");
        int i = Pool.assign();
        for (byte b = 0; b < i; b++) {
            if (paramArrayOfState[b] != null && b == paramArrayOfState[b].get_representativeId()) {
                if (bool)
                    System.out.println("),");
                bool = true;
                System.out.print("S" + paramArrayOfState[b].get_representativeId());
                System.out.print("=");
                paramArrayOfState[b].FSPoutput();
            }
        }
        System.out.println(").\n");
    }

    public static Graph SMoutput(State[] paramArrayOfState) {
        Graph graph = new Graph();
        graph.setStringAttribute("type", "gba");
        graph.setStringAttribute("ac", "edges");
        if (paramArrayOfState == null)
            return graph;
        int i = Pool.assign();
        Node[] arrayOfNode = new Node[i];
        for (byte b1 = 0; b1 < i; b1++) {
            if (paramArrayOfState[b1] != null && b1 == paramArrayOfState[b1].get_representativeId()) {
                arrayOfNode[b1] = new Node(graph);
                arrayOfNode[b1].setStringAttribute("label", "S" + paramArrayOfState[b1].get_representativeId());
            }
        }
        for (byte b2 = 0; b2 < i; b2++) {
            if (paramArrayOfState[b2] != null && b2 == paramArrayOfState[b2].get_representativeId())
                paramArrayOfState[b2].SMoutput(arrayOfNode, arrayOfNode[b2]);
        }
        if (Node.accepting_conds == 0) {
            graph.setIntAttribute("nsets", 1);
        } else {
            graph.setIntAttribute("nsets", Node.accepting_conds);
        }
        return graph;
    }
}
