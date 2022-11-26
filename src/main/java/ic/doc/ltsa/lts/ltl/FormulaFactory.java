package ic.doc.ltsa.lts.ltl;

import ic.doc.ltsa.lts.Diagnostics;
import ic.doc.ltsa.lts.Symbol;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class FormulaFactory {
    NotVisitor nv = new NotVisitor(this);
    Map subf = new HashMap();
    SortedSet props = new TreeSet();
    int id = 1;
    Formula formula;

    public void setFormula(Formula paramFormula) {
        this.formula = makeNot(paramFormula);
    }

    public Formula getFormula() {
        return this.formula;
    }

    public Formula make(Symbol paramSymbol) {
        return unique(new Proposition(paramSymbol));
    }

    public SortedSet getProps() {
        return this.props;
    }

    public Formula make(Formula paramFormula1, Symbol paramSymbol, Formula paramFormula2) {
        switch (paramSymbol.kind) {
            case 45 :
                return makeNot(paramFormula2);
            case 23 :
                return makeNext(paramFormula2);
            case 74 :
                return makeEventually(paramFormula2);
            case 75 :
                return makeAlways(paramFormula2);
            case 42 :
                return makeAnd(paramFormula1, paramFormula2);
            case 40 :
                return makeOr(paramFormula1, paramFormula2);
            case 69 :
                return makeImplies(paramFormula1, paramFormula2);
            case 20 :
                return makeUntil(paramFormula1, paramFormula2);
            case 76 :
                return makeEquivalent(paramFormula1, paramFormula2);
        }
        Diagnostics.fatal("Unexpected operator in LTL expression: " + paramSymbol, paramSymbol);
        return null;
    }

    Formula makeAnd(Formula paramFormula1, Formula paramFormula2) {
        if (paramFormula1 == paramFormula2)
            return paramFormula1;
        if (paramFormula1 == False.make() || paramFormula2 == False.make())
            return False.make();
        if (paramFormula1 == True.make())
            return paramFormula2;
        if (paramFormula2 == True.make())
            return paramFormula1;
        if (paramFormula1 == makeNot(paramFormula2))
            return False.make();
        if (paramFormula1.compareTo(paramFormula2) < 0)
            return unique(new And(paramFormula1, paramFormula2));
        return unique(new And(paramFormula2, paramFormula1));
    }

    Formula makeOr(Formula paramFormula1, Formula paramFormula2) {
        if (paramFormula1 == paramFormula2)
            return paramFormula1;
        if (paramFormula1 == True.make() || paramFormula2 == True.make())
            return True.make();
        if (paramFormula1 == False.make())
            return paramFormula2;
        if (paramFormula2 == False.make())
            return paramFormula1;
        if (paramFormula1 == makeNot(paramFormula2))
            return True.make();
        if (paramFormula1.compareTo(paramFormula2) < 0)
            return unique(new Or(paramFormula1, paramFormula2));
        return unique(new Or(paramFormula2, paramFormula1));
    }

    Formula makeUntil(Formula paramFormula1, Formula paramFormula2) {
        if (paramFormula2 == False.make())
            return False.make();
        return unique(new Until(paramFormula1, paramFormula2));
    }

    Formula makeRelease(Formula paramFormula1, Formula paramFormula2) {
        return unique(new Release(paramFormula1, paramFormula2));
    }

    Formula makeImplies(Formula paramFormula1, Formula paramFormula2) {
        return makeOr(makeNot(paramFormula1), paramFormula2);
    }

    Formula makeEquivalent(Formula paramFormula1, Formula paramFormula2) {
        return makeAnd(makeImplies(paramFormula1, paramFormula2), makeImplies(paramFormula2, paramFormula1));
    }

    Formula makeEventually(Formula paramFormula) {
        return makeUntil(True.make(), paramFormula);
    }

    Formula makeAlways(Formula paramFormula) {
        return makeRelease(False.make(), paramFormula);
    }

    Formula makeNot(Formula paramFormula) {
        return paramFormula.accept(this.nv);
    }

    Formula makeNot(Proposition paramProposition) {
        return unique(new Not(paramProposition));
    }

    Formula makeNext(Formula paramFormula) {
        return unique(new Next(paramFormula));
    }

    int processUntils(Formula paramFormula, List paramList) {
        paramFormula.accept(new UntilVisitor(this, paramList));
        return paramList.size();
    }

    boolean specialCaseV(Formula paramFormula, Set paramSet) {
        Formula formula = makeRelease(False.make(), paramFormula);
        return paramSet.contains(formula);
    }

    boolean syntaxImplied(Formula paramFormula, SortedSet paramSortedSet1, SortedSet paramSortedSet2) {
        boolean bool;
        if (paramFormula == null)
            return true;
        if (paramFormula instanceof True)
            return true;
        if (paramSortedSet1.contains(paramFormula))
            return true;
        if (paramFormula.isLiteral())
            return false;
        Formula formula1 = paramFormula.getSub1();
        Formula formula2 = paramFormula.getSub2();
        Formula formula3 = (paramFormula instanceof Until || paramFormula instanceof Release) ? paramFormula : null;
        boolean bool1 = syntaxImplied(formula2, paramSortedSet1, paramSortedSet2);
        boolean bool2 = syntaxImplied(formula1, paramSortedSet1, paramSortedSet2);
        if (formula3 != null) {
            if (paramSortedSet2 != null) {
                bool = paramSortedSet2.contains(formula3);
            } else {
                bool = false;
            }
        } else {
            bool = true;
        }
        if (paramFormula instanceof Until || paramFormula instanceof Or)
            return (bool1 || (bool2 && bool));
        if (paramFormula instanceof Release)
            return ((bool2 && bool1) || (bool2 && bool));
        if (paramFormula instanceof And)
            return (bool2 && bool1);
        if (paramFormula instanceof Next) {
            if (formula1 != null) {
                if (paramSortedSet2 != null)
                    return paramSortedSet2.contains(formula1);
                return false;
            }
            return true;
        }
        return false;
    }

    private int newId() {
        return ++this.id;
    }

    private Formula unique(Formula paramFormula) {
        String str = paramFormula.toString();
        if (this.subf.containsKey(str))
            return (Formula) this.subf.get(str);
        paramFormula.setId(newId());
        this.subf.put(str, paramFormula);
        if (paramFormula instanceof Proposition)
            this.props.add(paramFormula);
        return paramFormula;
    }
}
