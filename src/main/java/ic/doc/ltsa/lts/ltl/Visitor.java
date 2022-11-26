package ic.doc.ltsa.lts.ltl;

interface Visitor {
    Formula visit(True paramTrue);

    Formula visit(False paramFalse);

    Formula visit(Proposition paramProposition);

    Formula visit(Not paramNot);

    Formula visit(And paramAnd);

    Formula visit(Or paramOr);

    Formula visit(Until paramUntil);

    Formula visit(Release paramRelease);

    Formula visit(Next paramNext);
}
