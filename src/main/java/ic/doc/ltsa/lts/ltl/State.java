package ic.doc.ltsa.lts.ltl;

import gov.nasa.arc.ase.util.graph.Node;
import ic.doc.ltsa.lts.LTSOutput;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

class State implements Comparable {
    private List transitions;
    private int stateId;

    State(List paramList, int paramInt) {
        this.transitions = paramList;
        this.stateId = paramInt;
    }

    State() {
        this(new LinkedList(), -1);
    }

    State(int paramInt) {
        this(new LinkedList(), paramInt);
    }

    void setId(int paramInt) {
        this.stateId = paramInt;
    }

    int getId() {
        return this.stateId;
    }

    public int compareTo(Object paramObject) {
        return (this != paramObject) ? 1 : 0;
    }

    public void add(Transition paramTransition) {
        this.transitions.add(paramTransition);
    }

    void print(LTSOutput paramLTSOutput, int paramInt) {
        paramLTSOutput.outln("STATE " + this.stateId);
        Iterator iterator = this.transitions.iterator();
        while (iterator.hasNext())
            ((Transition) iterator.next()).print(paramLTSOutput, paramInt);
    }

    void Gmake(Node[] paramArrayOfNode, Node paramNode, int paramInt) {
        ListIterator listIterator = this.transitions.listIterator(0);
        boolean bool = true;
        while (listIterator.hasNext()) {
            Transition transition = (Transition) listIterator.next();
            transition.Gmake(paramArrayOfNode, paramNode, paramInt);
        }
    }
}
