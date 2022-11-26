package ic.doc.ltsa.lts;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

class StateExpr extends Declaration {
    Vector processes;
    Symbol name;
    Vector expr;
    Vector choices;
    Stack boolexpr;
    StateExpr thenpart;
    StateExpr elsepart;

    public void addSeqProcessRef(SeqProcessRef paramSeqProcessRef) {
        if (this.processes == null)
            this.processes = new Vector();
        this.processes.addElement(paramSeqProcessRef);
    }

    public CompactState makeInserts(Hashtable paramHashtable, StateMachine paramStateMachine) {
        Vector vector = new Vector();
        Enumeration enumeration = this.processes.elements();
        while (enumeration.hasMoreElements()) {
            SeqProcessRef seqProcessRef = enumeration.nextElement();
            CompactState compactState = seqProcessRef.instantiate(paramHashtable, paramStateMachine.constants);
            if (!compactState.isEnd())
                vector.addElement(compactState);
        }
        if (vector.size() > 0)
            return CompactState.sequentialCompose(vector);
        return null;
    }

    public Integer instantiate(Integer paramInteger, Hashtable paramHashtable, StateMachine paramStateMachine) {
        if (this.processes == null)
            return paramInteger;
        CompactState compactState = makeInserts(paramHashtable, paramStateMachine);
        if (compactState == null)
            return paramInteger;
        Integer integer = paramStateMachine.stateLabel.interval(compactState.maxStates);
        compactState.offsetSeq(integer.intValue(), paramInteger.intValue());
        paramStateMachine.addSequential(integer, compactState);
        return integer;
    }

    public void firstTransition(int paramInt, Hashtable paramHashtable, StateMachine paramStateMachine) {
        if (this.boolexpr != null) {
            if (Expression.evaluate(this.boolexpr, paramHashtable, paramStateMachine.constants) != 0) {
                if (this.thenpart.name == null)
                    this.thenpart.firstTransition(paramInt, paramHashtable, paramStateMachine);
            } else if (this.elsepart.name == null) {
                this.elsepart.firstTransition(paramInt, paramHashtable, paramStateMachine);
            }
        } else {
            addTransition(paramInt, paramHashtable, paramStateMachine);
        }
    }

    public void addTransition(int paramInt, Hashtable paramHashtable, StateMachine paramStateMachine) {
        Enumeration enumeration = this.choices.elements();
        while (enumeration.hasMoreElements()) {
            ChoiceElement choiceElement = enumeration.nextElement();
            choiceElement.addTransition(paramInt, paramHashtable, paramStateMachine);
        }
    }

    public void endTransition(int paramInt, Symbol paramSymbol, Hashtable paramHashtable, StateMachine paramStateMachine) {
        if (this.boolexpr != null) {
            if (Expression.evaluate(this.boolexpr, paramHashtable, paramStateMachine.constants) != 0) {
                this.thenpart.endTransition(paramInt, paramSymbol, paramHashtable, paramStateMachine);
            } else {
                this.elsepart.endTransition(paramInt, paramSymbol, paramHashtable, paramStateMachine);
            }
        } else if (this.name != null) {
            Integer integer = (Integer) paramStateMachine.explicit_states.get(evalName(paramHashtable, paramStateMachine));
            if (integer == null)
                if (evalName(paramHashtable, paramStateMachine).equals("STOP")) {
                    paramStateMachine.explicit_states.put("STOP", integer = paramStateMachine.stateLabel.label());
                } else if (evalName(paramHashtable, paramStateMachine).equals("ERROR")) {
                    paramStateMachine.explicit_states.put("ERROR", integer = new Integer(-1));
                } else if (evalName(paramHashtable, paramStateMachine).equals("END")) {
                    paramStateMachine.explicit_states.put("END", integer = paramStateMachine.stateLabel.label());
                } else {
                    paramStateMachine.explicit_states.put(evalName(paramHashtable, paramStateMachine), integer = new Integer(-1));
                    Diagnostics.warning(evalName(paramHashtable, paramStateMachine) + " defined to be ERROR", "definition not found- " + evalName(paramHashtable, paramStateMachine), this.name);
                }
            integer = instantiate(integer, paramHashtable, paramStateMachine);
            paramStateMachine.transitions.addElement(new Transition(paramInt, paramSymbol, integer.intValue()));
        } else {
            Integer integer = paramStateMachine.stateLabel.label();
            paramStateMachine.transitions.addElement(new Transition(paramInt, paramSymbol, integer.intValue()));
            addTransition(integer.intValue(), paramHashtable, paramStateMachine);
        }
    }

    public String evalName(Hashtable paramHashtable, StateMachine paramStateMachine) {
        if (this.expr == null)
            return this.name.toString();
        Enumeration enumeration = this.expr.elements();
        String str = this.name.toString();
        while (enumeration.hasMoreElements()) {
            Stack stack = enumeration.nextElement();
            str = str + "." + Expression.getValue(stack, paramHashtable, paramStateMachine.constants);
        }
        return str;
    }

    public StateExpr myclone() {
        StateExpr stateExpr = new StateExpr();
        stateExpr.processes = this.processes;
        stateExpr.name = this.name;
        stateExpr.expr = this.expr;
        if (this.choices != null) {
            stateExpr.choices = new Vector();
            Enumeration enumeration = this.choices.elements();
            while (enumeration.hasMoreElements())
                stateExpr.choices.addElement(((ChoiceElement) enumeration.nextElement()).myclone());
        }
        stateExpr.boolexpr = this.boolexpr;
        if (this.thenpart != null)
            stateExpr.thenpart = this.thenpart.myclone();
        if (this.elsepart != null)
            stateExpr.elsepart = this.elsepart.myclone();
        return stateExpr;
    }
}
