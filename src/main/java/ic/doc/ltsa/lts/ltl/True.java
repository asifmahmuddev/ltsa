package ic.doc.ltsa.lts.ltl;

class True extends Formula {
    private static True t;

    public static True make() {
        if (t == null) {
            t = new True();
            t.setId(1);
        }
        return t;
    }

    public String toString() {
        return "true";
    }

    Formula accept(Visitor paramVisitor) {
        return paramVisitor.visit(this);
    }

    boolean isLiteral() {
        return true;
    }
}
