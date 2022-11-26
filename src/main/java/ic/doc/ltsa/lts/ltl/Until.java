package ic.doc.ltsa.lts.ltl;

class Until extends Formula {
    Formula left;
    Formula right;

    Formula getLeft() {
        return this.left;
    }

    Formula getRight() {
        return this.right;
    }

    Until(Formula paramFormula1, Formula paramFormula2) {
        this.left = paramFormula1;
        this.right = paramFormula2;
    }

    public String toString() {
        return "(" + this.left.toString() + " U " + this.right.toString() + ")";
    }

    Formula accept(Visitor paramVisitor) {
        return paramVisitor.visit(this);
    }
}
