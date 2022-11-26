package ic.doc.ltsa.lts;

import ic.doc.extension.Relation;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

class CompositeBody {
    ProcessRef singleton;
    Vector procRefs;
    Stack boolexpr;
    CompositeBody thenpart;
    CompositeBody elsepart;
    ActionLabels range;
    ActionLabels prefix;
    ActionLabels accessSet;
    Vector relabelDefns;
    private Vector accessors = null;
    private Relation relabels = null;

    void compose(CompositionExpression paramCompositionExpression, Vector paramVector, Hashtable paramHashtable) {
        Vector vector = (this.accessSet == null) ? null : this.accessSet.getActions(paramHashtable, paramCompositionExpression.constants);
        Relation relation = RelabelDefn.getRelabels(this.relabelDefns, paramCompositionExpression.constants, paramHashtable);
        if (this.boolexpr != null) {
            if (Expression.evaluate(this.boolexpr, paramHashtable, paramCompositionExpression.constants) != 0) {
                this.thenpart.compose(paramCompositionExpression, paramVector, paramHashtable);
            } else if (this.elsepart != null) {
                this.elsepart.compose(paramCompositionExpression, paramVector, paramHashtable);
            }
        } else if (this.range != null) {
            this.range.initContext(paramHashtable, paramCompositionExpression.constants);
            while (this.range.hasMoreNames()) {
                this.range.nextName();
                this.thenpart.compose(paramCompositionExpression, paramVector, paramHashtable);
            }
            this.range.clearContext();
        } else {
            Vector vector1 = getPrefixedMachines(paramCompositionExpression, paramHashtable);
            if (vector != null)
                for (Enumeration enumeration1 = vector1.elements(); enumeration1.hasMoreElements();) {
                    CompactState compactState = (CompactState) enumeration1.nextElement();
                    if (compactState instanceof CompactState) {
                        CompactState compactState1 = compactState;
                        compactState1.addAccess(vector);
                        continue;
                    }
                    CompositeState compositeState = (CompositeState) compactState;
                    compositeState.addAccess(vector);
                }
            if (relation != null)
                for (byte b = 0; b < vector1.size(); b++) {
                    CompactState compactState = (CompactState) vector1.elementAt(b);
                    if (compactState instanceof CompactState) {
                        CompactState compactState1 = compactState;
                        compactState1.relabel(relation);
                    } else {
                        CompositeState compositeState = (CompositeState) compactState;
                        CompactState compactState1 = compositeState.relabel(relation, paramCompositionExpression.output);
                        if (compactState1 != null)
                            vector1.setElementAt(compactState1, b);
                    }
                }
            for (Enumeration enumeration = vector1.elements(); enumeration.hasMoreElements();)
                paramVector.addElement(enumeration.nextElement());
        }
    }

    private Vector getPrefixedMachines(CompositionExpression paramCompositionExpression, Hashtable paramHashtable) {
        if (this.prefix == null)
            return getMachines(paramCompositionExpression, paramHashtable);
        Vector vector = new Vector();
        this.prefix.initContext(paramHashtable, paramCompositionExpression.constants);
        while (this.prefix.hasMoreNames()) {
            String str = this.prefix.nextName();
            Vector vector1 = getMachines(paramCompositionExpression, paramHashtable);
            for (Enumeration enumeration = vector1.elements(); enumeration.hasMoreElements();) {
                CompactState compactState = (CompactState) enumeration.nextElement();
                if (compactState instanceof CompactState) {
                    CompactState compactState1 = compactState;
                    compactState1.prefixLabels(str);
                    vector.addElement(compactState1);
                    continue;
                }
                CompositeState compositeState = (CompositeState) compactState;
                compositeState.prefixLabels(str);
                vector.addElement(compositeState);
            }
        }
        this.prefix.clearContext();
        return vector;
    }

    private Vector getMachines(CompositionExpression paramCompositionExpression, Hashtable paramHashtable) {
        Vector vector = new Vector();
        if (this.singleton != null) {
            this.singleton.instantiate(paramCompositionExpression, vector, paramCompositionExpression.output, paramHashtable);
        } else if (this.procRefs != null) {
            Enumeration enumeration = this.procRefs.elements();
            while (enumeration.hasMoreElements()) {
                CompositeBody compositeBody = enumeration.nextElement();
                compositeBody.compose(paramCompositionExpression, vector, paramHashtable);
            }
        }
        return vector;
    }
}
