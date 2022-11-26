package ic.doc.ltsa.lts;

import java.util.BitSet;
import java.util.Iterator;
import java.util.LinkedList;

public class SuperTrace {
    BitSet table;
    MyStack stack;
    int nbits;
    Automata mach;
    LTSOutput output;
    LinkedList errorTrace;
    private static int DEPTHBOUND = 100000;
    private static int HASHSIZE = 8000;
    private static final int SUCCESS = 0;
    private static final int DEADLOCK = 1;
    private static final int ERROR = 2;
    int nstate;
    int nTrans;

    public static void setDepthBound(int paramInt) {
        DEPTHBOUND = paramInt;
    }

    public static int getDepthBound() {
        return DEPTHBOUND;
    }

    public static void setHashSize(int paramInt) {
        HASHSIZE = paramInt;
    }

    public static int getHashSize() {
        return HASHSIZE;
    }

    public SuperTrace(Automata paramAutomata, LTSOutput paramLTSOutput) {
        this.nstate = 0;
        this.nTrans = 0;
        this.mach = paramAutomata;
        this.output = paramLTSOutput;
        this.nbits = HASHSIZE * 1024 * 8;
        this.table = new BitSet(this.nbits);
        this.stack = new MyStack();
        analyse();
    }

    public void analyse() {
        this.output.outln("Analysing using Supertrace (Depth bound " + DEPTHBOUND + " Hashtable size " + HASHSIZE + "K )...");
        System.gc();
        long l1 = System.currentTimeMillis();
        int i = search();
        long l2 = System.currentTimeMillis();
        outStatistics(this.stack.depth, this.nstate, this.nTrans);
        if (i == 1) {
            this.output.outln("Trace to DEADLOCK:");
            this.errorTrace = computeTrace(false);
            if (this.errorTrace.size() <= 100) {
                printPath(this.errorTrace);
            } else {
                this.output.outln("Trace length " + this.errorTrace.size() + ", replay using Check/Run");
            }
        } else if (i == 2) {
            this.output.outln("Trace to property violation in " + this.mach.getViolatedProperty() + ":");
            this.errorTrace = computeTrace(true);
            if (this.errorTrace.size() <= 100) {
                printPath(this.errorTrace);
            } else {
                this.output.outln("Trace length " + this.errorTrace.size() + ", replay using Check/Run");
            }
        } else {
            this.output.outln("No deadlocks/errors");
        }
        this.output.outln("Analysed using Supertrace in: " + (l2 - l1) + "ms");
    }

    private int hashOne(byte[] paramArrayOfbyte) {
        return StateCodec.hash(paramArrayOfbyte);
    }

    private int hashTwo(byte[] paramArrayOfbyte) {
        long l = StateCodec.hashLong(paramArrayOfbyte);
        l += 1325656567898L;
        int i = (int) (l ^ l >>> 32L);
        return i & Integer.MAX_VALUE;
    }

    private void put(byte[] paramArrayOfbyte) {
        this.table.set(hashOne(paramArrayOfbyte) % this.nbits);
        this.table.set(hashTwo(paramArrayOfbyte) % this.nbits);
    }

    private boolean contains(byte[] paramArrayOfbyte) {
        return (this.table.get(hashOne(paramArrayOfbyte) % this.nbits) && this.table.get(hashTwo(paramArrayOfbyte) % this.nbits));
    }

    private int search() {
        byte[] arrayOfByte = this.mach.START();
        MyHash myHash = null;
        if (this.mach.isPartialOrder()) {
            myHash = new MyHash(DEPTHBOUND + 1);
            this.mach.setStackChecker(myHash);
        }
        this.stack.push(arrayOfByte);
        put(arrayOfByte);
        while (!this.stack.empty()) {
            if (this.stack.marked()) {
                if (myHash != null)
                    myHash.remove(this.stack.peek());
                this.stack.pop();
                continue;
            }
            this.nstate++;
            if (this.nstate % 10000 == 0)
                outStatistics(this.stack.getDepth(), this.nstate, this.nTrans);
            byte[] arrayOfByte1 = this.stack.peek();
            this.stack.mark();
            if (myHash != null)
                myHash.put(arrayOfByte1);
            MyList myList = this.mach.getTransitions(arrayOfByte1);
            if (myList.empty() && !this.mach.END(arrayOfByte1))
                return 1;
            while (!myList.empty()) {
                this.nTrans++;
                if (myList.getTo() == null)
                    return 2;
                if (this.stack.getDepth() < DEPTHBOUND && !contains(myList.getTo())) {
                    this.stack.push(myList.getTo());
                    put(myList.getTo());
                }
                myList.next();
            }
        }
        return 0;
    }

    public LinkedList getErrorTrace() {
        return this.errorTrace;
    }

    private void outStatistics(int paramInt1, int paramInt2, int paramInt3) {
        this.output.out("-- Depth: " + paramInt1 + " States: " + paramInt2 + " Transitions: " + paramInt3);
        Runtime runtime = Runtime.getRuntime();
        this.output.outln(" Memory used: " + ((runtime.totalMemory() - runtime.freeMemory()) / 1000L) + "K");
    }

    private void printPath(LinkedList paramLinkedList) {
        Iterator iterator = paramLinkedList.iterator();
        while (iterator.hasNext())
            this.output.outln("\t" + (String) iterator.next());
    }

    private LinkedList computeTrace(boolean paramBoolean) {
        this.mach.disablePartialOrder();
        LinkedList linkedList = new LinkedList();
        if (paramBoolean) {
            for (; !this.stack.marked(); this.stack.pop());
            linkedList.addFirst(findAction(this.stack.peek(), null));
        }
        byte[] arrayOfByte = this.stack.pop();
        while (!this.stack.empty()) {
            if (!this.stack.marked()) {
                this.stack.pop();
                continue;
            }
            linkedList.addFirst(findAction(this.stack.peek(), arrayOfByte));
            arrayOfByte = this.stack.pop();
        }
        return linkedList;
    }

    private String findAction(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
        MyList myList = this.mach.getTransitions(paramArrayOfbyte1);
        while (!myList.empty()) {
            if (StateCodec.equals(myList.getTo(), paramArrayOfbyte2))
                return this.mach.getAlphabet()[myList.getAction()];
            myList.next();
        }
        return "ACTION NOT FOUND";
    }
}
