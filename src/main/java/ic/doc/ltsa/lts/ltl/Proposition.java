package ic.doc.ltsa.lts.ltl;

import ic.doc.ltsa.lts.Symbol;

class Proposition extends Formula {
    Symbol sym;

    Proposition(Symbol paramSymbol) {
        this.sym = paramSymbol;
    }

    public String toString() {
        return this.sym.toString();
    }

    Formula accept(Visitor paramVisitor) {
        return paramVisitor.visit(this);
    }

    boolean isLiteral() {
        return true;
    }
}
