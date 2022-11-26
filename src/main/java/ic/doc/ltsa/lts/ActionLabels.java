package ic.doc.ltsa.lts;

import java.util.Hashtable;
import java.util.Vector;

public abstract class ActionLabels {
    protected ActionLabels follower;
    protected Hashtable locals;
    protected Hashtable globals;

    public void addFollower(ActionLabels paramActionLabels) {
        this.follower = paramActionLabels;
    }

    public ActionLabels getFollower() {
        return this.follower;
    }

    public void initContext(Hashtable paramHashtable1, Hashtable paramHashtable2) {
        this.locals = paramHashtable1;
        this.globals = paramHashtable2;
        initialise();
        checkDuplicateVarDefn();
        if (this.follower != null)
            this.follower.initContext(paramHashtable1, paramHashtable2);
    }

    public void clearContext() {
        removeVarDefn();
        if (this.follower != null)
            this.follower.clearContext();
    }

    public String nextName() {
        String str = computeName();
        if (this.follower != null) {
            str = str + "." + this.follower.nextName();
            if (!this.follower.hasMoreNames()) {
                this.follower.initialise();
                next();
            }
        } else {
            next();
        }
        return str;
    }

    public abstract boolean hasMoreNames();

    public Vector getActions(Hashtable paramHashtable1, Hashtable paramHashtable2) {
        Vector vector = new Vector();
        initContext(paramHashtable1, paramHashtable2);
        while (hasMoreNames()) {
            String str = nextName();
            vector.addElement(str);
        }
        clearContext();
        return vector;
    }

    public boolean hasMultipleValues() {
        if (this instanceof ActionRange || this instanceof ActionSet || this instanceof ActionVarRange || this instanceof ActionVarSet)
            return true;
        if (this.follower != null)
            return this.follower.hasMultipleValues();
        return false;
    }

    protected void checkDuplicateVarDefn() {
    }

    protected void removeVarDefn() {
    }

    protected abstract String computeName();

    protected abstract void next();

    protected abstract void initialise();

    public ActionLabels myclone() {
        ActionLabels actionLabels = make();
        if (this.follower != null)
            actionLabels.follower = this.follower.myclone();
        return actionLabels;
    }

    protected abstract ActionLabels make();
}
