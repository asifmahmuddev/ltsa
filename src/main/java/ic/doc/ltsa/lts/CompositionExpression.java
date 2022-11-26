package ic.doc.ltsa.lts;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

class CompositionExpression {
    Symbol name;
    CompositeBody body;
    Hashtable constants;
    Hashtable init_constants = new Hashtable();
    Vector parameters = new Vector();
    Hashtable processes;
    Hashtable compiledProcesses;
    Hashtable composites;
    LTSOutput output;
    boolean priorityIsLow = true;
    LabelSet priorityActions;
    LabelSet alphaHidden;
    boolean exposeNotHide = false;
    boolean makeDeterministic = false;
    boolean makeMinimal = false;
    boolean makeProperty = false;
    boolean makeCompose = false;

    CompositeState compose(Vector paramVector) {
        Vector vector1 = new Vector();
        Hashtable hashtable = new Hashtable();
        this.constants = (Hashtable) this.init_constants.clone();
        if (paramVector != null)
            doParams(paramVector);
        this.body.compose(this, vector1, hashtable);
        Vector vector2 = new Vector();
        for (Enumeration enumeration = vector1.elements(); enumeration.hasMoreElements();) {
            CompositeState compositeState1 = (CompositeState) enumeration.nextElement();
            if (compositeState1 instanceof CompactState) {
                vector2.addElement(compositeState1);
                continue;
            }
            CompositeState compositeState2 = compositeState1;
            for (Enumeration enumeration1 = compositeState2.machines.elements(); enumeration1.hasMoreElements();)
                vector2.addElement(enumeration1.nextElement());
        }
        String str = (paramVector == null) ? this.name.toString() : (this.name.toString() + StateMachine.paramString(paramVector));
        CompositeState compositeState = new CompositeState(str, vector2);
        compositeState.priorityIsLow = this.priorityIsLow;
        compositeState.priorityLabels = computeAlphabet(this.priorityActions);
        compositeState.hidden = computeAlphabet(this.alphaHidden);
        compositeState.exposeNotHide = this.exposeNotHide;
        compositeState.makeDeterministic = this.makeDeterministic;
        compositeState.makeMinimal = this.makeMinimal;
        compositeState.makeCompose = this.makeCompose;
        if (this.makeProperty) {
            compositeState.makeDeterministic = true;
            compositeState.isProperty = true;
        }
        return compositeState;
    }

    private void doParams(Vector paramVector) {
        Enumeration enumeration1 = paramVector.elements();
        Enumeration enumeration2 = this.parameters.elements();
        while (enumeration1.hasMoreElements() && enumeration2.hasMoreElements())
            this.constants.put(enumeration2.nextElement(), enumeration1.nextElement());
    }

    private Vector computeAlphabet(LabelSet paramLabelSet) {
        if (paramLabelSet == null)
            return null;
        return paramLabelSet.getActions(this.constants);
    }
}
