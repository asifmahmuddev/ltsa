package ic.doc.ltsa.lts.ltl;

import gov.nasa.arc.ase.util.graph.Graph;
import gov.nasa.arc.ase.util.graph.Node;
import ic.doc.ltsa.lts.Diagnostics;
import ic.doc.ltsa.lts.LTSOutput;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GeneralizedBuchiAutomata {
    List nodes;
    Formula formula;
    FormulaFactory fac;
    List untils;
    int maxId = -1;
    Node[] equivClasses;
    State[] states;
    int naccept;
    String name;
    LabelFactory labelFac;

    public GeneralizedBuchiAutomata(String paramString, FormulaFactory paramFormulaFactory) {
        this.fac = paramFormulaFactory;
        this.name = paramString;
        this.formula = paramFormulaFactory.getFormula();
        this.nodes = new ArrayList();
        this.labelFac = new LabelFactory(this.name, this.fac);
    }

    public void translate() {
        Node.setAut(this);
        Node.setFactory(this.fac);
        Transition.setLabelFactory(this.labelFac);
        this.naccept = this.fac.processUntils(this.formula, this.untils = new ArrayList());
        Node node = new Node(this.formula);
        this.nodes = node.expand(this.nodes);
        this.states = makeStates();
    }

    public LabelFactory getLabelFactory() {
        return this.labelFac;
    }

    public void printNodes(LTSOutput paramLTSOutput) {
        for (byte b = 0; b < this.states.length; b++) {
            if (this.states[b] != null && b == this.states[b].getId())
                this.states[b].print(paramLTSOutput, this.naccept);
        }
    }

    public int indexEquivalence(Node paramNode) {
        byte b;
        for (b = 0; b < this.maxId && this.equivClasses[b] != null; b++) {
            if ((this.equivClasses[b]).next.equals(paramNode.next))
                return (this.equivClasses[b]).id;
        }
        if (b == this.maxId)
            Diagnostics.fatal("size of equivalence classes array was incorrect");
        this.equivClasses[b] = paramNode;
        return (this.equivClasses[b]).id;
    }

    public State[] makeStates() {
        State[] arrayOfState = new State[this.maxId];
        this.equivClasses = new Node[this.maxId];
        Iterator iterator = this.nodes.iterator();
        while (iterator.hasNext()) {
            Node node = iterator.next();
            node.equivId = indexEquivalence(node);
            node.makeTransitions(arrayOfState);
        }
        return arrayOfState;
    }

    int newId() {
        return ++this.maxId;
    }

    Graph Gmake() {
        Graph graph = new Graph();
        graph.setStringAttribute("type", "gba");
        graph.setStringAttribute("ac", "edges");
        if (this.states == null)
            return graph;
        int i = this.maxId;
        Node[] arrayOfNode = new Node[i];
        for (byte b1 = 0; b1 < i; b1++) {
            if (this.states[b1] != null && b1 == this.states[b1].getId()) {
                arrayOfNode[b1] = new Node(graph);
                arrayOfNode[b1].setStringAttribute("label", "S" + this.states[b1].getId());
            }
        }
        for (byte b2 = 0; b2 < i; b2++) {
            if (this.states[b2] != null && b2 == this.states[b2].getId())
                this.states[b2].Gmake(arrayOfNode, arrayOfNode[b2], this.naccept);
        }
        if (this.naccept == 0) {
            graph.setIntAttribute("nsets", 1);
        } else {
            graph.setIntAttribute("nsets", this.naccept);
        }
        return graph;
    }
}
