package ic.doc.ltsa.lts;

import java.util.BitSet;
import java.util.Hashtable;

public class Minimiser {
    static final int TAU = 0;
    BitSet[] E;
    BitSet[] A;
    EventState[] T;
    CompactState machine;
    LTSOutput output;

    public Minimiser(CompactState paramCompactState, LTSOutput paramLTSOutput) {
        this.machine = paramCompactState;
        this.output = paramLTSOutput;
    }

    private void initTau() {
        this.T = new EventState[this.machine.states.length];
        for (byte b = 0; b < this.T.length; b++)
            this.T[b] = EventState.reachableTau(this.machine.states, b);
    }

    private CompactState machTau(CompactState paramCompactState) {
        for (byte b1 = 0; b1 < paramCompactState.states.length; b1++)
            paramCompactState.states[b1] = EventState.tauAdd(paramCompactState.states[b1], this.T);
        for (byte b2 = 0; b2 < paramCompactState.states.length; b2++) {
            paramCompactState.states[b2] = EventState.union(paramCompactState.states[b2], this.T[b2]);
            paramCompactState.states[b2] = EventState.actionAdd(paramCompactState.states[b2], paramCompactState.states);
        }
        for (byte b3 = 0; b3 < paramCompactState.states.length; b3++)
            paramCompactState.states[b3] = EventState.add(paramCompactState.states[b3], new EventState(0, b3));
        this.output.out(".");
        return paramCompactState;
    }

    private CompactState removeTau(CompactState paramCompactState) {
        for (byte b = 0; b < paramCompactState.states.length; b++)
            paramCompactState.states[b] = EventState.removeTau(paramCompactState.states[b]);
        return paramCompactState;
    }

    private void initialise() {
        this.A = new BitSet[this.machine.maxStates];
        for (byte b1 = 0; b1 < this.A.length; b1++) {
            this.A[b1] = new BitSet(this.machine.alphabet.length);
            EventState.setActions(this.machine.states[b1], this.A[b1]);
        }
        this.E = new BitSet[this.machine.maxStates];
        for (byte b2 = 0; b2 < this.E.length; b2++)
            this.E[b2] = new BitSet(this.E.length);
        for (byte b3 = 0; b3 < this.E.length; b3++) {
            this.E[b3].set(b3);
            for (byte b = 0; b < b3; b++) {
                if (this.A[b3].equals(this.A[b])) {
                    this.E[b3].set(b);
                    this.E[b].set(b3);
                }
            }
        }
        this.output.out(".");
    }

    private void dominimise() {
        boolean bool = true;
        while (bool) {
            this.output.out(".");
            bool = false;
            for (byte b = 0; b < this.E.length; b++) {
                Thread.yield();
                for (byte b1 = 0; b1 < b; b1++) {
                    if (this.E[b].get(b1)) {
                        boolean bool1 = (is_equivalent(b, b1) && is_equivalent(b1, b)) ? true : false;
                        if (!bool1) {
                            bool = true;
                            this.E[b].clear(b1);
                            this.E[b1].clear(b);
                        }
                    }
                }
            }
        }
    }

    public CompactState minimise() {
        this.output.out(this.machine.name + " minimising");
        long l1 = System.currentTimeMillis();
        CompactState compactState1 = this.machine.myclone();
        if (this.machine.endseq >= 0) {
            int i = this.machine.endseq;
            this.machine.states[i] = EventState.add(this.machine.states[i], new EventState(this.machine.alphabet.length, i));
        }
        if (this.machine.hasTau()) {
            initTau();
            this.machine = machTau(this.machine);
            this.T = null;
        }
        initialise();
        dominimise();
        this.machine = compactState1;
        CompactState compactState2 = makeNewMachine();
        long l2 = System.currentTimeMillis();
        this.output.outln("");
        this.output.outln("Minimised States: " + compactState2.maxStates + " in " + (l2 - l1) + "ms");
        return compactState2;
    }

    public CompactState trace_minimise() {
        boolean bool = false;
        if (this.machine.hasTau()) {
            bool = true;
            this.output.out("Eliminating tau");
            initTau();
            this.machine = machTau(this.machine);
            this.machine = removeTau(this.machine);
            this.T = null;
        }
        if (bool || this.machine.isNonDeterministic()) {
            bool = true;
            Determinizer determinizer = new Determinizer(this.machine, this.output);
            this.machine = determinizer.determine();
        }
        if (bool)
            return minimise();
        return this.machine;
    }

    private boolean is_equivalent(int paramInt1, int paramInt2) {
        EventState eventState = this.machine.states[paramInt1];
        while (eventState != null) {
            EventState eventState1 = eventState;
            while (eventState1 != null) {
                if (!findSuccessor(paramInt2, eventState1))
                    return false;
                eventState1 = eventState1.nondet;
            }
            eventState = eventState.list;
        }
        return true;
    }

    private boolean findSuccessor(int paramInt, EventState paramEventState) {
        EventState eventState = this.machine.states[paramInt];
        for (; eventState.event != paramEventState.event; eventState = eventState.list);
        while (eventState != null) {
            if (paramEventState.next < 0) {
                if (eventState.next < 0)
                    return true;
            } else if (eventState.next >= 0 && this.E[paramEventState.next].get(eventState.next)) {
                return true;
            }
            eventState = eventState.nondet;
        }
        return false;
    }

    private CompactState makeNewMachine() {
        Hashtable hashtable1 = new Hashtable();
        Hashtable hashtable2 = new Hashtable();
        Counter counter = new Counter(0);
        for (byte b1 = 0; b1 < this.E.length; b1++) {
            Integer integer1 = new Integer(b1);
            Integer integer2 = (Integer) hashtable1.get(integer1);
            if (integer2 == null) {
                hashtable1.put(integer1, integer2 = counter.label());
                hashtable2.put(integer2, integer1);
            }
            for (byte b = 0; b < this.E.length; b++) {
                if (this.E[b1].get(b))
                    hashtable1.put(new Integer(b), integer2);
            }
        }
        CompactState compactState = new CompactState();
        compactState.name = this.machine.name;
        compactState.maxStates = hashtable2.size();
        compactState.alphabet = this.machine.alphabet;
        compactState.states = new EventState[compactState.maxStates];
        if (this.machine.endseq < 0) {
            compactState.endseq = this.machine.endseq;
        } else {
            compactState.endseq = ((Integer) hashtable1.get(new Integer(this.machine.endseq))).intValue();
            compactState.states[compactState.endseq] = EventState.remove(compactState.states[compactState.endseq], new EventState(compactState.alphabet.length, compactState.endseq));
        }
        for (byte b2 = 0; b2 < this.machine.maxStates; b2++) {
            int i = ((Integer) hashtable1.get(new Integer(b2))).intValue();
            EventState eventState = EventState.renumberStates(this.machine.states[b2], hashtable1);
            compactState.states[i] = EventState.union(compactState.states[i], eventState);
        }
        for (byte b3 = 0; b3 < compactState.maxStates; b3++)
            compactState.states[b3] = EventState.remove(compactState.states[b3], new EventState(0, b3));
        return compactState;
    }

    public void print(LTSOutput paramLTSOutput) {
        privPrint(paramLTSOutput, this.E);
    }

    private void privPrint(LTSOutput paramLTSOutput, BitSet[] paramArrayOfBitSet) {
        if (paramArrayOfBitSet.length > 20)
            return;
        char[] arrayOfChar = new char[paramArrayOfBitSet.length * 2];
        for (byte b1 = 0; b1 < paramArrayOfBitSet.length * 2;) {
            arrayOfChar[b1] = ' ';
            b1++;
        }
        paramLTSOutput.outln("E:");
        paramLTSOutput.out("       ");
        for (byte b2 = 0; b2 < paramArrayOfBitSet.length;) {
            paramLTSOutput.out(" " + b2);
            b2++;
        }
        paramLTSOutput.outln("");
        for (byte b3 = 0; b3 < paramArrayOfBitSet.length; b3++) {
            paramLTSOutput.out("State " + b3 + " ");
            for (byte b = 0; b < paramArrayOfBitSet.length; b++) {
                if (paramArrayOfBitSet[b3].get(b)) {
                    arrayOfChar[b * 2] = '1';
                } else {
                    arrayOfChar[b * 2] = ' ';
                }
            }
            paramLTSOutput.outln(new String(arrayOfChar));
        }
    }
}
