package ic.doc.ltsa.lts.ltl;

class NotVisitor implements Visitor {
    private FormulaFactory fac;

    NotVisitor(FormulaFactory paramFormulaFactory) {
        this.fac = paramFormulaFactory;
    }

    public Formula visit(True paramTrue) {
        return False.make();
    }

    public Formula visit(False paramFalse) {
        return True.make();
    }

    public Formula visit(Proposition paramProposition) {
        return this.fac.makeNot(paramProposition);
    }

    public Formula visit(Not paramNot) {
        return paramNot.getNext();
    }

    public Formula visit(Next paramNext) {
        return this.fac.makeNext(this.fac.makeNot(paramNext.getNext()));
    }

    public Formula visit(And paramAnd) {
        return this.fac.makeOr(this.fac.makeNot(paramAnd.getLeft()), this.fac.makeNot(paramAnd.getRight()));
    }

    public Formula visit(Or paramOr) {
        return this.fac.makeAnd(this.fac.makeNot(paramOr.getLeft()), this.fac.makeNot(paramOr.getRight()));
    }

    public Formula visit(Until paramUntil) {
        return this.fac.makeRelease(this.fac.makeNot(paramUntil.getLeft()), this.fac.makeNot(paramUntil.getRight()));
    }

    public Formula visit(Release paramRelease) {
        return this.fac.makeUntil(this.fac.makeNot(paramRelease.getLeft()), this.fac.makeNot(paramRelease.getRight()));
    }
}
