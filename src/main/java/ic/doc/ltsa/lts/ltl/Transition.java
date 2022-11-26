package ic.doc.ltsa.lts.ltl;

import gov.nasa.arc.ase.util.graph.Edge;
import gov.nasa.arc.ase.util.graph.Node;
import ic.doc.ltsa.lts.LTSOutput;
import java.util.BitSet;
import java.util.SortedSet;

class Transition {
    SortedSet propositions;
    int pointsTo;
    BitSet accepting;
    boolean safe_acc;
    static LabelFactory lf;

    static void setLabelFactory(LabelFactory paramLabelFactory) {
        lf = paramLabelFactory;
    }

    Transition(SortedSet paramSortedSet, int paramInt, BitSet paramBitSet, boolean paramBoolean) {
        this.propositions = paramSortedSet;
        this.pointsTo = paramInt;
        this.accepting = new BitSet();
        this.accepting.or(paramBitSet);
        this.safe_acc = paramBoolean;
    }

    int goesTo() {
        return this.pointsTo;
    }

    BitSet computeAccepting(int paramInt) {
        BitSet bitSet = new BitSet(paramInt);
        for (byte b = 0; b < paramInt; b++) {
            if (!this.accepting.get(b))
                bitSet.set(b);
        }
        return bitSet;
    }

    void print(LTSOutput paramLTSOutput, int paramInt) {
        if (this.propositions.isEmpty()) {
            paramLTSOutput.out("LABEL True");
        } else {
            Node.printFormulaSet(paramLTSOutput, "LABEL", this.propositions);
        }
        paramLTSOutput.out(" T0 " + goesTo());
        if (paramInt > 0) {
            paramLTSOutput.outln(" Acc " + computeAccepting(paramInt));
        } else if (this.safe_acc) {
            paramLTSOutput.outln(" Acc {0}");
        } else {
            paramLTSOutput.outln("");
        }
    }

    void Gmake(Node[] paramArrayOfNode, Node paramNode, int paramInt) {
        String str1 = "-";
        String str2 = "-";
        if (!this.propositions.isEmpty())
            str1 = lf.makeLabel(this.propositions);
        Edge edge = new Edge(paramNode, paramArrayOfNode[this.pointsTo], str1, str2);
        if (paramInt == 0) {
            if (this.safe_acc)
                edge.setBooleanAttribute("acc0", true);
        } else {
            for (byte b = 0; b < paramInt; b++) {
                if (!this.accepting.get(b))
                    edge.setBooleanAttribute("acc" + b, true);
            }
        }
    }
}
