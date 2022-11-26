package ic.doc.ltsa.lts;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

class ProcessRef {
    Symbol name;
    Vector actualParams;

    public void instantiate(CompositionExpression paramCompositionExpression, Vector paramVector, LTSOutput paramLTSOutput, Hashtable paramHashtable) {
        CompositeState compositeState;
        Vector vector = paramValues(paramHashtable, paramCompositionExpression);
        String str = (vector == null) ? this.name.toString() : (this.name.toString() + StateMachine.paramString(vector));
        CompactState compactState = (CompactState) paramCompositionExpression.compiledProcesses.get(str);
        if (compactState != null) {
            paramVector.addElement(compactState.myclone());
            return;
        }
        ProcessSpec processSpec = (ProcessSpec) paramCompositionExpression.processes.get(this.name.toString());
        if (processSpec != null) {
            if (this.actualParams != null && this.actualParams.size() != processSpec.parameters.size())
                Diagnostics.fatal("actuals do not match formal parameters", this.name);
            if (!processSpec.imported()) {
                StateMachine stateMachine = new StateMachine(processSpec, vector);
                compactState = stateMachine.makeCompactState();
            } else {
                compactState = new AutCompactState(processSpec.name, processSpec.importFile);
            }
            paramVector.addElement(compactState.myclone());
            paramCompositionExpression.compiledProcesses.put(compactState.name, compactState);
            if (!processSpec.imported()) {
                paramCompositionExpression.output.outln("Compiled: " + compactState.name);
            } else {
                paramCompositionExpression.output.outln("Imported: " + compactState.name);
            }
            return;
        }
        CompositionExpression compositionExpression = (CompositionExpression) paramCompositionExpression.composites.get(this.name.toString());
        if (compositionExpression == null)
            Diagnostics.fatal("definition not found- " + this.name, this.name);
        if (this.actualParams != null && this.actualParams.size() != compositionExpression.parameters.size())
            Diagnostics.fatal("actuals do not match formal parameters", this.name);
        if (compositionExpression == paramCompositionExpression) {
            Hashtable hashtable = (Hashtable) paramCompositionExpression.constants.clone();
            compositeState = compositionExpression.compose(vector);
            paramCompositionExpression.constants = hashtable;
        } else {
            compositeState = compositionExpression.compose(vector);
        }
        if (compositeState.needNotCreate()) {
            for (Enumeration enumeration = compositeState.machines.elements(); enumeration.hasMoreElements();) {
                compactState = enumeration.nextElement();
                compactState.name = compositeState.name + "." + compactState.name;
            }
            paramVector.addElement(compositeState);
        } else {
            compactState = compositeState.create(paramLTSOutput);
            paramCompositionExpression.compiledProcesses.put(compactState.name, compactState);
            paramCompositionExpression.output.outln("Compiled: " + compactState.name);
            paramVector.addElement(compactState.myclone());
        }
    }

    private Vector paramValues(Hashtable paramHashtable, CompositionExpression paramCompositionExpression) {
        if (this.actualParams == null)
            return null;
        Enumeration enumeration = this.actualParams.elements();
        Vector vector = new Vector();
        while (enumeration.hasMoreElements()) {
            Stack stack = enumeration.nextElement();
            vector.addElement(Expression.getValue(stack, paramHashtable, paramCompositionExpression.constants));
        }
        return vector;
    }
}
