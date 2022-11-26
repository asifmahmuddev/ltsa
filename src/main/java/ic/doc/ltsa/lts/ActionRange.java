package ic.doc.ltsa.lts;

import java.util.Stack;

class ActionRange extends ActionLabels {
    Stack rlow;
    Stack rhigh;
    protected int current;
    protected int high;
    protected int low;

    public ActionRange(Stack paramStack1, Stack paramStack2) {
        this.rlow = paramStack1;
        this.rhigh = paramStack2;
    }

    public ActionRange(Range paramRange) {
        this.rlow = paramRange.low;
        this.rhigh = paramRange.high;
    }

    protected String computeName() {
        return String.valueOf(this.current);
    }

    protected void initialise() {
        this.low = Expression.evaluate(this.rlow, this.locals, this.globals);
        this.high = Expression.evaluate(this.rhigh, this.locals, this.globals);
        if (this.low > this.high)
            Diagnostics.fatal("Range not defined", this.rlow.peek());
        this.current = this.low;
    }

    protected void next() {
        this.current++;
    }

    public boolean hasMoreNames() {
        return (this.current <= this.high);
    }

    protected ActionLabels make() {
        return new ActionRange(this.rlow, this.rhigh);
    }
}
