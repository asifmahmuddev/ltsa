package ic.doc.ltsa.lts;

class MyHashProgEntry {
    byte[] key;
    int dfn;
    int low;
    boolean isReturn;
    boolean isProcessed;
    MyHashProgEntry next;
    MyHashProgEntry parent;

    MyHashProgEntry(byte[] paramArrayOfbyte) {
        this.key = paramArrayOfbyte;
        this.dfn = 0;
        this.low = 0;
        this.isReturn = false;
        this.isProcessed = false;
        this.next = null;
        this.parent = null;
    }

    MyHashProgEntry(byte[] paramArrayOfbyte, MyHashProgEntry paramMyHashProgEntry) {
        this.key = paramArrayOfbyte;
        this.dfn = 0;
        this.low = 0;
        this.isReturn = false;
        this.isProcessed = false;
        this.next = null;
        this.parent = paramMyHashProgEntry;
    }
}
