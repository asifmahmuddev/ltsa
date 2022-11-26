package ic.doc.ltsa.lts;

import ic.doc.extension.Relation;
import java.io.PrintStream;
import java.util.BitSet;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class CompactState implements Automata {
    public String name;
    public int maxStates;
    public String[] alphabet;
    public EventState[] states;
    int endseq = -9999;
    private boolean hasduplicates;
    private boolean prop;

    public void reachable() {
        MyIntHash myIntHash = EventState.reachable(this.states);
        EventState[] arrayOfEventState = this.states;
        this.maxStates = myIntHash.size();
        this.states = new EventState[this.maxStates];
        for (byte b = 0; b < arrayOfEventState.length; b++) {
            int i = myIntHash.get(b);
            if (i > -2)
                this.states[i] = EventState.renumberStates(arrayOfEventState[b], myIntHash);
        }
        if (this.endseq > 0)
            this.endseq = myIntHash.get(this.endseq);
    }

    public void removeNonDetTau() {
        int i;
        if (!hasTau())
            return;
        do {
            boolean bool = false;
            for (byte b1 = 0; b1 < this.maxStates; b1++)
                this.states[b1] = EventState.remove(this.states[b1], new EventState(0, b1));
            BitSet bitSet = new BitSet(this.maxStates);
            for (byte b2 = 1; b2 < this.maxStates; b2++) {
                if (EventState.hasOnlyTauAndAccept(this.states[b2], this.alphabet)) {
                    bitSet.set(b2);
                    bool = true;
                }
            }
            if (!bool)
                return;
            for (byte b3 = 0; b3 < this.maxStates; b3++) {
                if (!bitSet.get(b3))
                    this.states[b3] = EventState.addNonDetTau(this.states[b3], this.states, bitSet);
            }
            i = this.maxStates;
            reachable();
        } while (i != this.maxStates);
    }

    public void removeAcceptTau() {
        for (byte b = 1; b < this.maxStates; b++) {
            if (EventState.hasOnlyTauAndAccept(this.states[b], this.alphabet))
                this.states[b] = EventState.removeAccept(this.states[b]);
        }
    }

    public void prefixLabels(String paramString) {
        this.name = paramString + ":" + this.name;
        for (byte b = 1; b < this.alphabet.length; b++) {
            String str = this.alphabet[b];
            this.alphabet[b] = paramString + "." + str;
        }
    }

    public CompactState() {
        this.hasduplicates = false;
        this.prop = false;
    }

    public CompactState(int paramInt1, String paramString, MyHashStack paramMyHashStack, MyList paramMyList, String[] paramArrayOfString, int paramInt2) {
        this.hasduplicates = false;
        this.prop = false;
        this.alphabet = paramArrayOfString;
        this.name = paramString;
        this.maxStates = paramInt1;
        this.states = new EventState[this.maxStates];
        while (!paramMyList.empty()) {
            int i = paramMyList.getFrom();
            boolean bool = (paramMyList.getTo() == null) ? true : paramMyHashStack.get(paramMyList.getTo());
            this.states[i] = EventState.add(this.states[i], new EventState(paramMyList.getAction(), bool));
            paramMyList.next();
        }
        this.endseq = paramInt2;
    }

    public boolean relabelDuplicates() {
        return this.hasduplicates;
    }

    public void relabel(Relation paramRelation) {
        this.hasduplicates = false;
        if (paramRelation.isRelation()) {
            relational_relabel(paramRelation);
        } else {
            functional_relabel((Hashtable) paramRelation);
        }
    }

    private void relational_relabel(Relation paramRelation) {
        Vector vector = new Vector();
        Relation relation = new Relation();
        vector.setSize(this.alphabet.length);
        int i = this.alphabet.length;
        vector.setElementAt(this.alphabet[0], 0);
        for (byte b = 1; b < this.alphabet.length; b++) {
            int j = -1;
            Object object = paramRelation.get(this.alphabet[b]);
            if (object != null) {
                if (object instanceof String) {
                    vector.setElementAt(object, b);
                } else {
                    Vector vector1 = (Vector) object;
                    vector.setElementAt(vector1.firstElement(), b);
                    for (byte b1 = 1; b1 < vector1.size(); b1++) {
                        vector.addElement(vector1.elementAt(b1));
                        relation.put(new Integer(b), new Integer(i));
                        i++;
                    }
                }
            } else if ((j = maximalPrefix(this.alphabet[b], (Hashtable) paramRelation)) >= 0) {
                String str = this.alphabet[b].substring(0, j);
                object = paramRelation.get(str);
                if (object != null) {
                    if (object instanceof String) {
                        vector.setElementAt((String) object + this.alphabet[b].substring(j), b);
                    } else {
                        Vector vector1 = (Vector) object;
                        vector.setElementAt((String) vector1.firstElement() + this.alphabet[b].substring(j), b);
                        for (byte b1 = 1; b1 < vector1.size(); b1++) {
                            vector.addElement((String) vector1.elementAt(b1) + this.alphabet[b].substring(j));
                            relation.put(new Integer(b), new Integer(i));
                            i++;
                        }
                    }
                } else {
                    vector.setElementAt(this.alphabet[b], b);
                }
            } else {
                vector.setElementAt(this.alphabet[b], b);
            }
        }
        String[] arrayOfString = new String[vector.size()];
        vector.copyInto((Object[]) arrayOfString);
        this.alphabet = arrayOfString;
        addtransitions(relation);
        checkDuplicates();
    }

    private void functional_relabel(Hashtable paramHashtable) {
        for (byte b = 1; b < this.alphabet.length; b++) {
            String str = (String) paramHashtable.get(this.alphabet[b]);
            if (str != null) {
                this.alphabet[b] = str;
            } else {
                this.alphabet[b] = prefixLabelReplace(b, paramHashtable);
            }
        }
        checkDuplicates();
    }

    private void checkDuplicates() {
        Hashtable hashtable = new Hashtable();
        for (byte b = 1; b < this.alphabet.length; b++) {
            if (hashtable.put(this.alphabet[b], this.alphabet[b]) != null) {
                this.hasduplicates = true;
                crunchDuplicates();
            }
        }
    }

    private void crunchDuplicates() {
        Hashtable hashtable1 = new Hashtable();
        Hashtable hashtable2 = new Hashtable();
        byte b1 = 0;
        for (byte b2 = 0; b2 < this.alphabet.length; b2++) {
            if (hashtable1.containsKey(this.alphabet[b2])) {
                hashtable2.put(new Integer(b2), hashtable1.get(this.alphabet[b2]));
            } else {
                hashtable1.put(this.alphabet[b2], new Integer(b1));
                hashtable2.put(new Integer(b2), new Integer(b1));
                b1++;
            }
        }
        this.alphabet = new String[hashtable1.size()];
        Enumeration enumeration = hashtable1.keys();
        while (enumeration.hasMoreElements()) {
            String str = enumeration.nextElement();
            int i = ((Integer) hashtable1.get(str)).intValue();
            this.alphabet[i] = str;
        }
        for (byte b3 = 0; b3 < this.states.length; b3++)
            this.states[b3] = EventState.renumberEvents(this.states[b3], hashtable2);
    }

    public Vector hide(Vector paramVector) {
        Vector vector = new Vector();
        for (byte b = 1; b < this.alphabet.length; b++) {
            if (!contains(this.alphabet[b], paramVector))
                vector.addElement(this.alphabet[b]);
        }
        return vector;
    }

    public void expose(Vector paramVector) {
        BitSet bitSet = new BitSet(this.alphabet.length);
        for (byte b = 1; b < this.alphabet.length; b++) {
            if (contains(this.alphabet[b], paramVector))
                bitSet.set(b);
        }
        bitSet.set(0);
        dohiding(bitSet);
    }

    public void conceal(Vector paramVector) {
        BitSet bitSet = new BitSet(this.alphabet.length);
        for (byte b = 1; b < this.alphabet.length; b++) {
            if (!contains(this.alphabet[b], paramVector))
                bitSet.set(b);
        }
        bitSet.set(0);
        dohiding(bitSet);
    }

    private void dohiding(BitSet paramBitSet) {
        Integer integer = new Integer(0);
        Hashtable hashtable = new Hashtable();
        Vector vector = new Vector();
        byte b1 = 0;
        for (byte b2 = 0; b2 < this.alphabet.length; b2++) {
            if (!paramBitSet.get(b2)) {
                hashtable.put(new Integer(b2), integer);
            } else {
                vector.addElement(this.alphabet[b2]);
                hashtable.put(new Integer(b2), new Integer(b1));
                b1++;
            }
        }
        this.alphabet = new String[vector.size()];
        vector.copyInto((Object[]) this.alphabet);
        for (byte b3 = 0; b3 < this.states.length; b3++)
            this.states[b3] = EventState.renumberEvents(this.states[b3], hashtable);
    }

    static boolean contains(String paramString, Vector paramVector) {
        Enumeration enumeration = paramVector.elements();
        while (enumeration.hasMoreElements()) {
            String str = enumeration.nextElement();
            if (str.equals(paramString) || isPrefix(str, paramString))
                return true;
        }
        return false;
    }

    public boolean isProperty() {
        return this.prop;
    }

    public void makeProperty() {
        this.endseq = -9999;
        this.prop = true;
        for (byte b = 0; b < this.maxStates; b++)
            this.states[b] = EventState.addTransToError(this.states[b], this.alphabet.length);
    }

    public boolean isNonDeterministic() {
        for (byte b = 0; b < this.maxStates; b++) {
            if (EventState.hasNonDet(this.states[b]))
                return true;
        }
        return false;
    }

    public void printAUT(PrintStream paramPrintStream) {
        paramPrintStream.print("des(0," + ntransitions() + "," + this.maxStates + ")\n");
        for (byte b = 0; b < this.states.length; b++)
            EventState.printAUT(this.states[b], b, this.alphabet, paramPrintStream);
    }

    public CompactState myclone() {
        CompactState compactState = new CompactState();
        compactState.name = this.name;
        compactState.endseq = this.endseq;
        compactState.prop = this.prop;
        compactState.alphabet = new String[this.alphabet.length];
        for (byte b1 = 0; b1 < this.alphabet.length;) {
            compactState.alphabet[b1] = this.alphabet[b1];
            b1++;
        }
        compactState.maxStates = this.maxStates;
        compactState.states = new EventState[this.maxStates];
        for (byte b2 = 0; b2 < this.maxStates; b2++)
            compactState.states[b2] = EventState.union(compactState.states[b2], this.states[b2]);
        return compactState;
    }

    public int ntransitions() {
        int i = 0;
        for (byte b = 0; b < this.states.length; b++)
            i += EventState.count(this.states[b]);
        return i;
    }

    public boolean hasTau() {
        for (byte b = 0; b < this.states.length; b++) {
            if (EventState.hasTau(this.states[b]))
                return true;
        }
        return false;
    }

    private String prefixLabelReplace(int paramInt, Hashtable paramHashtable) {
        int i = maximalPrefix(this.alphabet[paramInt], paramHashtable);
        if (i < 0)
            return this.alphabet[paramInt];
        String str1 = this.alphabet[paramInt].substring(0, i);
        String str2 = (String) paramHashtable.get(str1);
        if (str2 == null)
            return this.alphabet[paramInt];
        return str2 + this.alphabet[paramInt].substring(i);
    }

    private int maximalPrefix(String paramString, Hashtable paramHashtable) {
        int i = paramString.lastIndexOf('.');
        if (i < 0)
            return i;
        if (paramHashtable.containsKey(paramString.substring(0, i)))
            return i;
        return maximalPrefix(paramString.substring(0, i), paramHashtable);
    }

    private static boolean isPrefix(String paramString1, String paramString2) {
        int i = paramString2.lastIndexOf('.');
        if (i < 0)
            return false;
        if (paramString1.equals(paramString2.substring(0, i)))
            return true;
        return isPrefix(paramString1, paramString2.substring(0, i));
    }

    public boolean isErrorTrace(Vector paramVector) {
        boolean bool = false;
        for (byte b = 0; b < this.maxStates && !bool; b++) {
            if (EventState.hasState(this.states[b], -1))
                bool = true;
        }
        if (!bool)
            return false;
        return isTrace(paramVector, 0, 0);
    }

    private boolean isTrace(Vector paramVector, int paramInt1, int paramInt2) {
        if (paramInt1 < paramVector.size()) {
            String str = paramVector.elementAt(paramInt1);
            int i = eventNo(str);
            if (i < this.alphabet.length) {
                if (EventState.hasEvent(this.states[paramInt2], i)) {
                    int[] arrayOfInt = EventState.nextState(this.states[paramInt2], i);
                    for (byte b = 0; b < arrayOfInt.length; b++) {
                        if (isTrace(paramVector, paramInt1 + 1, arrayOfInt[b]))
                            return true;
                    }
                    return false;
                }
                if (i != 0)
                    return false;
            }
            return isTrace(paramVector, paramInt1 + 1, paramInt2);
        }
        return (paramInt2 == -1);
    }

    private int eventNo(String paramString) {
        byte b = 0;
        for (; b < this.alphabet.length && !paramString.equals(this.alphabet[b]); b++);
        return b;
    }

    public void addAccess(Vector paramVector) {
        int i = paramVector.size();
        if (i == 0)
            return;
        String str = "{";
        CompactState[] arrayOfCompactState = new CompactState[i];
        Enumeration enumeration = paramVector.elements();
        byte b1 = 0;
        while (enumeration.hasMoreElements()) {
            String str1 = enumeration.nextElement();
            str = str + str1;
            arrayOfCompactState[b1] = myclone();
            arrayOfCompactState[b1].prefixLabels(str1);
            b1++;
            if (b1 < i)
                str = str + ",";
        }
        this.name = str + "}::" + this.name;
        int j = this.alphabet.length - 1;
        this.alphabet = new String[j * i + 1];
        this.alphabet[0] = "tau";
        for (byte b2 = 0; b2 < i; b2++) {
            for (byte b = 1; b < (arrayOfCompactState[b2]).alphabet.length; b++)
                this.alphabet[j * b2 + b] = (arrayOfCompactState[b2]).alphabet[b];
        }
        for (byte b3 = 1; b3 < i; b3++) {
            for (byte b = 0; b < this.maxStates; b++) {
                EventState.offsetEvents((arrayOfCompactState[b3]).states[b], j * b3);
                this.states[b] = EventState.union(this.states[b], (arrayOfCompactState[b3]).states[b]);
            }
        }
    }

    private void addtransitions(Relation paramRelation) {
        for (byte b = 0; b < this.states.length; b++) {
            EventState eventState = EventState.newTransitions(this.states[b], paramRelation);
            if (eventState != null)
                this.states[b] = EventState.union(this.states[b], eventState);
        }
    }

    public boolean hasLabel(String paramString) {
        for (byte b = 0; b < this.alphabet.length; b++) {
            if (paramString.equals(this.alphabet[b]))
                return true;
        }
        return false;
    }

    public boolean usesLabel(String paramString) {
        if (!hasLabel(paramString))
            return false;
        int i = eventNo(paramString);
        for (byte b = 0; b < this.states.length; b++) {
            if (EventState.hasEvent(this.states[b], i))
                return true;
        }
        return false;
    }

    public boolean isSequential() {
        return (this.endseq >= 0);
    }

    public boolean isEnd() {
        return (this.maxStates == 1 && this.endseq == 0);
    }

    public static CompactState sequentialCompose(Vector paramVector) {
        if (paramVector == null)
            return null;
        if (paramVector.size() == 0)
            return null;
        if (paramVector.size() == 1)
            return paramVector.elementAt(0);
        CompactState[] arrayOfCompactState = new CompactState[paramVector.size()];
        arrayOfCompactState = paramVector.<CompactState>toArray(arrayOfCompactState);
        CompactState compactState = new CompactState();
        compactState.alphabet = sharedAlphabet(arrayOfCompactState);
        compactState.maxStates = seqSize(arrayOfCompactState);
        compactState.states = new EventState[compactState.maxStates];
        int i = 0;
        for (byte b = 0; b < arrayOfCompactState.length; b++) {
            boolean bool = (b == arrayOfCompactState.length - 1) ? true : false;
            copyOffset(i, compactState.states, arrayOfCompactState[b], bool);
            if (bool)
                (arrayOfCompactState[b]).endseq += i;
            i += (arrayOfCompactState[b]).states.length;
        }
        return compactState;
    }

    public void expandSequential(Hashtable paramHashtable) {
        int i = paramHashtable.size();
        CompactState[] arrayOfCompactState = new CompactState[i + 1];
        int[] arrayOfInt = new int[i + 1];
        arrayOfCompactState[0] = this;
        byte b1 = 1;
        Enumeration enumeration = paramHashtable.keys();
        while (enumeration.hasMoreElements()) {
            Integer integer = enumeration.nextElement();
            CompactState compactState = (CompactState) paramHashtable.get(integer);
            arrayOfCompactState[b1] = compactState;
            arrayOfInt[b1] = integer.intValue();
            b1++;
        }
        this.alphabet = sharedAlphabet(arrayOfCompactState);
        for (byte b2 = 1; b2 < arrayOfCompactState.length; b2++) {
            int j = arrayOfInt[b2];
            for (byte b = 0; b < (arrayOfCompactState[b2]).states.length; b++)
                this.states[j + b] = (arrayOfCompactState[b2]).states[b];
        }
    }

    private static int seqSize(CompactState[] paramArrayOfCompactState) {
        int i = 0;
        for (byte b = 0; b < paramArrayOfCompactState.length; b++)
            i += (paramArrayOfCompactState[b]).states.length;
        return i;
    }

    private static void copyOffset(int paramInt, EventState[] paramArrayOfEventState, CompactState paramCompactState, boolean paramBoolean) {
        for (byte b = 0; b < paramCompactState.states.length; b++) {
            if (!paramBoolean) {
                paramArrayOfEventState[b + paramInt] = EventState.offsetSeq(paramInt, paramCompactState.endseq, paramCompactState.maxStates + paramInt, paramCompactState.states[b]);
            } else {
                paramArrayOfEventState[b + paramInt] = EventState.offsetSeq(paramInt, paramCompactState.endseq, paramCompactState.endseq + paramInt, paramCompactState.states[b]);
            }
        }
    }

    public void offsetSeq(int paramInt1, int paramInt2) {
        for (byte b = 0; b < this.states.length; b++)
            EventState.offsetSeq(paramInt1, this.endseq, paramInt2, this.states[b]);
    }

    private static String[] sharedAlphabet(CompactState[] paramArrayOfCompactState) {
        Counter counter = new Counter(0);
        Hashtable hashtable = new Hashtable();
        for (byte b1 = 0; b1 < paramArrayOfCompactState.length; b1++) {
            for (byte b = 0; b < (paramArrayOfCompactState[b1]).alphabet.length; b++) {
                if (!hashtable.containsKey((paramArrayOfCompactState[b1]).alphabet[b]))
                    hashtable.put((paramArrayOfCompactState[b1]).alphabet[b], counter.label());
            }
        }
        String[] arrayOfString = new String[hashtable.size()];
        Enumeration enumeration = hashtable.keys();
        while (enumeration.hasMoreElements()) {
            String str = enumeration.nextElement();
            int i = ((Integer) hashtable.get(str)).intValue();
            arrayOfString[i] = str;
        }
        for (byte b2 = 0; b2 < paramArrayOfCompactState.length; b2++) {
            for (byte b = 0; b < (paramArrayOfCompactState[b2]).maxStates; b++) {
                EventState eventState = (paramArrayOfCompactState[b2]).states[b];
                while (eventState != null) {
                    EventState eventState1 = eventState;
                    eventState1.event = ((Integer) hashtable.get((paramArrayOfCompactState[b2]).alphabet[eventState1.event])).intValue();
                    while (eventState1.nondet != null) {
                        eventState1.nondet.event = eventState1.event;
                        eventState1 = eventState1.nondet;
                    }
                    eventState = eventState.list;
                }
            }
        }
        return arrayOfString;
    }

    private byte[] encode(int paramInt) {
        byte[] arrayOfByte = new byte[4];
        for (byte b = 0; b < 4; b++) {
            arrayOfByte[b] = (byte) (arrayOfByte[b] | (byte) paramInt);
            paramInt >>>= 8;
        }
        return arrayOfByte;
    }

    private int decode(byte[] paramArrayOfbyte) {
        int i = 0;
        for (byte b = 3; b >= 0; b--) {
            i |= paramArrayOfbyte[b] & 0xFF;
            if (b > 0)
                i <<= 8;
        }
        return i;
    }

    public String[] getAlphabet() {
        return this.alphabet;
    }

    public Vector getAlphabetV() {
        Vector vector = new Vector(this.alphabet.length - 1);
        for (byte b = 1; b < this.alphabet.length; b++)
            vector.add(this.alphabet[b]);
        return vector;
    }

    public MyList getTransitions(byte[] paramArrayOfbyte) {
        int i;
        MyList myList = new MyList();
        if (paramArrayOfbyte == null) {
            i = -1;
        } else {
            i = decode(paramArrayOfbyte);
        }
        if (i < 0 || i >= this.maxStates)
            return myList;
        if (this.states[i] != null)
            for (Enumeration enumeration = this.states[i].elements(); enumeration.hasMoreElements();) {
                EventState eventState = enumeration.nextElement();
                myList.add(i, encode(eventState.next), eventState.event);
            }
        return myList;
    }

    public String getViolatedProperty() {
        return null;
    }

    public Vector getTraceToState(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
        EventState eventState = new EventState(0, 0);
        int i = EventState.search(eventState, this.states, decode(paramArrayOfbyte1), decode(paramArrayOfbyte2), -123456);
        return EventState.getPath(eventState.path, this.alphabet);
    }

    public boolean END(byte[] paramArrayOfbyte) {
        return (decode(paramArrayOfbyte) == this.endseq);
    }

    public byte[] START() {
        return encode(0);
    }

    public void setStackChecker(StackCheck paramStackCheck) {
    }

    public boolean isPartialOrder() {
        return false;
    }

    public void disablePartialOrder() {
    }

    public boolean isAccepting(int paramInt) {
        if (paramInt < 0 || paramInt >= this.maxStates)
            return false;
        return EventState.isAccepting(this.states[paramInt], this.alphabet);
    }

    public BitSet accepting() {
        BitSet bitSet = new BitSet();
        for (byte b = 0; b < this.maxStates; b++) {
            if (isAccepting(b))
                bitSet.set(b);
        }
        return bitSet;
    }
}
