package ic.doc.ltsa.lts;

class MyHashStackEntry {
    byte[] key;
    int stateNumber;
    boolean marked;
    MyHashStackEntry next;
    MyHashStackEntry link;

    MyHashStackEntry(byte[] paramArrayOfbyte) {
        this.key = paramArrayOfbyte;
        this.stateNumber = -1;
        this.next = null;
        this.link = null;
        this.marked = false;
    }

    MyHashStackEntry(byte[] paramArrayOfbyte, int paramInt) {
        this.key = paramArrayOfbyte;
        this.stateNumber = paramInt;
        this.next = null;
        this.link = null;
        this.marked = false;
    }
}
