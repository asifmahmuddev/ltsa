package ic.doc.ltsa.lts;

class ActionName extends ActionLabels {
    protected Symbol name;
    protected boolean consumed;

    public ActionName(Symbol paramSymbol) {
        this.name = paramSymbol;
    }

    protected String computeName() {
        return this.name.toString();
    }

    protected void initialise() {
        this.consumed = false;
    }

    protected void next() {
        this.consumed = true;
    }

    public boolean hasMoreNames() {
        return !this.consumed;
    }

    protected ActionLabels make() {
        return new ActionName(this.name);
    }
}
