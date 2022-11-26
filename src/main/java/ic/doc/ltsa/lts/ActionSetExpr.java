package ic.doc.ltsa.lts;

import java.util.Enumeration;
import java.util.Vector;

class ActionSetExpr extends ActionLabels {
    protected LabelSet left;
    protected LabelSet right;
    protected Vector actions;
    protected int current;
    protected int high;
    protected int low;

    public ActionSetExpr(LabelSet paramLabelSet1, LabelSet paramLabelSet2) {
        this.left = paramLabelSet1;
        this.right = paramLabelSet2;
    }

    protected String computeName() {
        return this.actions.elementAt(this.current);
    }

    protected void initialise() {
        Vector vector1 = this.left.getActions(this.locals, this.globals);
        Vector vector2 = this.right.getActions(this.locals, this.globals);
        this.actions = new Vector();
        Enumeration enumeration = vector1.elements();
        while (enumeration.hasMoreElements()) {
            String str = enumeration.nextElement();
            if (!vector2.contains(str))
                this.actions.addElement(str);
        }
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
        return new ActionSetExpr(this.left, this.right);
    }
}
