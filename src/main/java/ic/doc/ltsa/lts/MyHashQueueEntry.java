package ic.doc.ltsa.lts;

class MyHashQueueEntry {
    byte[] key;
    int action;
    int level = 0;
    MyHashQueueEntry next;
    MyHashQueueEntry link;
    MyHashQueueEntry parent;

    MyHashQueueEntry(byte[] paramArrayOfbyte) {
        this.key = paramArrayOfbyte;
        this.action = 0;
        this.next = null;
        this.link = null;
    }

    MyHashQueueEntry(byte[] paramArrayOfbyte, int paramInt, MyHashQueueEntry paramMyHashQueueEntry) {
        this.key = paramArrayOfbyte;
        this.action = paramInt;
        this.next = null;
        this.link = null;
        this.parent = paramMyHashQueueEntry;
    }
}
