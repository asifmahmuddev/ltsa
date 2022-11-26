package ic.doc.ltsa.lts.ltl;

import gov.nasa.arc.ase.util.graph.Edge;
import gov.nasa.arc.ase.util.graph.Graph;
import gov.nasa.arc.ase.util.graph.Node;
import ic.doc.ltsa.lts.CompactState;
import ic.doc.ltsa.lts.Diagnostics;
import ic.doc.ltsa.lts.EventState;
import java.io.PrintStream;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;

class Converter extends CompactState {
    BitSet accepting;
    Graph g;

    Converter(String paramString, Graph paramGraph, LabelFactory paramLabelFactory) {
        this.name = paramString;
        this.g = paramGraph;
        this.accepting = getAcceptance();
        this.alphabet = paramLabelFactory.makeAlphabet();
        makeStates(paramLabelFactory);
    }

    private void makeStates(LabelFactory paramLabelFactory) {
        this.maxStates = this.g.getNodeCount() + 1;
        this.states = new EventState[this.maxStates];
        HashMap hashMap = paramLabelFactory.getTransLabels();
        addTrueNode(this.maxStates - 1, hashMap);
        Iterator iterator = this.g.getNodes().iterator();
        while (iterator.hasNext())
            addNode(iterator.next(), hashMap);
        reachable();
    }

    void addNode(Node paramNode, HashMap paramHashMap) {
        int i = paramNode.getId();
        if (this.accepting.get(i))
            this.states[i] = EventState.add(this.states[i], new EventState(this.alphabet.length - 1, i));
        BitSet bitSet = new BitSet(this.alphabet.length - 2);
        Iterator iterator = paramNode.getOutgoingEdges().iterator();
        while (iterator.hasNext())
            addEdge(iterator.next(), i, paramHashMap, bitSet);
        complete(i, bitSet);
    }

    void addTrueNode(int paramInt, HashMap paramHashMap) {
        BitSet bitSet = (BitSet) paramHashMap.get("true");
        for (byte b = 0; b < bitSet.size(); b++) {
            if (bitSet.get(b))
                this.states[paramInt] = EventState.add(this.states[paramInt], new EventState(b + 1, paramInt));
        }
    }

    void complete(int paramInt, BitSet paramBitSet) {
        for (byte b = 0; b < this.alphabet.length - 2; b++) {
            if (!paramBitSet.get(b))
                this.states[paramInt] = EventState.add(this.states[paramInt], new EventState(b + 1, this.maxStates - 1));
        }
    }

    void addEdge(Edge paramEdge, int paramInt, HashMap paramHashMap, BitSet paramBitSet) {
        String str;
        if (paramEdge.getGuard().equals("-")) {
            str = "true";
        } else {
            str = paramEdge.getGuard();
        }
        BitSet bitSet = (BitSet) paramHashMap.get(str);
        paramBitSet.or(bitSet);
        for (byte b = 0; b < bitSet.size(); b++) {
            if (bitSet.get(b))
                this.states[paramInt] = EventState.add(this.states[paramInt], new EventState(b + 1, paramEdge.getNext().getId()));
        }
    }

    public void printFSP(PrintStream paramPrintStream) {
        boolean bool = false;
        if (this.g.getInit() != null) {
            paramPrintStream.print(this.name + " = S" + this.g.getInit().getId());
        } else {
            paramPrintStream.print("Empty");
            bool = true;
        }
        for (Iterator iterator = this.g.getNodes().iterator(); iterator.hasNext(); printNode(node, paramPrintStream)) {
            paramPrintStream.println(",");
            Node node = iterator.next();
        }
        paramPrintStream.println(".");
        if (paramPrintStream != System.out)
            paramPrintStream.close();
    }

    protected BitSet getAcceptance() {
        BitSet bitSet = new BitSet();
        int i = this.g.getIntAttribute("nsets");
        if (i > 0)
            Diagnostics.fatal("More than one acceptance set");
        for (Node node : this.g.getNodes()) {
            if (node.getBooleanAttribute("accepting"))
                bitSet.set(node.getId());
        }
        return bitSet;
    }

    void printNode(Node paramNode, PrintStream paramPrintStream) {
        String str = this.accepting.get(paramNode.getId()) ? "@" : "";
        paramPrintStream.print("S" + paramNode.getId() + str + " =(");
        for (Iterator iterator = paramNode.getOutgoingEdges().iterator(); iterator.hasNext();) {
            printEdge(iterator.next(), paramPrintStream);
            if (iterator.hasNext())
                paramPrintStream.print(" |");
        }
        paramPrintStream.print(")");
    }

    void printEdge(Edge paramEdge, PrintStream paramPrintStream) {
        String str2, str1 = "";
        if (paramEdge.getGuard().equals("-")) {
            str2 = "true";
        } else {
            str2 = paramEdge.getGuard();
        }
        paramPrintStream.print(str2 + " -> S" + paramEdge.getNext().getId());
    }
}
