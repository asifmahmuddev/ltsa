package ic.doc.ltsa.lts;

import java.util.Vector;

class ActionSet extends ActionLabels {
    protected LabelSet set;
    protected Vector actions;
    protected int current;
    protected int high;
    protected int low;

    public ActionSet(LabelSet paramLabelSet) {
        this.set = paramLabelSet;
    }

    protected String computeName() {
        return this.actions.elementAt(this.current);
    }

    protected void initialise() {
        this.actions = this.set.getActions(this.locals, this.globals);
        this.current = this.low = 0;
        this.high = this.actions.size() - 1;
    }

    protected void next() {
        this.current++;
    }

    public boolean hasMoreNames() {
        return (this.current <= this.high);
    }

    protected ActionLabels make() {
        return new ActionSet(this.set);
    }
}
