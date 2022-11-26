package ic.doc.ltsa.lts;

class MyIntHashEntry {
    int key;
    int value;
    MyIntHashEntry next;

    MyIntHashEntry(int paramInt) {
        this.key = paramInt;
        this.value = 0;
        this.next = null;
    }

    MyIntHashEntry(int paramInt1, int paramInt2) {
        this.key = paramInt1;
        this.value = paramInt2;
        this.next = null;
    }
}
