package ic.doc.ltsa.lts;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

public class PartialOrder {
    private CompactState[] machines;
    private int[][] actionSharedBy;
    private StackChecker checker;
    private int[][] candidates;
    private int[][] partners;
    private int Nactions;
    private String[] names;
    private BitSet visible;
    private boolean preserveOE;
    private BitSet high;

    public PartialOrder(Hashtable paramHashtable, String[] paramArrayOfString, CompactState[] paramArrayOfCompactState, StackChecker paramStackChecker, Vector paramVector, boolean paramBoolean1,
        boolean paramBoolean2, BitSet paramBitSet) {
        this.machines = paramArrayOfCompactState;
        this.names = paramArrayOfString;
        this.Nactions = paramArrayOfString.length;
        this.checker = paramStackChecker;
        this.preserveOE = paramBoolean2;
        this.high = paramBitSet;
        this.actionSharedBy = new int[this.Nactions][];
        for (byte b1 = 1; b1 < paramArrayOfString.length; b1++) {
            BitSet bitSet = (BitSet) paramHashtable.get(paramArrayOfString[b1]);
            this.actionSharedBy[b1] = bitsToArray(bitSet);
        }
        this.visible = new BitSet(this.Nactions);
        for (byte b2 = 1; b2 < paramArrayOfString.length; b2++) {
            if (paramVector == null) {
                this.visible.set(b2);
            } else if (paramBoolean1) {
                if (CompactState.contains(paramArrayOfString[b2], paramVector))
                    this.visible.set(b2);
            } else if (!CompactState.contains(paramArrayOfString[b2], paramVector)) {
                this.visible.set(b2);
            }
        }
        initPartners();
        this.candidates = computeCandidates();
    }

    public List transitions(int[] paramArrayOfint) {
        for (byte b1 = 0; b1 < this.machines.length; b1++) {
            if (this.candidates[b1][paramArrayOfint[b1]] == 1) {
                ArrayList arrayList = new ArrayList(8);
                boolean bool = getMachTransitions(arrayList, b1, paramArrayOfint, null);
                if (bool)
                    return arrayList;
            }
        }
        for (byte b2 = 0; b2 < this.machines.length; b2++) {
            if (this.candidates[b2][paramArrayOfint[b2]] == 2) {
                int i = this.partners[b2][paramArrayOfint[b2]];
                if (b2 == this.partners[i][paramArrayOfint[i]]) {
                    List list = getPairTransitions(b2, i, paramArrayOfint);
                    if (list != null)
                        return list;
                }
            }
        }
        return null;
    }

    private boolean addTransitions(List paramList, int[] paramArrayOfint, int paramInt1, int paramInt2) {
        int[] arrayOfInt = null;
        int i = this.actionSharedBy[paramInt1][paramInt2];
        EventState eventState = (this.machines[i]).states[paramArrayOfint[i]];
        if (eventState != null)
            arrayOfInt = myclone(paramArrayOfint, paramInt1);
        eventState = EventState.firstCompState(eventState, paramInt1, paramArrayOfint);
        if (paramInt2 < (this.actionSharedBy[paramInt1]).length - 1) {
            if (!addTransitions(paramList, paramArrayOfint, paramInt1, paramInt2 + 1))
                return false;
        } else {
            if (this.checker.onStack(paramArrayOfint))
                return false;
            paramList.add(paramArrayOfint);
        }
        while (eventState != null) {
            int[] arrayOfInt1 = myclone(arrayOfInt, paramInt1);
            eventState = EventState.moreCompState(eventState, arrayOfInt1);
            if (paramInt2 < (this.actionSharedBy[paramInt1]).length - 1) {
                if (!addTransitions(paramList, arrayOfInt1, paramInt1, paramInt2 + 1))
                    return false;
                continue;
            }
            if (this.checker.onStack(arrayOfInt1))
                return false;
            paramList.add(arrayOfInt1);
        }
        return true;
    }

    private List getPairTransitions(int paramInt1, int paramInt2, int[] paramArrayOfint) {
        ArrayList arrayList = new ArrayList(8);
        boolean bool = true;
        if (!this.preserveOE) {
            BitSet bitSet = getUnshared(paramInt1, paramArrayOfint);
            if (bitSet != null)
                bool = getMachTransitions(arrayList, paramInt1, paramArrayOfint, bitSet);
            if (!bool)
                return null;
            bitSet = getUnshared(paramInt2, paramArrayOfint);
            if (bitSet != null)
                bool = getMachTransitions(arrayList, paramInt2, paramArrayOfint, bitSet);
            if (!bool)
                return null;
        }
        BitSet bitSet1 = new BitSet(this.Nactions);
        EventState.hasEvents((this.machines[paramInt1]).states[paramArrayOfint[paramInt1]], bitSet1);
        BitSet bitSet2 = new BitSet(this.Nactions);
        EventState.hasEvents((this.machines[paramInt2]).states[paramArrayOfint[paramInt2]], bitSet2);
        bitSet1.and(bitSet2);
        if (this.preserveOE && countSet(bitSet1) != 1)
            return null;
        bitSet1.clear(0);
        int[] arrayOfInt = bitsToArray(bitSet1);
        for (byte b = 0; b < arrayOfInt.length; b++) {
            bool = addTransitions(arrayList, myclone(paramArrayOfint, arrayOfInt[b]), arrayOfInt[b], 0);
            if (!bool)
                return null;
        }
        return arrayList;
    }

    private BitSet getUnshared(int paramInt, int[] paramArrayOfint) {
        BitSet bitSet = new BitSet(this.Nactions);
        Enumeration enumeration = (this.machines[paramInt]).states[paramArrayOfint[paramInt]].elements();
        while (enumeration.hasMoreElements()) {
            EventState eventState = enumeration.nextElement();
            if (eventState.event == 0) {
                bitSet.set(eventState.event);
                continue;
            }
            if ((this.actionSharedBy[eventState.event]).length == 1)
                bitSet.set(eventState.event);
        }
        if (bitSet.length() == 0)
            return null;
        return bitSet;
    }

    private boolean getMachTransitions(List paramList, int paramInt, int[] paramArrayOfint, BitSet paramBitSet) {
        Enumeration enumeration = (this.machines[paramInt]).states[paramArrayOfint[paramInt]].elements();
        while (enumeration.hasMoreElements()) {
            EventState eventState = enumeration.nextElement();
            if (paramBitSet == null || paramBitSet.get(eventState.event)) {
                int[] arrayOfInt = myclone(paramArrayOfint, eventState.event);
                arrayOfInt[paramInt] = eventState.next;
                if (this.checker.onStack(arrayOfInt))
                    return false;
                paramList.add(arrayOfInt);
            }
        }
        return true;
    }

    private int[] bitsToArray(BitSet paramBitSet) {
        int i = countSet(paramBitSet);
        if (i == 0)
            return null;
        int[] arrayOfInt = new int[i];
        byte b1 = 0;
        int j = paramBitSet.length();
        for (byte b2 = 0; b2 < j; b2++) {
            if (paramBitSet.get(b2)) {
                arrayOfInt[b1] = b2;
                b1++;
            }
        }
        return arrayOfInt;
    }

    private int countSet(BitSet paramBitSet) {
        byte b1 = 0;
        int i = paramBitSet.length();
        for (byte b2 = 0; b2 < i; b2++) {
            if (paramBitSet.get(b2))
                b1++;
        }
        return b1;
    }

    private int[] myclone(int[] paramArrayOfint, int paramInt) {
        int[] arrayOfInt = new int[paramArrayOfint.length];
        for (byte b = 0; b < paramArrayOfint.length - 1;) {
            arrayOfInt[b] = paramArrayOfint[b];
            b++;
        }
        arrayOfInt[paramArrayOfint.length - 1] = paramInt;
        return arrayOfInt;
    }

    private void printArray(String paramString, int[][] paramArrayOfint) {
        System.out.println(paramString);
        for (byte b = 0; b < paramArrayOfint.length; b++) {
            System.out.print("Mach " + b + " --");
            for (byte b1 = 0; b1 < (paramArrayOfint[b]).length; b1++)
                System.out.print(" " + paramArrayOfint[b][b1]);
            System.out.println(".");
        }
    }

    private void initPartners() {
        this.partners = new int[this.machines.length][];
        for (byte b = 0; b < this.machines.length; b++) {
            this.partners[b] = new int[(this.machines[b]).states.length];
            for (byte b1 = 0; b1 < (this.machines[b]).states.length;) {
                this.partners[b][b1] = -1;
                b1++;
            }
        }
    }

    private int[][] computeCandidates() {
        int[][] arrayOfInt = new int[this.machines.length][];
        for (byte b = 0; b < this.machines.length; b++) {
            arrayOfInt[b] = new int[(this.machines[b]).states.length];
            for (byte b1 = 0; b1 < (this.machines[b]).states.length; b1++) {
                int[] arrayOfInt1 = EventState.localEnabled((this.machines[b]).states[b1]);
                arrayOfInt[b][b1] = candidateNumber(b, b1, arrayOfInt1);
            }
        }
        return arrayOfInt;
    }

    private int candidateNumber(int paramInt1, int paramInt2, int[] paramArrayOfint) {
        if (paramArrayOfint == null)
            return 0;
        if (this.preserveOE && EventState.hasNonDet((this.machines[paramInt1]).states[paramInt2]))
            return 0;
        int i = 0;
        byte b1 = 0;
        int j = -1;
        for (byte b2 = 0; b2 < paramArrayOfint.length; b2++) {
            int k = 0;
            int m = paramArrayOfint[b2];
            if (this.visible.get(m))
                return 0;
            if (this.high != null && !this.high.get(m))
                return 0;
            if (m == 0) {
                k = 1;
            } else {
                k = (this.actionSharedBy[m]).length;
            }
            if (k == 1)
                b1++;
            if (k > i)
                i = k;
            if (i > 2)
                return 0;
            if (k == 2)
                if (j < 0) {
                    j = getPartner(paramInt1, m);
                } else if (j != getPartner(paramInt1, m)) {
                    return 0;
                }
        }
        if (this.preserveOE && (b1 > 1 || (i == 2 && b1 > 0)))
            return 0;
        if (i == 2)
            this.partners[paramInt1][paramInt2] = j;
        return i;
    }

    private int getPartner(int paramInt1, int paramInt2) {
        if (this.actionSharedBy[paramInt2][0] == paramInt1)
            return this.actionSharedBy[paramInt2][1];
        return this.actionSharedBy[paramInt2][0];
    }
}
