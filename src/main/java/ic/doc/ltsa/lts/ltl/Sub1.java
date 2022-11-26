package ic.doc.ltsa.lts.ltl;

class Sub1 implements Visitor {
    private static Sub1 inst;

    public static Sub1 get() {
        if (inst == null)
            inst = new Sub1();
        return inst;
    }

    public Formula visit(True paramTrue) {
        return null;
    }

    public Formula visit(False paramFalse) {
        return null;
    }

    public Formula visit(Proposition paramProposition) {
        return null;
    }

    public Formula visit(Not paramNot) {
        return paramNot.getNext();
    }

    public Formula visit(Next paramNext) {
        return paramNext.getNext();
    }

    public Formula visit(And paramAnd) {
        return paramAnd.getLeft();
    }

    public Formula visit(Or paramOr) {
        return paramOr.getLeft();
    }

    public Formula visit(Until paramUntil) {
        return paramUntil.getLeft();
    }

    public Formula visit(Release paramRelease) {
        return paramRelease.getRight();
    }
}
