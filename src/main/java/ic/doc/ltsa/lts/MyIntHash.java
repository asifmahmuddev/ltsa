package ic.doc.ltsa.lts;

public class MyIntHash {
    private MyIntHashEntry[] table;
    private int count = 0;

    public MyIntHash(int paramInt) {
        this.table = new MyIntHashEntry[paramInt];
    }

    public void put(int paramInt) {
        MyIntHashEntry myIntHashEntry = new MyIntHashEntry(paramInt);
        int i = paramInt % this.table.length;
        myIntHashEntry.next = this.table[i];
        this.table[i] = myIntHashEntry;
        this.count++;
    }

    public void put(int paramInt1, int paramInt2) {
        int i = paramInt1 % this.table.length;
        MyIntHashEntry myIntHashEntry = this.table[i];
        while (myIntHashEntry != null) {
            if (myIntHashEntry.key == paramInt1) {
                myIntHashEntry.value = paramInt2;
                return;
            }
            myIntHashEntry = myIntHashEntry.next;
        }
        myIntHashEntry = new MyIntHashEntry(paramInt1, paramInt2);
        myIntHashEntry.next = this.table[i];
        this.table[i] = myIntHashEntry;
        this.count++;
    }

    public boolean containsKey(int paramInt) {
        int i = paramInt % this.table.length;
        MyIntHashEntry myIntHashEntry = this.table[i];
        while (myIntHashEntry != null) {
            if (myIntHashEntry.key == paramInt)
                return true;
            myIntHashEntry = myIntHashEntry.next;
        }
        return false;
    }

    public int get(int paramInt) {
        int i = paramInt % this.table.length;
        MyIntHashEntry myIntHashEntry = this.table[i];
        while (myIntHashEntry != null) {
            if (myIntHashEntry.key == paramInt)
                return myIntHashEntry.value;
            myIntHashEntry = myIntHashEntry.next;
        }
        return -99999;
    }

    public int size() {
        return this.count;
    }
}
