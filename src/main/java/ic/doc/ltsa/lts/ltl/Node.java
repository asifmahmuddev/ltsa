package ic.doc.ltsa.lts.ltl;

import ic.doc.ltsa.lts.LTSOutput;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class Node implements Comparable {
    int id = 0;
    int equivId = -1;
    SortedSet incoming;
    SortedSet oldf;
    SortedSet newf;
    SortedSet next;
    BitSet accepting;
    BitSet rightOfU;
    static FormulaFactory fac;
    static GeneralizedBuchiAutomata aut;
    private Node otherSource;

    static void setAut(GeneralizedBuchiAutomata paramGeneralizedBuchiAutomata) {
        aut = paramGeneralizedBuchiAutomata;
    }

    static void setFactory(FormulaFactory paramFormulaFactory) {
        fac = paramFormulaFactory;
    }

    public Node() {
        this(null, null, null, null, null, null);
    }

    public Node(Formula paramFormula) {
        this();
        collapsed = false;
        if (!(paramFormula instanceof True))
            decomposeAndforNext(paramFormula);
    }

    public int compareTo(Object paramObject) {
        return this.id - ((Node) paramObject).id;
    }

    public void decomposeAndforNext(Formula paramFormula) {
        if (paramFormula instanceof And) {
            decomposeAndforNext(((And) paramFormula).getLeft());
            decomposeAndforNext(((And) paramFormula).getRight());
        } else if (!isRedundant(this.next, null, paramFormula)) {
            this.next.add(paramFormula);
        }
    }

    private boolean isRedundant(SortedSet paramSortedSet1, SortedSet paramSortedSet2, Formula paramFormula) {
        return (fac.specialCaseV(paramFormula, paramSortedSet1) || (fac.syntaxImplied(paramFormula, paramSortedSet1, paramSortedSet2)
            && (!(paramFormula instanceof Until) || fac.syntaxImplied(paramFormula.getSub2(), paramSortedSet1, paramSortedSet2))));
    }

    private Node split(Formula paramFormula) {
        Node node = new Node(this.incoming, this.oldf, this.newf, this.next, this.accepting, this.rightOfU);
        Formula formula = paramFormula.getSub2();
        if (!this.oldf.contains(formula))
            node.newf.add(formula);
        if (paramFormula instanceof Release) {
            formula = paramFormula.getSub1();
            if (!this.oldf.contains(formula))
                node.newf.add(formula);
        }
        formula = paramFormula.getSub1();
        if (!this.oldf.contains(formula))
            this.newf.add(formula);
        formula = (paramFormula instanceof Until || paramFormula instanceof Release) ? paramFormula : null;
        if (formula != null)
            decomposeAndforNext(formula);
        if (paramFormula instanceof Until) {
            this.accepting.set(paramFormula.getUI());
            node.accepting.set(paramFormula.getUI());
        }
        if (paramFormula.isRightOfUntil()) {
            this.rightOfU.set(paramFormula.getRofUI());
            node.rightOfU.set(paramFormula.getRofUI());
        }
        if (paramFormula.isLiteral()) {
            this.oldf.add(paramFormula);
            node.oldf.add(paramFormula);
        }
        return node;
    }

    public List expand(List paramList) {
        if (this.newf.isEmpty()) {
            if (this.id != 0)
                this.accepting.andNot(this.rightOfU);
            Node node1 = alreadyThere(paramList);
            if (node1 != null) {
                node1.modify(this);
                return paramList;
            }
            Node node2 = new Node();
            node2.incoming.add(this);
            node2.newf.addAll(this.next);
            paramList.add(this);
            return node2.expand(paramList);
        }
        Formula formula = this.newf.first();
        this.newf.remove(formula);
        if (contradiction(formula))
            return paramList;
        TreeSet treeSet = new TreeSet();
        treeSet.addAll(this.oldf);
        treeSet.addAll(this.newf);
        if (isRedundant(treeSet, this.next, formula))
            return expand(paramList);
        if (!formula.isLiteral()) {
            if (formula instanceof Or || formula instanceof Until || formula instanceof Release) {
                Node node = split(formula);
                return node.expand(expand(paramList));
            }
            if (formula instanceof And) {
                Formula formula1 = formula.getSub1();
                if (!this.oldf.contains(formula1))
                    this.newf.add(formula1);
                formula1 = formula.getSub2();
                if (!this.oldf.contains(formula1))
                    this.newf.add(formula1);
                if (formula.isRightOfUntil())
                    this.rightOfU.set(formula.getRofUI());
                return expand(paramList);
            }
            if (formula instanceof Next) {
                decomposeAndforNext(formula.getSub1());
                if (formula.isRightOfUntil())
                    this.rightOfU.set(formula.getRofUI());
                return expand(paramList);
            }
        }
        if (!(formula instanceof True))
            this.oldf.add(formula);
        if (formula.isRightOfUntil())
            this.rightOfU.set(formula.getRofUI());
        return expand(paramList);
    }

    private boolean contradiction(Formula paramFormula) {
        return fac.syntaxImplied(fac.makeNot(paramFormula), this.oldf, this.next);
    }

    private Node alreadyThere(List paramList) {
        Iterator iterator = paramList.iterator();
        while (iterator.hasNext()) {
            Node node = iterator.next();
            if (this.next.equals(node.next) && compareAccepting(node))
                return node;
        }
        return null;
    }

    private boolean compareAccepting(Node paramNode) {
        if (this.id == 0 && !collapsed)
            return true;
        return this.accepting.equals(paramNode.accepting);
    }

    static void printFormulaSet(LTSOutput paramLTSOutput, String paramString, SortedSet paramSortedSet) {
        paramLTSOutput.out(paramString + ":- ");
        Iterator iterator = paramSortedSet.iterator();
        while (iterator.hasNext()) {
            Formula formula = iterator.next();
            paramLTSOutput.out(formula.toString() + ", ");
        }
    }

    static void printIdSet(LTSOutput paramLTSOutput, String paramString, SortedSet paramSortedSet) {
        paramLTSOutput.out(paramString + ":- ");
        Iterator iterator = paramSortedSet.iterator();
        while (iterator.hasNext()) {
            Node node = iterator.next();
            paramLTSOutput.out(node.id + ", ");
        }
        paramLTSOutput.outln(".");
    }

    void printNode(LTSOutput paramLTSOutput) {
        paramLTSOutput.outln("\nNODE " + this.id + " equivId " + this.equivId);
        printIdSet(paramLTSOutput, "INCOMING", this.incoming);
        printFormulaSet(paramLTSOutput, "NEW", this.newf);
        paramLTSOutput.outln(".");
        printFormulaSet(paramLTSOutput, "OLD", this.oldf);
        paramLTSOutput.outln(".");
        printFormulaSet(paramLTSOutput, "NEXT", this.next);
        paramLTSOutput.outln(".");
        paramLTSOutput.outln("ACCEPTING:- " + this.accepting);
        paramLTSOutput.outln("RIGHTOFU:- " + this.rightOfU);
        if (this.otherSource != null) {
            paramLTSOutput.outln("OTHERSOURCE " + this.otherSource.id + " ************** ");
            Node node = this.otherSource;
            while (node != null) {
                node.printNode(paramLTSOutput);
                node = node.otherSource;
                if (node == this)
                    break;
            }
        }
    }

    public Node(SortedSet paramSortedSet1, SortedSet paramSortedSet2, SortedSet paramSortedSet3, SortedSet paramSortedSet4, BitSet paramBitSet1, BitSet paramBitSet2) {
        this.otherSource = null;
        this.id = aut.newId();
        this.incoming = (paramSortedSet1 != null) ? new TreeSet(paramSortedSet1) : new TreeSet();
        this.oldf = (paramSortedSet2 != null) ? new TreeSet(paramSortedSet2) : new TreeSet();
        this.newf = (paramSortedSet3 != null) ? new TreeSet(paramSortedSet3) : new TreeSet();
        this.next = (paramSortedSet4 != null) ? new TreeSet(paramSortedSet4) : new TreeSet();
        this.accepting = new BitSet();
        if (paramBitSet1 != null)
            this.accepting.or(paramBitSet1);
        this.rightOfU = new BitSet();
        if (paramBitSet2 != null)
            this.rightOfU.or(paramBitSet2);
    }

    private static boolean collapsed = false;

    private void modify(Node paramNode) {
        boolean bool = false;
        Node node1 = this;
        Node node2 = this;
        if (this.id == 0 && !collapsed) {
            this.accepting = paramNode.accepting;
            collapsed = true;
        }
        for (; node2 != null; node2 = node2.otherSource) {
            if (node2.oldf.equals(paramNode.oldf)) {
                node2.incoming.addAll(paramNode.incoming);
                bool = true;
            }
            node1 = node2;
        }
        if (!bool)
            node1.otherSource = paramNode;
    }

    private boolean isSafetyAcc() {
        if (this.next.isEmpty())
            return true;
        Iterator iterator = this.next.iterator();
        while (iterator.hasNext()) {
            Formula formula = iterator.next();
            if (!(formula instanceof Release))
                return false;
        }
        return true;
    }

    public void makeTransitions(State[] paramArrayOfState) {
        boolean bool = false;
        if (paramArrayOfState[this.id] == null) {
            paramArrayOfState[this.id] = new State(this.equivId);
        } else {
            paramArrayOfState[this.id].setId(this.equivId);
        }
        boolean bool1 = isSafetyAcc();
        for (Node node = this; node != null; node = node.otherSource) {
            Iterator iterator = node.incoming.iterator();
            while (iterator.hasNext()) {
                Node node1 = iterator.next();
                int i = node1.id;
                if (paramArrayOfState[i] == null)
                    paramArrayOfState[i] = new State();
                paramArrayOfState[i].add(new Transition(node.oldf, this.equivId, this.accepting, bool1));
            }
        }
    }
}
