package gov.nasa.arc.ase.ltl;

import java.util.BitSet;
import java.util.Iterator;
import java.util.TreeSet;

class Node implements Comparable {
    private int nodeId;
    private TreeSet incoming;
    private TreeSet toBeDone;
    private TreeSet old;
    private TreeSet next;
    private BitSet accepting;
    private BitSet right_of_untils;
    private Node OtherTransitionSource;
    public static int accepting_conds = 0;
    private static boolean init_collapsed = false;
    private int equivalenceId;

    public Node() {
        this.nodeId = Pool.assign();
        this.incoming = new TreeSet();
        this.toBeDone = new TreeSet();
        this.old = new TreeSet();
        this.next = new TreeSet();
        this.OtherTransitionSource = null;
        this.accepting = new BitSet(accepting_conds);
        this.right_of_untils = new BitSet(accepting_conds);
    }

    public Node(TreeSet paramTreeSet1, TreeSet paramTreeSet2, TreeSet paramTreeSet3, TreeSet paramTreeSet4, BitSet paramBitSet1, BitSet paramBitSet2) {
        this.nodeId = Pool.assign();
        this.incoming = new TreeSet(paramTreeSet1);
        this.toBeDone = new TreeSet(paramTreeSet2);
        this.old = new TreeSet(paramTreeSet3);
        this.next = new TreeSet(paramTreeSet4);
        this.OtherTransitionSource = null;
        this.accepting = new BitSet(accepting_conds);
        this.accepting.or(paramBitSet1);
        this.right_of_untils = new BitSet(accepting_conds);
        this.right_of_untils.or(paramBitSet2);
    }

    public static Node createInitial(Formula paramFormula) {
        accepting_conds = paramFormula.processRightUntils();
        Formula.until_forms = new Formula[accepting_conds];
        Formula.untils.toArray((Object[]) Formula.until_forms);
        Node node = new Node();
        node.nodeId = 0;
        if (paramFormula.getContent() != 't')
            node.decompose_ands_for_next(paramFormula);
        return node;
    }

    public static void reset_static() {
        accepting_conds = 0;
        init_collapsed = false;
    }

    public static int getAcceptingConds() {
        return accepting_conds;
    }

    public void set_equivalenceId(int paramInt) {
        this.equivalenceId = paramInt;
    }

    public int get_equivalenceId() {
        return this.equivalenceId;
    }

    public int getNodeId() {
        return this.nodeId;
    }

    public boolean isInitial() {
        return (this.nodeId == 0);
    }

    public void update_accepting() {
        this.accepting.andNot(this.right_of_untils);
    }

    public boolean compare_accepting(Node paramNode) {
        if (this.nodeId == 0 && !init_collapsed)
            return true;
        return this.accepting.equals(paramNode.accepting);
    }

    public TreeSet getField_old() {
        return this.old;
    }

    public void decompose_ands_for_next(Formula paramFormula) {
        if (paramFormula.getContent() == 'A') {
            decompose_ands_for_next(paramFormula.getSub1());
            decompose_ands_for_next(paramFormula.getSub2());
        } else if (!is_redundant(this.next, null, paramFormula)) {
            this.next.add(paramFormula);
        }
    }

    public TreeSet getField_next() {
        return this.next;
    }

    private Node split(Formula paramFormula) {
        Node node = new Node(this.incoming, this.toBeDone, this.old, this.next, this.accepting, this.right_of_untils);
        Formula formula = paramFormula.getSub2();
        if (!this.old.contains(formula))
            node.toBeDone.add(formula);
        if (paramFormula.getContent() == 'V') {
            formula = paramFormula.getSub1();
            if (!this.old.contains(formula))
                node.toBeDone.add(formula);
        }
        formula = paramFormula.getSub1();
        if (!this.old.contains(formula))
            this.toBeDone.add(formula);
        formula = paramFormula.getNext();
        if (formula != null)
            decompose_ands_for_next(formula);
        if (paramFormula.getContent() == 'U') {
            this.accepting.set(paramFormula.get_untils_index());
            node.accepting.set(paramFormula.get_untils_index());
        }
        if (paramFormula.is_right_of_until()) {
            this.right_of_untils.set(paramFormula.get_rightOfUntils_index());
            node.right_of_untils.set(paramFormula.get_rightOfUntils_index());
        }
        if (paramFormula.is_literal()) {
            this.old.add(paramFormula);
            System.out.println("added " + paramFormula);
            node.old.add(paramFormula);
        }
        return node;
    }

    public void print() {
        System.out.println("\n\nPrinting node " + this.nodeId);
        Iterator iterator = this.next.iterator();
        Formula formula = null;
        while (iterator.hasNext()) {
            formula = iterator.next();
            System.out.println("Formula: " + formula.toString());
        }
    }

    public Automaton expand(Automaton paramAutomaton) {
        if (this.toBeDone.isEmpty()) {
            if (this.nodeId != 0)
                update_accepting();
            Node node1 = paramAutomaton.alreadyThere(this);
            if (node1 != null) {
                node1.modify(this);
                return paramAutomaton;
            }
            Node node2 = new Node();
            node2.incoming.add(this);
            node2.toBeDone.addAll(this.next);
            paramAutomaton.add(this);
            return node2.expand(paramAutomaton);
        }
        Formula formula = this.toBeDone.first();
        this.toBeDone.remove(formula);
        if (testForContradictions(formula))
            return paramAutomaton;
        TreeSet treeSet = new TreeSet();
        treeSet.addAll(this.old);
        treeSet.addAll(this.toBeDone);
        if (is_redundant(treeSet, this.next, formula))
            return expand(paramAutomaton);
        if (!formula.is_literal()) {
            Formula formula1;
            Node node;
            switch (formula.getContent()) {
                case 'O' :
                case 'U' :
                case 'V' :
                case 'W' :
                    node = split(formula);
                    return node.expand(expand(paramAutomaton));
                case 'X' :
                    decompose_ands_for_next(formula.getSub1());
                    if (formula.is_right_of_until())
                        this.right_of_untils.set(formula.get_rightOfUntils_index());
                    return expand(paramAutomaton);
                case 'A' :
                    formula1 = formula.getSub1();
                    if (!this.old.contains(formula1))
                        this.toBeDone.add(formula1);
                    formula1 = formula.getSub2();
                    if (!this.old.contains(formula1))
                        this.toBeDone.add(formula1);
                    if (formula.is_right_of_until())
                        this.right_of_untils.set(formula.get_rightOfUntils_index());
                    return expand(paramAutomaton);
            }
            System.out.println("default case of switch entered");
            return null;
        }
        if (formula.getContent() != 't')
            this.old.add(formula);
        if (formula.is_right_of_until())
            this.right_of_untils.set(formula.get_rightOfUntils_index());
        return expand(paramAutomaton);
    }

    private static boolean is_redundant(TreeSet paramTreeSet1, TreeSet paramTreeSet2, Formula paramFormula) {
        if (paramFormula.is_special_case_of_V(paramTreeSet1)
            || (paramFormula.is_synt_implied(paramTreeSet1, paramTreeSet2) && (paramFormula.getContent() != 'U' || paramFormula.getSub2().is_synt_implied(paramTreeSet1, paramTreeSet2))))
            return true;
        return false;
    }

    private void modify(Node paramNode) {
        boolean bool = false;
        Node node1 = this, node2 = this;
        if (this.nodeId == 0 && !init_collapsed) {
            this.accepting = paramNode.accepting;
            init_collapsed = true;
        }
        while (node2 != null) {
            if (node2.old.equals(paramNode.old)) {
                node2.incoming.addAll(paramNode.incoming);
                bool = true;
            }
            node1 = node2;
            node2 = node2.OtherTransitionSource;
        }
        if (!bool)
            node1.OtherTransitionSource = paramNode;
    }

    public int compareTo(Object paramObject) {
        if (this == paramObject)
            return 0;
        return 1;
    }

    private boolean testForContradictions(Formula paramFormula) {
        Formula formula = paramFormula.negate();
        if (formula.is_synt_implied(this.old, this.next))
            return true;
        return false;
    }

    public int getId() {
        return this.nodeId;
    }

    public void debug() {
        Iterator iterator = this.old.iterator();
        Formula formula = null;
        while (iterator.hasNext())
            formula = iterator.next();
    }

    private boolean is_safety_acc_node() {
        if (this.next.isEmpty())
            return true;
        Iterator iterator = this.next.iterator();
        Formula formula = null;
        while (iterator.hasNext()) {
            formula = iterator.next();
            if (formula.getContent() != 'V' && formula.getContent() != 'W')
                return false;
        }
        return true;
    }

    public void RTstructure(State[] paramArrayOfState) {
        boolean bool = false;
        if (paramArrayOfState[this.nodeId] == null) {
            paramArrayOfState[this.nodeId] = new State(this.accepting, this.equivalenceId);
        } else {
            paramArrayOfState[this.nodeId].update_acc(this.accepting, this.equivalenceId);
        }
        if (is_safety_acc_node()) {
            paramArrayOfState[this.nodeId].update_safety_acc(true);
            bool = true;
        }
        Node node = this;
        while (node != null) {
            Iterator iterator = node.incoming.iterator();
            while (iterator.hasNext()) {
                Node node1 = iterator.next();
                int i = node1.getId();
                if (paramArrayOfState[i] == null)
                    paramArrayOfState[i] = new State();
                paramArrayOfState[i].add(new Transition(node.old, this.equivalenceId, this.accepting, bool));
            }
            node = node.OtherTransitionSource;
        }
    }
}
