package ic.doc.ltsa.lts.ltl;

import java.util.List;

class UntilVisitor implements Visitor {
    private FormulaFactory fac;
    private List ll;

    UntilVisitor(FormulaFactory paramFormulaFactory, List paramList) {
        this.fac = paramFormulaFactory;
        this.ll = paramList;
    }

    public Formula visit(True paramTrue) {
        return paramTrue;
    }

    public Formula visit(False paramFalse) {
        return paramFalse;
    }

    public Formula visit(Proposition paramProposition) {
        return paramProposition;
    }

    public Formula visit(Not paramNot) {
        paramNot.getNext().accept(this);
        return paramNot;
    }

    public Formula visit(Next paramNext) {
        paramNext.getNext().accept(this);
        return paramNext;
    }

    public Formula visit(And paramAnd) {
        paramAnd.getLeft().accept(this);
        paramAnd.getRight().accept(this);
        return paramAnd;
    }

    public Formula visit(Or paramOr) {
        paramOr.getLeft().accept(this);
        paramOr.getRight().accept(this);
        return paramOr;
    }

    public Formula visit(Until paramUntil) {
        this.ll.add(paramUntil);
        paramUntil.setUI(this.ll.size() - 1);
        paramUntil.getRight().setRofUI(this.ll.size() - 1);
        paramUntil.getLeft().accept(this);
        paramUntil.getRight().accept(this);
        return paramUntil;
    }

    public Formula visit(Release paramRelease) {
        paramRelease.getLeft().accept(this);
        paramRelease.getRight().accept(this);
        return paramRelease;
    }
}
