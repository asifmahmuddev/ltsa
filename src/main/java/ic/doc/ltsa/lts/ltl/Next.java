package ic.doc.ltsa.lts.ltl;

class Next extends Formula {
    Formula next;

    Formula getNext() {
        return this.next;
    }

    Next(Formula paramFormula) {
        this.next = paramFormula;
    }

    public String toString() {
        return "X " + this.next.toString();
    }

    Formula accept(Visitor paramVisitor) {
        return paramVisitor.visit(this);
    }
}
