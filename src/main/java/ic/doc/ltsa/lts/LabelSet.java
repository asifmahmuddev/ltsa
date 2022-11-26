package ic.doc.ltsa.lts;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

class LabelSet {
    boolean isConstant = false;
    Vector labels;
    Vector actions;
    static Hashtable constants;

    public LabelSet(Symbol paramSymbol, Vector paramVector) {
        this.labels = paramVector;
        if (constants.put(paramSymbol.toString(), this) != null)
            Diagnostics.fatal("duplicate set definition: " + paramSymbol, paramSymbol);
        this.actions = getActions(null);
        this.isConstant = true;
        this.labels = null;
    }

    public LabelSet(Vector paramVector) {
        this.labels = paramVector;
    }

    public Vector getActions(Hashtable paramHashtable) {
        return getActions(null, paramHashtable);
    }

    public Vector getActions(Hashtable paramHashtable1, Hashtable paramHashtable2) {
        if (this.isConstant)
            return this.actions;
        if (this.labels == null)
            return null;
        Vector vector = new Vector();
        Hashtable hashtable = new Hashtable();
        Hashtable hashtable1 = (paramHashtable1 != null) ? (Hashtable) paramHashtable1.clone() : null;
        Enumeration enumeration = this.labels.elements();
        while (enumeration.hasMoreElements()) {
            ActionLabels actionLabels = enumeration.nextElement();
            actionLabels.initContext(hashtable1, paramHashtable2);
            while (actionLabels.hasMoreNames()) {
                String str = actionLabels.nextName();
                if (!hashtable.containsKey(str)) {
                    vector.addElement(str);
                    hashtable.put(str, str);
                }
            }
            actionLabels.clearContext();
        }
        return vector;
    }
}
