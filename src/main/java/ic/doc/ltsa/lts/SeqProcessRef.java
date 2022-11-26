package ic.doc.ltsa.lts;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

class SeqProcessRef {
    Symbol name;
    Vector actualParams;
    static LTSOutput output;

    SeqProcessRef(Symbol paramSymbol, Vector paramVector) {
        this.name = paramSymbol;
        this.actualParams = paramVector;
    }

    CompactState instantiate(Hashtable paramHashtable1, Hashtable paramHashtable2) {
        Vector vector = paramValues(paramHashtable1, paramHashtable2);
        String str = (vector == null) ? this.name.toString() : (this.name.toString() + StateMachine.paramString(vector));
        CompactState compactState = (CompactState) LTSCompiler.compiled.get(str);
        if (compactState == null) {
            ProcessSpec processSpec = (ProcessSpec) LTSCompiler.processes.get(this.name.toString());
            if (processSpec != null) {
                processSpec = processSpec.myclone();
                if (this.actualParams != null && this.actualParams.size() != processSpec.parameters.size())
                    Diagnostics.fatal("actuals do not match formal parameters", this.name);
                StateMachine stateMachine = new StateMachine(processSpec, vector);
                compactState = stateMachine.makeCompactState();
                output.outln("-- compiled:" + compactState.name);
            }
        }
        if (compactState == null) {
            CompositionExpression compositionExpression = (CompositionExpression) LTSCompiler.composites.get(this.name.toString());
            if (compositionExpression != null) {
                CompositeState compositeState = compositionExpression.compose(vector);
                compactState = compositeState.create(output);
            }
        }
        if (compactState != null) {
            LTSCompiler.compiled.put(compactState.name, compactState);
            if (!compactState.isSequential())
                Diagnostics.fatal("process is not sequential - " + this.name, this.name);
            return compactState.myclone();
        }
        Diagnostics.fatal("process definition not found- " + this.name, this.name);
        return null;
    }

    private Vector paramValues(Hashtable paramHashtable1, Hashtable paramHashtable2) {
        if (this.actualParams == null)
            return null;
        Enumeration enumeration = this.actualParams.elements();
        Vector vector = new Vector();
        while (enumeration.hasMoreElements()) {
            Stack stack = enumeration.nextElement();
            vector.addElement(Expression.getValue(stack, paramHashtable1, paramHashtable2));
        }
        return vector;
    }
}
