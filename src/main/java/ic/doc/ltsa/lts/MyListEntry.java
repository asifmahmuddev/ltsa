package ic.doc.ltsa.lts;

class MyListEntry {
    int fromState;
    byte[] toState;
    int actionNo;
    MyListEntry next;

    MyListEntry(int paramInt1, byte[] paramArrayOfbyte, int paramInt2) {
        this.fromState = paramInt1;
        this.toState = paramArrayOfbyte;
        this.actionNo = paramInt2;
        this.next = null;
    }
}
