package ic.doc.ltsa.lts.ltl;

class False extends Formula {
    private static False f;

    public static False make() {
        if (f == null) {
            f = new False();
            f.setId(0);
        }
        return f;
    }

    public String toString() {
        return "false";
    }

    Formula accept(Visitor paramVisitor) {
        return paramVisitor.visit(this);
    }

    boolean isLiteral() {
        return true;
    }
}
