package ic.doc.ltsa.lts;

import ic.doc.extension.Relation;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

class StateMachine {
    String name;
    String kludgeName;
    Hashtable alphabet = new Hashtable();
    Vector hidden;
    Relation relabels;
    Hashtable explicit_states = new Hashtable();
    Hashtable constants;
    Counter eventLabel = new Counter(0);
    Counter stateLabel = new Counter(0);
    Vector transitions = new Vector();
    boolean isProperty = false;
    boolean isMinimal = false;
    boolean isDeterministic = false;
    boolean exposeNotHide = false;
    Hashtable sequentialInserts;
    Hashtable preInsertsLast;
    Hashtable preInsertsMach;
    Hashtable aliases = new Hashtable();
    public static LTSOutput output;

    public StateMachine(ProcessSpec paramProcessSpec, Vector paramVector) {
        this.name = paramProcessSpec.getname();
        if (paramVector != null) {
            paramProcessSpec.doParams(paramVector);
            this.kludgeName = this.name + paramString(paramVector);
        } else {
            this.kludgeName = this.name;
        }
        make(paramProcessSpec);
    }

    public StateMachine(ProcessSpec paramProcessSpec) {
        this.name = paramProcessSpec.getname();
        this.kludgeName = this.name;
        make(paramProcessSpec);
    }

    private void make(ProcessSpec paramProcessSpec) {
        this.constants = paramProcessSpec.constants;
        this.alphabet.put("tau", this.eventLabel.label());
        paramProcessSpec.explicitStates(this);
        paramProcessSpec.crunch(this);
        renumber();
        paramProcessSpec.transition(this);
        paramProcessSpec.addAlphabet(this);
        paramProcessSpec.relabelAlphabet(this);
        paramProcessSpec.hideAlphabet(this);
        this.isProperty = paramProcessSpec.isProperty;
        this.isMinimal = paramProcessSpec.isMinimal;
        this.isDeterministic = paramProcessSpec.isDeterministic;
        this.exposeNotHide = paramProcessSpec.exposeNotHide;
    }

    public CompactState makeCompactState() {
        CompactState compactState = new CompactState();
        compactState.name = this.kludgeName;
        compactState.maxStates = this.stateLabel.lastLabel().intValue();
        Integer integer = (Integer) this.explicit_states.get("END");
        if (integer != null)
            compactState.endseq = integer.intValue();
        compactState.alphabet = new String[this.alphabet.size()];
        Enumeration enumeration = this.alphabet.keys();
        while (enumeration.hasMoreElements()) {
            String str = enumeration.nextElement();
            int i = ((Integer) this.alphabet.get(str)).intValue();
            if (str.equals("@"))
                str = "@" + compactState.name;
            compactState.alphabet[i] = str;
        }
        compactState.states = new EventState[compactState.maxStates];
        enumeration = this.transitions.elements();
        while (enumeration.hasMoreElements()) {
            Transition transition = (Transition) enumeration.nextElement();
            int i = ((Integer) this.alphabet.get("" + transition.event)).intValue();
            compactState.states[transition.from] = EventState.add(compactState.states[transition.from], new EventState(i, transition.to));
        }
        if (this.sequentialInserts != null)
            compactState.expandSequential(this.sequentialInserts);
        if (this.relabels != null)
            compactState.relabel(this.relabels);
        if (this.hidden != null)
            if (!this.exposeNotHide) {
                compactState.conceal(this.hidden);
            } else {
                compactState.expose(this.hidden);
            }
        if (this.isProperty) {
            if (compactState.isNonDeterministic() || compactState.hasTau())
                Diagnostics.fatal("primitive property processes must be deterministic: " + this.name);
            compactState.makeProperty();
        }
        check_for_ERROR(compactState);
        compactState.reachable();
        if (this.isMinimal) {
            Minimiser minimiser = new Minimiser(compactState, output);
            compactState = minimiser.minimise();
        }
        if (this.isDeterministic) {
            Minimiser minimiser = new Minimiser(compactState, output);
            compactState = minimiser.trace_minimise();
        }
        return compactState;
    }

    void check_for_ERROR(CompactState paramCompactState) {
        Integer integer = (Integer) this.explicit_states.get(this.name);
        if (integer.intValue() == -1) {
            paramCompactState.states = new EventState[1];
            paramCompactState.maxStates = 1;
            paramCompactState.states[0] = EventState.add(paramCompactState.states[0], new EventState(0, -1));
        }
    }

    void addSequential(Integer paramInteger, CompactState paramCompactState) {
        if (this.sequentialInserts == null)
            this.sequentialInserts = new Hashtable();
        this.sequentialInserts.put(paramInteger, paramCompactState);
    }

    void preAddSequential(Integer paramInteger1, Integer paramInteger2, CompactState paramCompactState) {
        if (this.preInsertsLast == null)
            this.preInsertsLast = new Hashtable();
        if (this.preInsertsMach == null)
            this.preInsertsMach = new Hashtable();
        this.preInsertsLast.put(paramInteger1, paramInteger2);
        this.preInsertsMach.put(paramInteger1, paramCompactState);
    }

    private void insertSequential(int[] paramArrayOfint) {
        if (this.preInsertsMach == null)
            return;
        Enumeration enumeration = this.preInsertsMach.keys();
        while (enumeration.hasMoreElements()) {
            Integer integer1 = enumeration.nextElement();
            CompactState compactState = (CompactState) this.preInsertsMach.get(integer1);
            Integer integer2 = (Integer) this.preInsertsLast.get(integer1);
            Integer integer3 = new Integer(paramArrayOfint[integer1.intValue()]);
            compactState.offsetSeq(integer3.intValue(), (integer2.intValue() >= 0) ? paramArrayOfint[integer2.intValue()] : integer2.intValue());
            addSequential(integer3, compactState);
        }
    }

    private Integer number(Integer paramInteger, Counter paramCounter) {
        if (this.preInsertsMach == null)
            return paramCounter.label();
        CompactState compactState = (CompactState) this.preInsertsMach.get(paramInteger);
        if (compactState == null)
            return paramCounter.label();
        return paramCounter.interval(compactState.maxStates);
    }

    private void crunch(int paramInt, int[] paramArrayOfint) {
        int i = paramArrayOfint[paramInt];
        while (i >= 0 && i != paramArrayOfint[i])
            i = paramArrayOfint[i];
        paramArrayOfint[paramInt] = i;
    }

    private void renumber() {
        int[] arrayOfInt = new int[this.stateLabel.lastLabel().intValue()];
        for (byte b1 = 0; b1 < arrayOfInt.length; b1++)
            arrayOfInt[b1] = b1;
        Enumeration enumeration = this.aliases.keys();
        while (enumeration.hasMoreElements()) {
            Integer integer1 = enumeration.nextElement();
            Integer integer2 = (Integer) this.aliases.get(integer1);
            arrayOfInt[integer1.intValue()] = integer2.intValue();
        }
        for (byte b2 = 0; b2 < arrayOfInt.length; b2++)
            crunch(b2, arrayOfInt);
        Counter counter = new Counter(0);
        Hashtable hashtable = new Hashtable();
        for (byte b3 = 0; b3 < arrayOfInt.length; b3++) {
            Integer integer = new Integer(arrayOfInt[b3]);
            if (!hashtable.containsKey(integer)) {
                Integer integer1 = (arrayOfInt[b3] >= 0) ? number(integer, counter) : new Integer(-1);
                hashtable.put(integer, integer1);
                arrayOfInt[b3] = integer1.intValue();
            } else {
                Integer integer1 = (Integer) hashtable.get(integer);
                arrayOfInt[b3] = integer1.intValue();
            }
        }
        insertSequential(arrayOfInt);
        enumeration = this.explicit_states.keys();
        while (enumeration.hasMoreElements()) {
            String str = (String) enumeration.nextElement();
            Integer integer = (Integer) this.explicit_states.get(str);
            if (integer.intValue() >= 0)
                this.explicit_states.put(str, new Integer(arrayOfInt[integer.intValue()]));
        }
        this.stateLabel = counter;
    }

    public void print(LTSOutput paramLTSOutput) {
        paramLTSOutput.outln("PROCESS: " + this.name);
        paramLTSOutput.outln("ALPHABET:");
        Enumeration enumeration = this.alphabet.keys();
        while (enumeration.hasMoreElements()) {
            String str = enumeration.nextElement();
            paramLTSOutput.outln("\t" + this.alphabet.get(str) + "\t" + str);
        }
        paramLTSOutput.outln("EXPLICIT STATES:");
        enumeration = this.explicit_states.keys();
        while (enumeration.hasMoreElements()) {
            String str = enumeration.nextElement();
            paramLTSOutput.outln("\t" + this.explicit_states.get(str) + "\t" + str);
        }
        paramLTSOutput.outln("TRANSITIONS:");
        enumeration = this.transitions.elements();
        while (enumeration.hasMoreElements()) {
            Transition transition = (Transition) enumeration.nextElement();
            paramLTSOutput.outln("\t" + transition);
        }
    }

    static String paramString(Vector paramVector) {
        int i = paramVector.size() - 1;
        StringBuffer stringBuffer = new StringBuffer();
        Enumeration enumeration = paramVector.elements();
        stringBuffer.append("(");
        for (byte b = 0; b <= i; b++) {
            String str = enumeration.nextElement().toString();
            stringBuffer.append(str);
            if (b < i)
                stringBuffer.append(",");
        }
        stringBuffer.append(")");
        return stringBuffer.toString();
    }
}
