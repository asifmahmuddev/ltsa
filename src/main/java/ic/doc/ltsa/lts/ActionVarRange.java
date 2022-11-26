package ic.doc.ltsa.lts;

import java.util.Stack;

class ActionVarRange extends ActionRange {
    protected Symbol var;

    public ActionVarRange(Symbol paramSymbol, Stack paramStack1, Stack paramStack2) {
        super(paramStack1, paramStack2);
        this.var = paramSymbol;
    }

    public ActionVarRange(Symbol paramSymbol, Range paramRange) {
        super(paramRange);
        this.var = paramSymbol;
    }

    protected String computeName() {
        if (this.locals != null)
            this.locals.put(this.var.toString(), new Value(this.current));
        return String.valueOf(this.current);
    }

    protected void checkDuplicateVarDefn() {
        if (this.locals == null)
            return;
        if (this.locals.get(this.var.toString()) != null)
            Diagnostics.fatal("Duplicate variable definition: " + this.var, this.var);
    }

    protected void removeVarDefn() {
        if (this.locals != null)
            this.locals.remove(this.var.toString());
    }

    protected ActionLabels make() {
        return new ActionVarRange(this.var, this.rlow, this.rhigh);
    }
}
