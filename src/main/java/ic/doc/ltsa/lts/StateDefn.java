package ic.doc.ltsa.lts;

import java.util.Hashtable;

class StateDefn extends Declaration {
    Symbol name;
    boolean accept = false;
    ActionLabels range;
    StateExpr stateExpr;

    private void check_put(String paramString, StateMachine paramStateMachine) {
        if (paramStateMachine.explicit_states.containsKey(paramString)) {
            Diagnostics.fatal("duplicate definition -" + this.name, this.name);
        } else {
            paramStateMachine.explicit_states.put(paramString, paramStateMachine.stateLabel.label());
        }
    }

    public void explicitStates(StateMachine paramStateMachine) {
        if (this.range == null) {
            String str = this.name.toString();
            if (str.equals("STOP") || str.equals("ERROR") || str.equals("END"))
                Diagnostics.fatal("reserved local process name -" + this.name, this.name);
            check_put(str, paramStateMachine);
        } else {
            Hashtable hashtable = new Hashtable();
            this.range.initContext(hashtable, paramStateMachine.constants);
            while (this.range.hasMoreNames())
                check_put(this.name.toString() + "." + this.range.nextName(), paramStateMachine);
            this.range.clearContext();
        }
    }

    private void crunchAlias(StateExpr paramStateExpr, String paramString, Hashtable paramHashtable, StateMachine paramStateMachine) {
        String str = paramStateExpr.evalName(paramHashtable, paramStateMachine);
        Integer integer = (Integer) paramStateMachine.explicit_states.get(str);
        if (integer == null)
            if (str.equals("STOP")) {
                paramStateMachine.explicit_states.put("STOP", integer = paramStateMachine.stateLabel.label());
            } else if (str.equals("ERROR")) {
                paramStateMachine.explicit_states.put("ERROR", integer = new Integer(-1));
            } else if (str.equals("END")) {
                paramStateMachine.explicit_states.put("END", integer = paramStateMachine.stateLabel.label());
            } else {
                paramStateMachine.explicit_states.put("ERROR", integer = new Integer(-1));
                Diagnostics.warning(str + " defined to be ERROR", "definition not found- " + str, paramStateExpr.name);
            }
        CompactState compactState = null;
        if (paramStateExpr.processes != null)
            compactState = paramStateExpr.makeInserts(paramHashtable, paramStateMachine);
        if (compactState != null) {
            paramStateMachine.preAddSequential((Integer) paramStateMachine.explicit_states.get(paramString), integer, compactState);
        } else {
            paramStateMachine.aliases.put(paramStateMachine.explicit_states.get(paramString), integer);
        }
    }

    public void crunch(StateMachine paramStateMachine) {
        if (this.stateExpr.name == null && this.stateExpr.boolexpr == null)
            return;
        Hashtable hashtable = new Hashtable();
        if (this.range == null) {
            crunchit(paramStateMachine, hashtable, this.stateExpr, this.name.toString());
        } else {
            this.range.initContext(hashtable, paramStateMachine.constants);
            while (this.range.hasMoreNames()) {
                String str = "" + this.name + "." + this.range.nextName();
                crunchit(paramStateMachine, hashtable, this.stateExpr, str);
            }
            this.range.clearContext();
        }
    }

    private void crunchit(StateMachine paramStateMachine, Hashtable paramHashtable, StateExpr paramStateExpr, String paramString) {
        if (paramStateExpr.name != null) {
            crunchAlias(paramStateExpr, paramString, paramHashtable, paramStateMachine);
        } else if (paramStateExpr.boolexpr != null) {
            if (Expression.evaluate(paramStateExpr.boolexpr, paramHashtable, paramStateMachine.constants) != 0) {
                paramStateExpr = paramStateExpr.thenpart;
            } else {
                paramStateExpr = paramStateExpr.elsepart;
            }
            if (paramStateExpr != null)
                crunchit(paramStateMachine, paramHashtable, paramStateExpr, paramString);
        }
    }

    public void transition(StateMachine paramStateMachine) {
        if (this.stateExpr.name != null)
            return;
        Hashtable hashtable = new Hashtable();
        if (this.range == null) {
            int i = ((Integer) paramStateMachine.explicit_states.get("" + this.name)).intValue();
            this.stateExpr.firstTransition(i, hashtable, paramStateMachine);
            if (this.accept) {
                if (!paramStateMachine.alphabet.containsKey("@"))
                    paramStateMachine.alphabet.put("@", paramStateMachine.eventLabel.label());
                Symbol symbol = new Symbol(124, "@");
                paramStateMachine.transitions.addElement(new Transition(i, symbol, i));
            }
        } else {
            this.range.initContext(hashtable, paramStateMachine.constants);
            while (this.range.hasMoreNames()) {
                int i = ((Integer) paramStateMachine.explicit_states.get("" + this.name + "." + this.range.nextName())).intValue();
                this.stateExpr.firstTransition(i, hashtable, paramStateMachine);
            }
            this.range.clearContext();
        }
    }

    public StateDefn myclone() {
        StateDefn stateDefn = new StateDefn();
        stateDefn.name = this.name;
        stateDefn.accept = this.accept;
        if (this.range != null)
            stateDefn.range = this.range.myclone();
        if (this.stateExpr != null)
            stateDefn.stateExpr = this.stateExpr.myclone();
        return stateDefn;
    }
}
