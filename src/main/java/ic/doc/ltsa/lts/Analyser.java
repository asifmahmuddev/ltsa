package ic.doc.ltsa.lts;

import ic.doc.extension.Animator;
import ic.doc.extension.WebAnimator;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class Analyser implements Animator, Automata, WebAnimator {
    private CompositeState cs;
    private CompactState[] sm;
    private LTSOutput output;
    private Hashtable alphabet = new Hashtable();
    private Hashtable actionMap = new Hashtable();
    private int[] actionCount;
    private String[] actionName;
    private int Nmach;
    private int[] Mbase;
    private MyHashStack analysed;
    private int stateNo = 0;
    private int stateCount = 0;
    private boolean[] violated;
    private boolean deadlockDetected = false;
    private static final int SUCCESS = 0;
    private static final int DEADLOCK = 1;
    private static final int ERROR = 2;
    private static final int FOUND = 3;
    private EventManager eman;
    private boolean lowpriority = true;
    private Vector priorLabels = null;
    private BitSet highAction = null;
    private int acceptEvent = -1;
    private StateCodec coder;
    private boolean canTerminate = false;
    public static boolean partialOrderReduction = false;
    public static boolean preserveObsEquiv = true;
    private PartialOrder partial = null;
    private MyList compTrans;
    private int endSequence;
    LinkedList trace;
    int errorMachine;
    private String[] menuAlpha;
    private Hashtable actionToIndex;
    private Hashtable indexToAction;
    private int[] currentA;
    private volatile List choices;
    private boolean errorState;
    private Enumeration _replay;
    private String _replayAction;
    int theChoice;
    Random rand;

    public CompactState compose() {
        return private_compose(true);
    }

    public CompactState composeNoHide() {
        return private_compose(false);
    }

    private CompactState private_compose(boolean paramBoolean) {
        this.output.outln("Composing...");
        long l1 = System.currentTimeMillis();
        int i = newState_compose();
        CompactState compactState = new CompactState(this.stateCount, this.cs.name, this.analysed, this.compTrans, this.actionName, this.endSequence);
        if (paramBoolean && this.cs.hidden != null)
            if (!this.cs.exposeNotHide) {
                compactState.conceal(this.cs.hidden);
            } else {
                compactState.expose(this.cs.hidden);
            }
        long l2 = System.currentTimeMillis();
        outStatistics(this.stateCount, this.compTrans.size());
        this.output.outln("Composed in " + (l2 - l1) + "ms");
        this.analysed = null;
        this.compTrans = null;
        return compactState;
    }

    public void analyse() {
        this.output.outln("Analysing...");
        System.gc();
        long l1 = System.currentTimeMillis();
        int i = newState_analyse(this.coder.zero(), null);
        long l2 = System.currentTimeMillis();
        if (i == 1) {
            this.output.outln("Trace to DEADLOCK:");
            printPath(this.trace);
        } else if (i == 2) {
            this.output.outln("Trace to property violation in " + (this.sm[this.errorMachine]).name + ":");
            printPath(this.trace);
        } else {
            this.output.outln("No deadlocks/errors");
        }
        this.output.outln("Analysed in: " + (l2 - l1) + "ms");
    }

    public List getErrorTrace() {
        return this.trace;
    }

    private int countSet(BitSet paramBitSet) {
        byte b1 = 0;
        for (byte b2 = 0; b2 < paramBitSet.size(); b2++) {
            if (paramBitSet.get(b2))
                b1++;
        }
        return b1;
    }

    private boolean isEND(int[] paramArrayOfint) {
        if (!this.canTerminate)
            return false;
        for (byte b = 0; b < this.Nmach; b++) {
            if ((this.sm[b]).endseq >= 0)
                if ((this.sm[b]).endseq != paramArrayOfint[b])
                    return false;
        }
        return true;
    }

    private void printState(int[] paramArrayOfint) {
        this.output.out("[");
        for (byte b = 0; b < paramArrayOfint.length; b++) {
            this.output.out("" + paramArrayOfint[b]);
            if (b < paramArrayOfint.length - 1)
                this.output.out(",");
        }
        this.output.out("]");
    }

    private int[] myclone(int[] paramArrayOfint) {
        int[] arrayOfInt = new int[paramArrayOfint.length];
        for (byte b = 0; b < paramArrayOfint.length;) {
            arrayOfInt[b] = paramArrayOfint[b];
            b++;
        }
        return arrayOfInt;
    }

    List eligibleTransitions(int[] paramArrayOfint) {
        if (this.partial != null) {
            List list = this.partial.transitions(paramArrayOfint);
            if (list != null)
                return list;
        }
        int[] arrayOfInt = myclone(this.actionCount);
        EventState[] arrayOfEventState = new EventState[this.actionCount.length];
        byte b1 = 0;
        byte b2 = 0;
        for (byte b3 = 0; b3 < this.Nmach; b3++) {
            EventState eventState = (this.sm[b3]).states[paramArrayOfint[b3]];
            while (eventState != null) {
                EventState eventState1 = eventState;
                eventState1.path = arrayOfEventState[eventState1.event];
                arrayOfEventState[eventState1.event] = eventState1;
                arrayOfInt[eventState1.event] = arrayOfInt[eventState1.event] - 1;
                if (eventState1.event != 0 && arrayOfInt[eventState1.event] == 0) {
                    b1++;
                    if (this.highAction != null && this.highAction.get(eventState1.event))
                        b2++;
                }
                eventState = eventState.list;
            }
        }
        if (b1 == 0 && arrayOfEventState[0] == null)
            return null;
        byte b4 = 1;
        ArrayList arrayList = new ArrayList(8);
        if (arrayOfEventState[0] != null) {
            boolean bool = (this.highAction != null && this.highAction.get(0)) ? true : false;
            if (bool || b2 == 0)
                computeTauTransitions(arrayOfEventState[0], paramArrayOfint, arrayList);
            if (bool)
                b2++;
        }
        while (b1 > 0) {
            b1--;
            for (; arrayOfInt[b4] > 0; b4++);
            if (b2 <= 0 || this.highAction.get(b4) || b4 == this.acceptEvent) {
                EventState eventState = arrayOfEventState[b4];
                boolean bool = false;
                while (eventState != null) {
                    if (eventState.nondet != null) {
                        bool = true;
                        break;
                    }
                    eventState = eventState.path;
                }
                eventState = arrayOfEventState[b4];
                if (!bool) {
                    int[] arrayOfInt1 = myclone(paramArrayOfint);
                    arrayOfInt1[this.Nmach] = b4;
                    while (eventState != null) {
                        arrayOfInt1[eventState.machine] = eventState.next;
                        eventState = eventState.path;
                    }
                    arrayList.add(arrayOfInt1);
                } else {
                    computeNonDetTransitions(eventState, paramArrayOfint, arrayList);
                }
            }
            arrayOfInt[b4] = arrayOfInt[b4] + 1;
        }
        return arrayList;
    }

    private void outStatistics(int paramInt1, int paramInt2) {
        Runtime runtime = Runtime.getRuntime();
        this.output.outln("-- States: " + paramInt1 + " Transitions: " + paramInt2 + " Memory used: " + ((runtime.totalMemory() - runtime.freeMemory()) / 1000L) + "K");
    }

    public Analyser(CompositeState paramCompositeState, LTSOutput paramLTSOutput, EventManager paramEventManager) {
        this.endSequence = -99999;
        this.errorState = false;
        this._replay = null;
        this._replayAction = null;
        this.theChoice = 0;
        this.rand = new Random();
        this.cs = paramCompositeState;
        this.output = paramLTSOutput;
        this.eman = paramEventManager;
        if (paramCompositeState.priorityLabels != null) {
            this.lowpriority = paramCompositeState.priorityIsLow;
            this.priorLabels = paramCompositeState.priorityLabels;
            this.highAction = new BitSet();
        }
        this.sm = new CompactState[paramCompositeState.machines.size()];
        this.violated = new boolean[paramCompositeState.machines.size()];
        Enumeration enumeration = paramCompositeState.machines.elements();
        for (byte b1 = 0; enumeration.hasMoreElements(); b1++)
            this.sm[b1] = ((CompactState) enumeration.nextElement()).myclone();
        this.Nmach = this.sm.length;
        paramLTSOutput.outln("Composition:");
        paramLTSOutput.out(paramCompositeState.name + " = ");
        for (byte b2 = 0; b2 < this.sm.length; b2++) {
            paramLTSOutput.out((this.sm[b2]).name);
            if (b2 < this.sm.length - 1)
                paramLTSOutput.out(" || ");
        }
        paramLTSOutput.outln("");
        if (this.priorLabels != null) {
            if (this.lowpriority) {
                paramLTSOutput.out("\t>> ");
            } else {
                paramLTSOutput.out("\t<< ");
            }
            paramLTSOutput.outln((new Alphabet(paramCompositeState.priorityLabels)).toString());
        }
        this.Mbase = new int[this.Nmach];
        paramLTSOutput.outln("State Space:");
        for (byte b3 = 0; b3 < this.sm.length; b3++) {
            paramLTSOutput.out(" " + (this.sm[b3]).maxStates + " ");
            if (b3 < this.sm.length - 1)
                paramLTSOutput.out("*");
            this.Mbase[b3] = (this.sm[b3]).maxStates;
        }
        this.coder = new StateCodec(this.Mbase);
        paramLTSOutput.outln("= 2 ** " + this.coder.bits());
        HashSet hashSet1 = new HashSet();
        HashSet hashSet2 = new HashSet();
        Counter counter = new Counter(0);
        for (byte b4 = 0; b4 < this.sm.length; b4++) {
            for (byte b = 0; b < (this.sm[b4]).alphabet.length; b++) {
                if ((this.sm[b4]).endseq > 0) {
                    hashSet1.add((this.sm[b4]).alphabet[b]);
                } else {
                    hashSet2.add((this.sm[b4]).alphabet[b]);
                }
                BitSet bitSet = (BitSet) this.alphabet.get((this.sm[b4]).alphabet[b]);
                if (bitSet == null) {
                    bitSet = new BitSet();
                    bitSet.set(b4);
                    String str = (this.sm[b4]).alphabet[b];
                    this.alphabet.put(str, bitSet);
                    this.actionMap.put(str, counter.label());
                } else {
                    bitSet.set(b4);
                }
            }
        }
        this.canTerminate = hashSet1.containsAll(hashSet2);
        this.actionName = new String[this.alphabet.size()];
        this.actionCount = new int[this.alphabet.size()];
        enumeration = this.alphabet.keys();
        while (enumeration.hasMoreElements()) {
            String str = (String) enumeration.nextElement();
            BitSet bitSet = (BitSet) this.alphabet.get(str);
            int i = ((Integer) this.actionMap.get(str)).intValue();
            this.actionName[i] = str;
            this.actionCount[i] = countSet(bitSet);
            if (str.charAt(0) == '@')
                this.acceptEvent = i;
            if (this.highAction != null) {
                if (!this.lowpriority) {
                    if (CompactState.contains(str, this.priorLabels))
                        this.highAction.set(i);
                    continue;
                }
                if (!CompactState.contains(str, this.priorLabels))
                    this.highAction.set(i);
            }
        }
        if (this.highAction != null) {
            if (this.lowpriority) {
                this.highAction.set(0);
            } else {
                this.highAction.clear(0);
            }
            if (this.acceptEvent > 0)
                this.highAction.clear(this.acceptEvent);
        }
        this.actionCount[0] = 0;
        for (byte b5 = 0; b5 < this.sm.length; b5++) {
            for (byte b = 0; b < (this.sm[b5]).maxStates; b++) {
                EventState eventState = (this.sm[b5]).states[b];
                while (eventState != null) {
                    EventState eventState1 = eventState;
                    eventState1.machine = b5;
                    eventState1.event = ((Integer) this.actionMap.get((this.sm[b5]).alphabet[eventState1.event])).intValue();
                    while (eventState1.nondet != null) {
                        eventState1.nondet.event = eventState1.event;
                        eventState1.nondet.machine = eventState1.machine;
                        eventState1 = eventState1.nondet;
                    }
                    eventState = eventState.list;
                }
            }
        }
    }

    private int newState_compose() {
        System.gc();
        this.analysed = new MyHashStack(100001);
        if (partialOrderReduction)
            this.partial = new PartialOrder(this.alphabet, this.actionName, this.sm, new StackChecker(this.coder, this.analysed), this.cs.hidden, this.cs.exposeNotHide, preserveObsEquiv,
                this.highAction);
        this.compTrans = new MyList();
        this.stateCount = 0;
        this.analysed.pushPut(this.coder.zero());
        while (!this.analysed.empty()) {
            if (this.analysed.marked()) {
                this.analysed.pop();
                continue;
            }
            int[] arrayOfInt = this.coder.decode(this.analysed.peek());
            this.analysed.mark(this.stateCount++);
            if (this.stateCount % 10000 == 0) {
                this.output.out("Depth " + this.analysed.getDepth() + " ");
                outStatistics(this.stateCount, this.compTrans.size());
            }
            List list = eligibleTransitions(arrayOfInt);
            if (list == null) {
                if (!isEND(arrayOfInt)) {
                    if (!this.deadlockDetected)
                        this.output.outln("  potential DEADLOCK");
                    this.deadlockDetected = true;
                    continue;
                }
                if (this.endSequence < 0) {
                    this.endSequence = this.stateCount - 1;
                    continue;
                }
                this.analysed.mark(this.endSequence);
                this.stateCount--;
                continue;
            }
            Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                int[] arrayOfInt1 = iterator.next();
                byte[] arrayOfByte = this.coder.encode(arrayOfInt1);
                this.compTrans.add(this.stateCount - 1, arrayOfByte, arrayOfInt1[this.Nmach]);
                if (arrayOfByte == null) {
                    byte b;
                    for (b = 0; arrayOfInt1[b] >= 0; b++);
                    if (!this.violated[b])
                        this.output.outln("  property " + (this.sm[b]).name + " violation.");
                    this.violated[b] = true;
                    continue;
                }
                if (!this.analysed.containsKey(arrayOfByte))
                    this.analysed.pushPut(arrayOfByte);
            }
        }
        return 0;
    }

    private void printPath(LinkedList paramLinkedList) {
        Iterator iterator = paramLinkedList.iterator();
        while (iterator.hasNext())
            this.output.outln("\t" + (String) iterator.next());
    }

    private int newState_analyse(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
        this.stateCount = 0;
        byte b = 0;
        MyHashQueue myHashQueue = new MyHashQueue(100001);
        if (partialOrderReduction)
            this.partial = new PartialOrder(this.alphabet, this.actionName, this.sm, new StackChecker(this.coder, myHashQueue), this.cs.hidden, this.cs.exposeNotHide, false, this.highAction);
        myHashQueue.addPut(paramArrayOfbyte1, 0, null);
        Integer integer = new Integer(-1);
        Runtime runtime = Runtime.getRuntime();
        MyHashQueueEntry myHashQueueEntry = null;
        while (!myHashQueue.empty()) {
            myHashQueueEntry = myHashQueue.peek();
            paramArrayOfbyte1 = myHashQueueEntry.key;
            int[] arrayOfInt = this.coder.decode(paramArrayOfbyte1);
            this.stateCount++;
            if (this.stateCount % 10000 == 0) {
                this.output.out("Depth " + myHashQueue.depth(myHashQueueEntry) + " ");
                outStatistics(this.stateCount, b);
            }
            List list = eligibleTransitions(arrayOfInt);
            myHashQueue.pop();
            if (list == null) {
                if (!isEND(arrayOfInt)) {
                    this.output.out("Depth " + myHashQueue.depth(myHashQueueEntry) + " ");
                    outStatistics(this.stateCount, b);
                    this.trace = myHashQueue.getPath(myHashQueueEntry, this.actionName);
                    return 1;
                }
                continue;
            }
            Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                int[] arrayOfInt1 = iterator.next();
                byte[] arrayOfByte = this.coder.encode(arrayOfInt1);
                b++;
                if (arrayOfByte == null || StateCodec.equals(arrayOfByte, paramArrayOfbyte2)) {
                    this.output.out("Depth " + myHashQueue.depth(myHashQueueEntry) + " ");
                    outStatistics(this.stateCount, b);
                    if (arrayOfByte == null) {
                        byte b1;
                        for (b1 = 0; arrayOfInt1[b1] >= 0; b1++);
                        this.errorMachine = b1;
                    }
                    this.trace = myHashQueue.getPath(myHashQueueEntry, this.actionName);
                    this.trace.add(this.actionName[arrayOfInt1[this.Nmach]]);
                    if (arrayOfByte == null)
                        return 2;
                    return 3;
                }
                if (!myHashQueue.containsKey(arrayOfByte))
                    myHashQueue.addPut(arrayOfByte, arrayOfInt1[this.Nmach], myHashQueueEntry);
            }
        }
        this.output.out("Depth " + myHashQueue.depth(myHashQueueEntry) + " ");
        outStatistics(this.stateCount, b);
        return 0;
    }

    private void computeTauTransitions(EventState paramEventState, int[] paramArrayOfint, List paramList) {
        EventState eventState = paramEventState;
        while (eventState != null) {
            EventState eventState1 = eventState;
            while (eventState1 != null) {
                int[] arrayOfInt = myclone(paramArrayOfint);
                arrayOfInt[eventState1.machine] = eventState1.next;
                arrayOfInt[this.Nmach] = 0;
                paramList.add(arrayOfInt);
                eventState1 = eventState1.nondet;
            }
            eventState = eventState.path;
        }
    }

    private void computeNonDetTransitions(EventState paramEventState, int[] paramArrayOfint, List paramList) {
        EventState eventState = paramEventState;
        while (eventState != null) {
            int[] arrayOfInt = myclone(paramArrayOfint);
            arrayOfInt[eventState.machine] = eventState.next;
            if (paramEventState.path != null) {
                computeNonDetTransitions(paramEventState.path, arrayOfInt, paramList);
            } else {
                arrayOfInt[this.Nmach] = paramEventState.event;
                paramList.add(arrayOfInt);
            }
            eventState = eventState.nondet;
        }
    }

    public String[] getAlphabet() {
        return this.actionName;
    }

    public MyList getTransitions(byte[] paramArrayOfbyte) {
        List list = eligibleTransitions(this.coder.decode(paramArrayOfbyte));
        MyList myList = new MyList();
        if (list == null)
            return myList;
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            int[] arrayOfInt = iterator.next();
            byte[] arrayOfByte = this.coder.encode(arrayOfInt);
            if (arrayOfByte == null) {
                byte b;
                for (b = 0; arrayOfInt[b] >= 0; b++);
                this.errorMachine = b;
            }
            myList.add(0, arrayOfByte, arrayOfInt[this.Nmach]);
        }
        return myList;
    }

    public String getViolatedProperty() {
        return (this.sm[this.errorMachine]).name;
    }

    public Vector getTraceToState(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
        if (StateCodec.equals(paramArrayOfbyte1, paramArrayOfbyte2))
            return new Vector();
        int i = newState_analyse(paramArrayOfbyte1, paramArrayOfbyte2);
        if (i == 3) {
            Vector vector = new Vector();
            vector.addAll(this.trace);
            return vector;
        }
        return null;
    }

    public boolean END(byte[] paramArrayOfbyte) {
        return isEND(this.coder.decode(paramArrayOfbyte));
    }

    public byte[] START() {
        return this.coder.zero();
    }

    public void setStackChecker(StackCheck paramStackCheck) {
        if (partialOrderReduction)
            this.partial = new PartialOrder(this.alphabet, this.actionName, this.sm, new StackChecker(this.coder, paramStackCheck), this.cs.hidden, this.cs.exposeNotHide, false, this.highAction);
    }

    public boolean isPartialOrder() {
        return partialOrderReduction;
    }

    public void disablePartialOrder() {
        this.partial = null;
    }

    private void getMenuHash() {
        this.actionToIndex = new Hashtable();
        this.indexToAction = new Hashtable();
        for (byte b = 1; b < this.menuAlpha.length; b++) {
            Integer integer1 = new Integer(b);
            Integer integer2 = (Integer) this.actionMap.get(this.menuAlpha[b]);
            this.actionToIndex.put(integer2, integer1);
            this.indexToAction.put(integer1, integer2);
        }
    }

    private void getMenu(Vector paramVector) {
        if (paramVector != null) {
            Vector vector = new Vector();
            Enumeration enumeration = paramVector.elements();
            while (enumeration.hasMoreElements()) {
                String str = enumeration.nextElement();
                if (this.alphabet.containsKey(str))
                    vector.addElement(str);
            }
            this.menuAlpha = new String[vector.size() + 1];
            this.menuAlpha[0] = "tau";
            for (byte b = 1; b < this.menuAlpha.length; b++)
                this.menuAlpha[b] = vector.elementAt(b - 1);
        } else {
            this.menuAlpha = this.actionName;
        }
        getMenuHash();
    }

    private BitSet menuActions() {
        BitSet bitSet = new BitSet(this.menuAlpha.length);
        if (this.choices != null) {
            Iterator iterator = this.choices.iterator();
            while (iterator.hasNext()) {
                int[] arrayOfInt = iterator.next();
                Integer integer1 = new Integer(arrayOfInt[this.Nmach]);
                Integer integer2 = (Integer) this.actionToIndex.get(integer1);
                if (integer2 != null)
                    bitSet.set(integer2.intValue());
            }
        }
        return bitSet;
    }

    private BitSet allActions() {
        BitSet bitSet = new BitSet(this.actionCount.length);
        if (this.choices != null) {
            Iterator iterator = this.choices.iterator();
            while (iterator.hasNext()) {
                int[] arrayOfInt = iterator.next();
                bitSet.set(arrayOfInt[this.Nmach]);
            }
        }
        return bitSet;
    }

    public BitSet initialise(Vector paramVector) {
        this.choices = eligibleTransitions(this.currentA = this.coder.decode(this.coder.zero()));
        if (this.eman != null)
            this.eman.post(new LTSEvent(0, this.currentA));
        getMenu(paramVector);
        if (this.cs.getErrorTrace() != null) {
            this._replay = this.cs.getErrorTrace().elements();
            if (this._replay.hasMoreElements())
                this._replayAction = this._replay.nextElement();
        }
        return menuActions();
    }

    public BitSet singleStep() {
        if (this.errorState)
            return null;
        if (nonMenuChoice()) {
            this.currentA = step(randomNonMenuChoice());
            if (this.errorState)
                return null;
            this.choices = eligibleTransitions(this.currentA);
        }
        return menuActions();
    }

    public BitSet menuStep(int paramInt) {
        if (this.errorState)
            return null;
        this.theChoice = ((Integer) this.indexToAction.get(new Integer(paramInt))).intValue();
        this.currentA = step(this.theChoice);
        if (this.errorState)
            return null;
        this.choices = eligibleTransitions(this.currentA);
        return menuActions();
    }

    public int actionChosen() {
        return this.theChoice;
    }

    public String actionNameChosen() {
        return this.actionName[this.theChoice];
    }

    public boolean nonMenuChoice() {
        if (this.errorState)
            return false;
        BitSet bitSet = allActions();
        for (byte b = 0; b < bitSet.size(); b++) {
            if (bitSet.get(b) && !this.actionToIndex.containsKey(new Integer(b))) {
                this.theChoice = b;
                return true;
            }
        }
        return false;
    }

    private int randomNonMenuChoice() {
        BitSet bitSet = allActions();
        ArrayList arrayList = new ArrayList(8);
        for (byte b = 0; b < bitSet.size(); b++) {
            Integer integer = new Integer(b);
            if (bitSet.get(b) && !this.actionToIndex.containsKey(integer))
                arrayList.add(integer);
        }
        int i = Math.abs(this.rand.nextInt()) % arrayList.size();
        this.theChoice = ((Integer) arrayList.get(i)).intValue();
        return this.theChoice;
    }

    public boolean traceChoice() {
        if (this.errorState)
            return false;
        if (this._replay == null)
            return false;
        if (this._replayAction != null) {
            int i = ((Integer) this.actionMap.get(this._replayAction)).intValue();
            BitSet bitSet = allActions();
            if (bitSet.get(i)) {
                this.theChoice = i;
                return true;
            }
        }
        return false;
    }

    public boolean hasErrorTrace() {
        return (this.cs.getErrorTrace() != null);
    }

    public BitSet traceStep() {
        if (this.errorState)
            return null;
        if (traceChoice()) {
            this.currentA = step(this.theChoice);
            if (this.errorState)
                return null;
            this.choices = eligibleTransitions(this.currentA);
            if (this._replay.hasMoreElements()) {
                this._replayAction = this._replay.nextElement();
            } else {
                this._replayAction = null;
            }
        }
        return menuActions();
    }

    public boolean isError() {
        return this.errorState;
    }

    public boolean isEnd() {
        return isEND(this.currentA);
    }

    private int[] thestep(int paramInt) {
        if (this.errorState)
            return this.currentA;
        if (this.choices == null) {
            this.output.outln("DEADLOCK");
            this.errorState = true;
            return this.currentA;
        }
        Iterator iterator = this.choices.iterator();
        while (iterator.hasNext()) {
            int[] arrayOfInt = iterator.next();
            if (arrayOfInt[this.Nmach] == paramInt) {
                arrayOfInt = nonDetSelect(arrayOfInt);
                this.errorState = (this.coder.encode(arrayOfInt) == null);
                if (this.errorState)
                    return arrayOfInt;
                return this.currentA = arrayOfInt;
            }
        }
        return this.currentA;
    }

    private int[] step(int paramInt) {
        int[] arrayOfInt = thestep(paramInt);
        if (this.eman != null)
            this.eman.post(new LTSEvent(0, arrayOfInt, this.actionName[paramInt]));
        return arrayOfInt;
    }

    int[] nonDetSelect(int[] paramArrayOfint) {
        int i = this.choices.indexOf(paramArrayOfint);
        int j = i + 1;
        while (j < this.choices.size() && paramArrayOfint[this.Nmach] == ((int[]) this.choices.get(j))[this.Nmach])
            j++;
        if (i + 1 == j)
            return paramArrayOfint;
        int k = i + Math.abs(this.rand.nextInt()) % (j - i);
        return this.choices.get(k);
    }

    public String[] getMenuNames() {
        return this.menuAlpha;
    }

    public String[] getAllNames() {
        return this.actionName;
    }

    public boolean getPriority() {
        return this.lowpriority;
    }

    public BitSet getPriorityActions() {
        if (this.priorLabels == null)
            return null;
        BitSet bitSet = new BitSet();
        for (byte b = 1; b < this.actionName.length; b++) {
            Integer integer = (Integer) this.actionToIndex.get(new Integer(b));
            if (integer != null && ((this.lowpriority && !this.highAction.get(b)) || (!this.lowpriority && this.highAction.get(b))))
                bitSet.set(integer.intValue());
        }
        return bitSet;
    }

    public void message(String paramString) {
        this.output.outln(paramString);
    }
}
