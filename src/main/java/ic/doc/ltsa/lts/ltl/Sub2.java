package ic.doc.ltsa.lts.ltl;

class Sub2 implements Visitor {
    private static Sub2 inst;

    public static Sub2 get() {
        if (inst == null)
            inst = new Sub2();
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
        return null;
    }

    public Formula visit(Next paramNext) {
        return null;
    }

    public Formula visit(And paramAnd) {
        return paramAnd.getRight();
    }

    public Formula visit(Or paramOr) {
        return paramOr.getRight();
    }

    public Formula visit(Until paramUntil) {
        return paramUntil.getRight();
    }

    public Formula visit(Release paramRelease) {
        return paramRelease.getLeft();
    }
}
