package ic.doc.ltsa.lts;

class MyHashEntry {
    byte[] key;
    int value;
    MyHashEntry next;

    MyHashEntry(byte[] paramArrayOfbyte) {
        this.key = paramArrayOfbyte;
        this.value = 0;
        this.next = null;
    }

    MyHashEntry(byte[] paramArrayOfbyte, int paramInt) {
        this.key = paramArrayOfbyte;
        this.value = paramInt;
        this.next = null;
    }
}
