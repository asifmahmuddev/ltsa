package ic.doc.ltsa.lts.ltl;

import ic.doc.ltsa.lts.ActionLabels;
import ic.doc.ltsa.lts.Diagnostics;
import ic.doc.ltsa.lts.Expression;
import ic.doc.ltsa.lts.Symbol;
import java.util.Hashtable;
import java.util.Stack;
import java.util.TreeSet;
import java.util.Vector;

public class PredicateDefinition {
    Symbol name;
    ActionLabels trueSet;
    ActionLabels falseSet;
    Vector trueActions;
    Vector falseActions;
    Stack expr;
    boolean initial;
    static Hashtable definitions;

    private PredicateDefinition(Symbol paramSymbol, ActionLabels paramActionLabels1, ActionLabels paramActionLabels2, Stack paramStack) {
        this.name = paramSymbol;
        this.trueSet = paramActionLabels1;
        this.falseSet = paramActionLabels2;
        this.expr = paramStack;
        this.initial = false;
    }

    public static void put(Symbol paramSymbol, ActionLabels paramActionLabels1, ActionLabels paramActionLabels2, Stack paramStack) {
        if (definitions == null)
            definitions = new Hashtable();
        if (definitions.put(paramSymbol.toString(), new PredicateDefinition(paramSymbol, paramActionLabels1, paramActionLabels2, paramStack)) != null)
            Diagnostics.fatal("duplicate LTL predicate definition: " + paramSymbol, paramSymbol);
    }

    public static void init() {
        definitions = null;
    }

    public static PredicateDefinition get(String paramString) {
        if (definitions == null)
            return null;
        PredicateDefinition predicateDefinition = (PredicateDefinition) definitions.get(paramString);
        if (predicateDefinition == null)
            Diagnostics.fatal("Predicate " + paramString + " not found");
        predicateDefinition.trueActions = predicateDefinition.trueSet.getActions(null, null);
        predicateDefinition.falseActions = predicateDefinition.falseSet.getActions(null, null);
        TreeSet treeSet = new TreeSet(predicateDefinition.trueActions);
        treeSet.retainAll(predicateDefinition.falseActions);
        if (!treeSet.isEmpty())
            Diagnostics.fatal("Predicate " + predicateDefinition.name + " True & False sets must be disjoint", predicateDefinition.name);
        if (predicateDefinition.expr != null) {
            int i = Expression.evaluate(predicateDefinition.expr, null, null);
            predicateDefinition.initial = (i > 0);
        }
        return predicateDefinition;
    }
}
