package ic.doc.ltsa.lts;

import java.util.Enumeration;
import java.util.Vector;

public class CounterExample {
    protected CompositeState mach;
    protected Vector errorTrace = null;

    public CounterExample(CompositeState paramCompositeState) {
        this.mach = paramCompositeState;
    }

    public void print(LTSOutput paramLTSOutput) {
        String str;
        EventState eventState = new EventState(0, 0);
        int i = EventState.search(eventState, this.mach.composition.states, 0, -1, this.mach.composition.endseq);
        this.errorTrace = null;
        switch (i) {
            case 1 :
                paramLTSOutput.outln("No deadlocks/errors");
                break;
            case 0 :
                paramLTSOutput.outln("Trace to DEADLOCK:");
                this.errorTrace = EventState.getPath(eventState.path, this.mach.composition.alphabet);
                printPath(paramLTSOutput, this.errorTrace);
                break;
            case -1 :
                this.errorTrace = EventState.getPath(eventState.path, this.mach.composition.alphabet);
                str = findComponent(this.errorTrace);
                paramLTSOutput.outln("Trace to property violation in " + str + ":");
                printPath(paramLTSOutput, this.errorTrace);
                break;
        }
    }

    private void printPath(LTSOutput paramLTSOutput, Vector paramVector) {
        Enumeration enumeration = paramVector.elements();
        while (enumeration.hasMoreElements())
            paramLTSOutput.outln("\t" + (String) enumeration.nextElement());
    }

    private String findComponent(Vector paramVector) {
        Enumeration enumeration = this.mach.machines.elements();
        while (enumeration.hasMoreElements()) {
            CompactState compactState = enumeration.nextElement();
            if (compactState.isErrorTrace(paramVector))
                return compactState.name;
        }
        return "?";
    }

    public Vector getErrorTrace() {
        return this.errorTrace;
    }
}
