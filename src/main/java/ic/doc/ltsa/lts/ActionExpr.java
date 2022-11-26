package ic.doc.ltsa.lts;

import java.util.Stack;

class ActionExpr extends ActionLabels {
    protected Stack expr;
    protected boolean consumed;

    public ActionExpr(Stack paramStack) {
        this.expr = paramStack;
    }

    protected String computeName() {
        Value value = Expression.getValue(this.expr, this.locals, this.globals);
        return value.toString();
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
        return new ActionExpr(this.expr);
    }
}
