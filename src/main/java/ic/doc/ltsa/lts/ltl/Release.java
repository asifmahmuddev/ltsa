package ic.doc.ltsa.lts.ltl;

class Release extends Formula {
    Formula left;
    Formula right;

    Formula getLeft() {
        return this.left;
    }

    Formula getRight() {
        return this.right;
    }

    Release(Formula paramFormula1, Formula paramFormula2) {
        this.left = paramFormula1;
        this.right = paramFormula2;
    }

    public String toString() {
        return "(" + this.left.toString() + " R " + this.right.toString() + ")";
    }

    Formula accept(Visitor paramVisitor) {
        return paramVisitor.visit(this);
    }
}
