package gov.nasa.arc.ase.ltl;

import gov.nasa.arc.ase.util.graph.Edge;
import gov.nasa.arc.ase.util.graph.Node;
import java.util.BitSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeSet;

class Transition {
    private TreeSet propositions;
    private int pointsTo;
    private BitSet accepting;
    private boolean safe_accepting;

    public Transition(TreeSet paramTreeSet, int paramInt, BitSet paramBitSet, boolean paramBoolean) {
        this.propositions = paramTreeSet;
        this.pointsTo = paramInt;
        this.accepting = new BitSet(Node.getAcceptingConds());
        this.accepting.or(paramBitSet);
        this.safe_accepting = paramBoolean;
    }

    public int goesTo() {
        return this.pointsTo;
    }

    public boolean enabled(Hashtable paramHashtable) {
        Iterator iterator = this.propositions.iterator();
        Formula formula = null;
        while (iterator.hasNext()) {
            Boolean bool;
            formula = iterator.next();
            char c;
            switch (c = formula.getContent()) {
                case 'N' :
                    bool = (Boolean) paramHashtable.get(formula.getSub1().getName());
                    if (bool == null)
                        return false;
                    if (bool.booleanValue())
                        return false;
                    break;
                case 't' :
                    break;
                case 'p' :
                    bool = (Boolean) paramHashtable.get(formula.getName());
                    if (bool == null)
                        return false;
                    if (!bool.booleanValue())
                        return false;
                    break;
            }
        }
        return true;
    }

    public void FSPoutput() {
        if (this.propositions.isEmpty()) {
            System.out.print("TRUE{");
        } else {
            Iterator iterator = this.propositions.iterator();
            Formula formula = null;
            StringBuffer stringBuffer = new StringBuffer();
            boolean bool = false;
            while (iterator.hasNext()) {
                formula = iterator.next();
                char c = formula.getContent();
                if (bool)
                    stringBuffer.append("_AND_");
                bool = true;
                switch (c) {
                    case 'N' :
                        stringBuffer.append('N');
                        stringBuffer.append(formula.getSub1().getName());
                        continue;
                    case 't' :
                        stringBuffer.append("TRUE");
                        continue;
                }
                stringBuffer.append(formula.getName());
            }
            System.out.print(stringBuffer + "{");
        }
        if (Node.accepting_conds == 0) {
            if (this.safe_accepting == true)
                System.out.print("0");
        } else {
            for (byte b = 0; b < Node.accepting_conds; b++) {
                if (!this.accepting.get(b))
                    System.out.print(b);
            }
        }
        System.out.print("} -> S" + this.pointsTo + " ");
    }

    public void SMoutput(Node[] paramArrayOfNode, Node paramNode) {
        String str1 = "-";
        String str2 = "-";
        if (!this.propositions.isEmpty()) {
            Iterator iterator = this.propositions.iterator();
            Formula formula = null;
            StringBuffer stringBuffer = new StringBuffer();
            boolean bool = false;
            while (iterator.hasNext()) {
                formula = iterator.next();
                char c = formula.getContent();
                if (bool)
                    stringBuffer.append("&");
                bool = true;
                switch (c) {
                    case 'N' :
                        stringBuffer.append('!');
                        stringBuffer.append(formula.getSub1().getName());
                        continue;
                    case 't' :
                        stringBuffer.append("true");
                        continue;
                }
                stringBuffer.append(formula.getName());
            }
            str1 = stringBuffer.toString();
        }
        Edge edge = new Edge(paramNode, paramArrayOfNode[this.pointsTo], str1, str2);
        if (Node.accepting_conds == 0) {
            if (this.safe_accepting == true)
                edge.setBooleanAttribute("acc0", true);
        } else {
            for (byte b = 0; b < Node.accepting_conds; b++) {
                if (!this.accepting.get(b))
                    edge.setBooleanAttribute("acc" + b, true);
            }
        }
    }
}
