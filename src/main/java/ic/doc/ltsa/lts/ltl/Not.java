package ic.doc.ltsa.lts.ltl;

class Not extends Formula {
    Formula next;

    Formula getNext() {
        return this.next;
    }

    Not(Formula paramFormula) {
        this.next = paramFormula;
    }

    public String toString() {
        return "!" + this.next.toString();
    }

    Formula accept(Visitor paramVisitor) {
        return paramVisitor.visit(this);
    }

    boolean isLiteral() {
        return this.next.isLiteral();
    }
}
