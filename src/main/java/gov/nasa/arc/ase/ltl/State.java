package gov.nasa.arc.ase.ltl;

import gov.nasa.arc.ase.util.graph.Node;
import java.util.BitSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.TreeSet;

class State implements Comparable {
    private int representativeId = -1;
    private LinkedList transitions;
    private BitSet accepting;
    private boolean safety_acceptance;

    public State(BitSet paramBitSet) {
        this.transitions = new LinkedList();
        this.accepting = paramBitSet;
        this.safety_acceptance = false;
    }

    public State(BitSet paramBitSet, int paramInt) {
        this.transitions = new LinkedList();
        this.accepting = paramBitSet;
        this.safety_acceptance = false;
        this.representativeId = paramInt;
    }

    public State() {
        this.transitions = new LinkedList();
        this.accepting = null;
        this.safety_acceptance = false;
    }

    public State(int paramInt) {
        this.transitions = new LinkedList();
        this.accepting = null;
        this.safety_acceptance = false;
        this.representativeId = paramInt;
    }

    public void set_representativeId(int paramInt) {
        this.representativeId = paramInt;
    }

    public int get_representativeId() {
        return this.representativeId;
    }

    public void update_safety_acc(boolean paramBoolean) {
        this.safety_acceptance = paramBoolean;
    }

    public boolean is_safe() {
        return this.safety_acceptance;
    }

    public boolean accepts(int paramInt) {
        return !this.accepting.get(paramInt);
    }

    public int compareTo(Object paramObject) {
        if (this == paramObject)
            return 0;
        return 1;
    }

    public void update_acc(BitSet paramBitSet) {
        this.accepting = paramBitSet;
    }

    public void update_acc(BitSet paramBitSet, int paramInt) {
        this.accepting = paramBitSet;
        this.representativeId = paramInt;
    }

    public void add(Transition paramTransition) {
        this.transitions.add(paramTransition);
    }

    public void step(Hashtable paramHashtable, TreeSet paramTreeSet, State[] paramArrayOfState) {
        ListIterator listIterator = this.transitions.listIterator(0);
        while (listIterator.hasNext()) {
            Transition transition = listIterator.next();
            if (transition.enabled(paramHashtable))
                paramTreeSet.add(paramArrayOfState[transition.goesTo()]);
        }
    }

    public void FSPoutput() {
        ListIterator listIterator = this.transitions.listIterator(0);
        boolean bool = true;
        while (listIterator.hasNext()) {
            Transition transition = listIterator.next();
            if (bool) {
                System.out.print("(");
                bool = false;
            } else {
                System.out.print("|");
            }
            transition.FSPoutput();
        }
    }

    public void SMoutput(Node[] paramArrayOfNode, Node paramNode) {
        ListIterator listIterator = this.transitions.listIterator(0);
        boolean bool = true;
        while (listIterator.hasNext()) {
            Transition transition = listIterator.next();
            transition.SMoutput(paramArrayOfNode, paramNode);
        }
    }
}
