package ic.doc.ltsa.lts;

import java.util.Hashtable;
import java.util.Stack;

class ChoiceElement extends Declaration {
    Stack guard;
    ActionLabels action;
    StateExpr stateExpr;

    private void add(int paramInt, Hashtable paramHashtable, StateMachine paramStateMachine, ActionLabels paramActionLabels) {
        paramActionLabels.initContext(paramHashtable, paramStateMachine.constants);
        while (paramActionLabels.hasMoreNames()) {
            String str = paramActionLabels.nextName();
            Symbol symbol = new Symbol(124, str);
            if (!paramStateMachine.alphabet.containsKey(str))
                paramStateMachine.alphabet.put(str, paramStateMachine.eventLabel.label());
            this.stateExpr.endTransition(paramInt, symbol, paramHashtable, paramStateMachine);
        }
        paramActionLabels.clearContext();
    }

    private void add(int paramInt, Hashtable paramHashtable, StateMachine paramStateMachine, String paramString) {
        Symbol symbol = new Symbol(124, paramString);
        if (!paramStateMachine.alphabet.containsKey(paramString))
            paramStateMachine.alphabet.put(paramString, paramStateMachine.eventLabel.label());
        this.stateExpr.endTransition(paramInt, symbol, paramHashtable, paramStateMachine);
    }

    public void addTransition(int paramInt, Hashtable paramHashtable, StateMachine paramStateMachine) {
        if ((this.guard == null || Expression.evaluate(this.guard, paramHashtable, paramStateMachine.constants) != 0) && this.action != null)
            add(paramInt, paramHashtable, paramStateMachine, this.action);
    }

    public ChoiceElement myclone() {
        ChoiceElement choiceElement = new ChoiceElement();
        choiceElement.guard = this.guard;
        if (this.action != null)
            choiceElement.action = this.action.myclone();
        if (this.stateExpr != null)
            choiceElement.stateExpr = this.stateExpr.myclone();
        return choiceElement;
    }
}
