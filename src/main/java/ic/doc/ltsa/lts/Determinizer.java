package ic.doc.ltsa.lts;

import java.util.BitSet;
import java.util.Hashtable;
import java.util.Vector;

public class Determinizer {
    static final int TAU = 0;
    CompactState machine;
    LTSOutput output;
    Vector newStates;
    Vector stateSets;
    Hashtable map;
    int nextState;
    int currentState;

    public Determinizer(CompactState paramCompactState, LTSOutput paramLTSOutput) {
        this.machine = paramCompactState;
        this.output = paramLTSOutput;
    }

    public CompactState determine() {
        this.output.outln("make DFA(" + this.machine.name + ")");
        this.newStates = new Vector(this.machine.maxStates * 2);
        this.stateSets = new Vector(this.machine.maxStates * 2);
        this.map = new Hashtable(this.machine.maxStates * 2);
        this.nextState = 0;
        this.currentState = 0;
        BitSet bitSet = new BitSet();
        bitSet.set(0);
        addState(bitSet);
        while (this.currentState < this.nextState) {
            compute(this.currentState);
            this.currentState++;
        }
        return makeNewMachine();
    }

    protected void compute(int paramInt) {
        BitSet bitSet = this.stateSets.elementAt(paramInt);
        EventState eventState1 = null;
        EventState eventState2 = null;
        for (byte b = 0; b < bitSet.size(); b++) {
            if (bitSet.get(b))
                eventState1 = EventState.union(eventState1, this.machine.states[b]);
        }
        EventState eventState3 = eventState1;
        while (eventState3 != null) {
            int i;
            boolean bool = false;
            BitSet bitSet1 = new BitSet();
            if (eventState3.next != -1) {
                bitSet1.set(eventState3.next);
            } else {
                bool = true;
            }
            EventState eventState = eventState3.nondet;
            while (eventState != null) {
                if (eventState.next != -1) {
                    bitSet1.set(eventState.next);
                } else {
                    bool = true;
                }
                eventState = eventState.nondet;
            }
            if (bool) {
                i = -1;
            } else {
                i = addState(bitSet1);
            }
            eventState2 = EventState.add(eventState2, new EventState(eventState3.event, i));
            eventState3 = eventState3.list;
        }
        this.newStates.addElement(eventState2);
    }

    protected int addState(BitSet paramBitSet) {
        Integer integer = (Integer) this.map.get(paramBitSet);
        if (integer != null)
            return integer.intValue();
        this.map.put(paramBitSet, new Integer(this.nextState));
        this.stateSets.addElement(paramBitSet);
        this.nextState++;
        return this.nextState - 1;
    }

    protected CompactState makeNewMachine() {
        CompactState compactState = new CompactState();
        compactState.name = this.machine.name;
        compactState.alphabet = new String[this.machine.alphabet.length];
        for (byte b1 = 0; b1 < this.machine.alphabet.length;) {
            compactState.alphabet[b1] = this.machine.alphabet[b1];
            b1++;
        }
        compactState.maxStates = this.nextState;
        compactState.states = new EventState[compactState.maxStates];
        for (byte b2 = 0; b2 < compactState.maxStates; b2++)
            compactState.states[b2] = this.newStates.elementAt(b2);
        if (this.machine.endseq >= 0) {
            BitSet bitSet = new BitSet();
            bitSet.set(this.machine.endseq);
            Integer integer = (Integer) this.map.get(bitSet);
            if (integer != null)
                compactState.endseq = integer.intValue();
        }
        this.output.outln("DFA(" + this.machine.name + ") has " + compactState.maxStates + " states.");
        return compactState;
    }
}
