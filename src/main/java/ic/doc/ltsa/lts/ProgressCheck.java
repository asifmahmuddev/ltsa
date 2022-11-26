package ic.doc.ltsa.lts;

import java.util.BitSet;
import java.util.Enumeration;
import java.util.Stack;
import java.util.Vector;

public class ProgressCheck {
    public static boolean strongFairFlag = true;
    private Automata mach;
    private Stack stack;
    int id = 0;
    int ncomp = 0;
    LTSOutput output;
    int violation = 0;
    boolean hasERROR = false;
    static final int Maxviolation = 10;
    String tnames;
    int accept = 0;
    boolean progress;
    private int sccId;
    private int nTrans;
    Vector errorTrace;
    Vector cycleTrace;

    public ProgressCheck(Automata paramAutomata, LTSOutput paramLTSOutput) {
        this.mach = paramAutomata;
        this.output = paramLTSOutput;
    }

    public void doProgressCheck() {
        this.progress = true;
        this.output.outln("Progress Check...");
        long l1 = System.currentTimeMillis();
        ProgressTest.initTests(this.mach.getAlphabet());
        this.stack = new Stack();
        findCC();
        long l2 = System.currentTimeMillis();
        if (this.hasERROR) {
            this.output.outln("Safety property violation detected - check safety.");
        } else if (this.violation == 0) {
            this.output.outln("No progress violations detected.");
        } else if (this.violation > 10) {
            this.output.outln("More than 10 violations");
        }
        this.output.outln("Progress Check in: " + (l2 - l1) + "ms");
    }

    public void doLTLCheck() {
        this.progress = false;
        this.output.outln("LTL Property Check...");
        long l1 = System.currentTimeMillis();
        this.accept = acceptLabel(this.mach.getAlphabet());
        if (this.accept == 0) {
            this.output.outln("No labeled acceptance states.");
            return;
        }
        this.stack = new Stack();
        findCC();
        long l2 = System.currentTimeMillis();
        if (this.hasERROR) {
            this.output.outln("Safety property violation detected - check safety.");
        } else if (this.violation == 0) {
            this.output.outln("No LTL Property violations detected.");
        } else if (this.violation > 10) {
            this.output.outln("More than 10 violations");
        }
        this.output.outln("LTL Property Check in: " + (l2 - l1) + "ms");
    }

    public int numberComponents() {
        return this.ncomp;
    }

    private void findCC() {
        MyHashProg myHashProg = new MyHashProg();
        MyStack myStack = new MyStack();
        this.mach.setStackChecker(myHashProg);
        this.sccId = 0;
        this.nTrans = 0;
        byte[] arrayOfByte = this.mach.START();
        myStack.push(arrayOfByte);
        myHashProg.add(arrayOfByte, null);
        while (!myStack.empty()) {
            MyHashProgEntry myHashProgEntry = myHashProg.get(myStack.peek());
            while (myHashProgEntry.isReturn || myHashProgEntry.isProcessed) {
                if (myHashProgEntry.isReturn && !myHashProgEntry.isProcessed) {
                    myHashProgEntry.isProcessed = true;
                    if (myHashProgEntry.parent != null)
                        myHashProgEntry.parent.low = Math.min(myHashProgEntry.parent.low, myHashProgEntry.low);
                    if (myHashProgEntry.low == myHashProgEntry.dfn && component(myHashProg, this.stack, myHashProgEntry.key))
                        return;
                }
                myStack.pop();
                if (myStack.empty()) {
                    outStatistics(this.sccId, this.nTrans);
                    return;
                }
                myHashProgEntry = myHashProg.get(myStack.peek());
            }
            myHashProgEntry.low = myHashProgEntry.dfn = ++this.sccId;
            if (this.sccId % 10000 == 0)
                outStatistics(this.sccId, this.nTrans);
            this.stack.push(myHashProgEntry.key);
            myHashProgEntry.isReturn = true;
            MyList myList = this.mach.getTransitions(myHashProgEntry.key);
            while (!myList.empty()) {
                this.nTrans++;
                if (myList.getTo() == null) {
                    this.hasERROR = true;
                    return;
                }
                if (this.accept == 0 || myList.getAction() != this.accept) {
                    MyHashProgEntry myHashProgEntry1 = myHashProg.get(myList.getTo());
                    if (myHashProgEntry1 == null) {
                        myHashProg.add(myList.getTo(), myHashProgEntry);
                        myStack.push(myList.getTo());
                    } else if (myHashProgEntry1.dfn == 0) {
                        myHashProgEntry1.parent = myHashProgEntry;
                        myStack.push(myList.getTo());
                    } else if (myHashProgEntry1.dfn < myHashProgEntry.dfn) {
                        myHashProgEntry.low = Math.min(myHashProgEntry1.dfn, myHashProgEntry.low);
                    }
                }
                myList.next();
            }
        }
        outStatistics(this.sccId, this.nTrans);
    }

    private void outhse(MyHashProgEntry paramMyHashProgEntry) {
        this.output.outln("state: " + paramMyHashProgEntry.key + " dfn: " + paramMyHashProgEntry.dfn + " low: " + paramMyHashProgEntry.low + " ret " + paramMyHashProgEntry.isReturn);
    }

    private boolean component(MyHashProg paramMyHashProg, Stack paramStack, byte[] paramArrayOfbyte) {
        this.ncomp++;
        boolean bool = false;
        Stack stack = new Stack();
        BitSet bitSet = new BitSet((this.mach.getAlphabet()).length);
        while (true) {
            stack.push(paramStack.pop());
            byte[] arrayOfByte = stack.peek();
            MyList myList = this.mach.getTransitions(arrayOfByte);
            while (!myList.empty()) {
                int i = myList.getAction();
                if (i == this.accept)
                    bool = true;
                bitSet.set(i);
                myList.next();
            }
            if (StateCodec.equals(arrayOfByte, paramArrayOfbyte)) {
                if (this.progress) {
                    if (missing(bitSet) && terminalComponent(paramMyHashProg, stack)) {
                        outStatistics(this.sccId, this.nTrans);
                        printCycle(stack, bitSet, paramArrayOfbyte);
                        return true;
                    }
                } else if (bool) {
                    if (!strongFairFlag) {
                        if (nontrivial(stack)) {
                            outStatistics(this.sccId, this.nTrans);
                            printCounterExample(stack, paramArrayOfbyte);
                            return true;
                        }
                    } else if (terminalComponent(paramMyHashProg, stack)) {
                        outStatistics(this.sccId, this.nTrans);
                        printCounterExample(null, paramArrayOfbyte);
                        return true;
                    }
                }
                for (Enumeration enumeration = stack.elements(); enumeration.hasMoreElements();) {
                    byte[] arrayOfByte1 = (byte[]) enumeration.nextElement();
                    MyHashProgEntry myHashProgEntry = paramMyHashProg.get(arrayOfByte1);
                    myHashProgEntry.dfn = Integer.MAX_VALUE;
                }
                return false;
            }
        }
    }

    private boolean missing(BitSet paramBitSet) {
        int i = (this.mach.getAlphabet()).length;
        if (ProgressTest.noTests()) {
            for (byte b = 1; b < i; b++) {
                if (!paramBitSet.get(b))
                    return true;
            }
        } else {
            this.tnames = null;
            Enumeration enumeration = ProgressTest.tests.elements();
            while (enumeration.hasMoreElements()) {
                ProgressTest progressTest = enumeration.nextElement();
                if (progressTest.cset == null) {
                    if (contains_none_of(i, paramBitSet, progressTest.pset)) {
                        if (this.tnames == null) {
                            this.tnames = progressTest.name;
                            continue;
                        }
                        this.tnames += " " + progressTest.name;
                    }
                    continue;
                }
                if (!contains_none_of(i, paramBitSet, progressTest.pset) && contains_none_of(i, paramBitSet, progressTest.cset)) {
                    if (this.tnames == null) {
                        this.tnames = progressTest.name;
                        continue;
                    }
                    this.tnames += " " + progressTest.name;
                }
            }
            if (this.tnames != null)
                return true;
        }
        return false;
    }

    private boolean contains_none_of(int paramInt, BitSet paramBitSet1, BitSet paramBitSet2) {
        for (byte b = 1; b < paramInt; b++) {
            if (paramBitSet1.get(b) && paramBitSet2.get(b))
                return false;
        }
        return true;
    }

    private boolean terminalComponent(MyHashProg paramMyHashProg, Vector paramVector) {
        BitSet bitSet = new BitSet(10001);
        for (Enumeration enumeration1 = paramVector.elements(); enumeration1.hasMoreElements();) {
            byte[] arrayOfByte = enumeration1.nextElement();
            MyHashProgEntry myHashProgEntry = paramMyHashProg.get(arrayOfByte);
            bitSet.set(myHashProgEntry.dfn);
        }
        for (Enumeration enumeration2 = paramVector.elements(); enumeration2.hasMoreElements();) {
            byte[] arrayOfByte = enumeration2.nextElement();
            MyList myList = this.mach.getTransitions(arrayOfByte);
            while (!myList.empty()) {
                if (myList.getTo() == null) {
                    this.hasERROR = true;
                    return false;
                }
                MyHashProgEntry myHashProgEntry = paramMyHashProg.get(myList.getTo());
                if (myHashProgEntry == null)
                    return false;
                if (myHashProgEntry.dfn == 0)
                    return false;
                if (myHashProgEntry.dfn == Integer.MAX_VALUE)
                    return false;
                if (!bitSet.get(myHashProgEntry.dfn))
                    return false;
                myList.next();
            }
        }
        return true;
    }

    private boolean inComponent(Vector paramVector, byte[] paramArrayOfbyte) {
        for (Enumeration enumeration = paramVector.elements(); enumeration.hasMoreElements();) {
            byte[] arrayOfByte = enumeration.nextElement();
            if (StateCodec.equals(arrayOfByte, paramArrayOfbyte))
                return true;
        }
        return false;
    }

    private boolean nontrivial(Vector paramVector) {
        if (paramVector.size() > 1)
            return true;
        byte[] arrayOfByte = paramVector.elementAt(0);
        MyList myList = this.mach.getTransitions(arrayOfByte);
        while (!myList.empty()) {
            int i = myList.getAction();
            if ((i != this.accept || this.accept == 0) && StateCodec.equals(arrayOfByte, myList.getTo()))
                return true;
            myList.next();
        }
        return false;
    }

    private void printSet(BitSet paramBitSet, boolean paramBoolean) {
        Vector vector = new Vector();
        String[] arrayOfString = this.mach.getAlphabet();
        for (byte b = 1; b < arrayOfString.length; b++) {
            if ((paramBoolean && !paramBitSet.get(b)) || (!paramBoolean && paramBitSet.get(b)))
                vector.addElement(arrayOfString[b]);
        }
        this.output.outln("\t" + (new Alphabet(vector)).toString());
    }

    Vector getErrorTrace() {
        if (this.errorTrace == null)
            return null;
        if (this.cycleTrace != null) {
            this.errorTrace.addAll(this.cycleTrace);
            this.errorTrace.addAll(this.cycleTrace);
        }
        return this.errorTrace;
    }

    private void printCycle(Stack paramStack, BitSet paramBitSet, byte[] paramArrayOfbyte) {
        this.violation++;
        if (this.violation > 10)
            return;
        this.errorTrace = getRootTrace(paramArrayOfbyte);
        if (this.errorTrace == null)
            return;
        this.cycleTrace = getCycleTrace(null, paramArrayOfbyte);
        if (ProgressTest.noTests()) {
            this.output.outln("Progress violation for actions: ");
            printSet(paramBitSet, true);
        } else {
            this.output.outln("Progress violation: " + this.tnames);
        }
        this.output.outln("Trace to terminal set of states:");
        printTrace(this.errorTrace);
        this.output.outln("Cycle in terminal set:");
        printTrace(this.cycleTrace);
        this.output.outln("Actions in terminal set:");
        printSet(paramBitSet, false);
    }

    private void printCounterExample(Stack paramStack, byte[] paramArrayOfbyte) {
        this.violation++;
        if (this.violation > 10)
            return;
        this.errorTrace = getRootTrace(paramArrayOfbyte);
        if (this.errorTrace == null)
            return;
        this.cycleTrace = getCycleTrace(paramStack, paramArrayOfbyte);
        this.output.outln("Violation of LTL property: " + this.mach.getAlphabet()[this.accept]);
        this.output.outln("Trace to terminal set of states:");
        printTrace(this.errorTrace);
        this.output.outln("Cycle in terminal set:");
        printTrace(this.cycleTrace);
    }

    Vector getRootTrace(byte[] paramArrayOfbyte) {
        this.output.outln("Finding trace to cycle...");
        Vector vector = this.mach.getTraceToState(this.mach.START(), paramArrayOfbyte);
        if (vector == null)
            this.hasERROR = true;
        return vector;
    }

    Vector getCycleTrace(Vector paramVector, byte[] paramArrayOfbyte) {
        this.output.outln("Finding trace in cycle...");
        Vector vector = null;
        MyList myList = this.mach.getTransitions(paramArrayOfbyte);
        byte[] arrayOfByte = null;
        int i = 0;
        while (!myList.empty()) {
            i = myList.getAction();
            if ((i == this.accept && this.accept != 0) || stateLabel(i)) {
                myList.next();
                continue;
            }
            arrayOfByte = myList.getTo();
            if (paramVector == null || inComponent(paramVector, arrayOfByte))
                break;
            myList.next();
        }
        if (arrayOfByte != null) {
            vector = this.mach.getTraceToState(arrayOfByte, paramArrayOfbyte);
            vector.add(0, this.mach.getAlphabet()[i]);
        }
        return vector;
    }

    private void printTrace(Vector paramVector) {
        if (paramVector == null)
            return;
        Enumeration enumeration = paramVector.elements();
        while (enumeration.hasMoreElements())
            this.output.outln("\t" + (String) enumeration.nextElement());
    }

    private boolean stateLabel(int paramInt) {
        String str = this.mach.getAlphabet()[paramInt];
        return (str.charAt(0) == '_');
    }

    private void outStatistics(int paramInt1, int paramInt2) {
        Runtime runtime = Runtime.getRuntime();
        this.output.outln("-- States: " + paramInt1 + " Transitions: " + paramInt2 + " Memory used: " + ((runtime.totalMemory() - runtime.freeMemory()) / 1000L) + "K");
    }

    private boolean isAccept(String paramString) {
        if (paramString.charAt(0) == '@')
            return true;
        int i = 0;
        int j = paramString.indexOf('.');
        while (j > 0) {
            if (paramString.substring(i, j).charAt(0) == '@')
                return true;
            i = j + 1;
            j = paramString.indexOf('.', j + 1);
        }
        return (paramString.substring(i).charAt(0) == '@');
    }

    private int acceptLabel(String[] paramArrayOfString) {
        for (byte b = 1; b < paramArrayOfString.length; b++) {
            if (isAccept(paramArrayOfString[b]))
                return b;
        }
        return 0;
    }
}
