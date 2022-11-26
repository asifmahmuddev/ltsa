package ic.doc.ltsa.lts;

class ActionVarSet extends ActionSet {
    protected Symbol var;

    public ActionVarSet(Symbol paramSymbol, LabelSet paramLabelSet) {
        super(paramLabelSet);
        this.var = paramSymbol;
    }

    protected String computeName() {
        String str = this.actions.elementAt(this.current);
        if (this.locals != null)
            this.locals.put(this.var.toString(), new Value(str));
        return str;
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
        return new ActionVarSet(this.var, this.set);
    }
}
