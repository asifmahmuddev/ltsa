package ic.doc.ltsa.lts;

import java.util.LinkedList;

public class MyHashQueue implements StackCheck {
    private MyHashQueueEntry[] table;
    private int count = 0;
    private MyHashQueueEntry head = null;
    private MyHashQueueEntry tail = null;

    public MyHashQueue(int paramInt) {
        this.table = new MyHashQueueEntry[paramInt];
    }

    public void addPut(byte[] paramArrayOfbyte, int paramInt, MyHashQueueEntry paramMyHashQueueEntry) {
        MyHashQueueEntry myHashQueueEntry = new MyHashQueueEntry(paramArrayOfbyte, paramInt, paramMyHashQueueEntry);
        if (paramMyHashQueueEntry != null)
            paramMyHashQueueEntry.level++;
        int i = StateCodec.hash(paramArrayOfbyte) % this.table.length;
        myHashQueueEntry.next = this.table[i];
        this.table[i] = myHashQueueEntry;
        this.count++;
        if (this.head == null) {
            this.head = this.tail = myHashQueueEntry;
        } else {
            this.tail.link = myHashQueueEntry;
            this.tail = myHashQueueEntry;
        }
    }

    public MyHashQueueEntry peek() {
        return this.head;
    }

    public void pop() {
        this.head = this.head.link;
        if (this.head == null)
            this.tail = this.head;
    }

    public boolean empty() {
        return (this.head == null);
    }

    public boolean containsKey(byte[] paramArrayOfbyte) {
        int i = StateCodec.hash(paramArrayOfbyte) % this.table.length;
        MyHashQueueEntry myHashQueueEntry = this.table[i];
        while (myHashQueueEntry != null) {
            if (StateCodec.equals(myHashQueueEntry.key, paramArrayOfbyte))
                return true;
            myHashQueueEntry = myHashQueueEntry.next;
        }
        return false;
    }

    public boolean onStack(byte[] paramArrayOfbyte) {
        int i = StateCodec.hash(paramArrayOfbyte) % this.table.length;
        MyHashQueueEntry myHashQueueEntry = this.table[i];
        while (myHashQueueEntry != null) {
            if (StateCodec.equals(myHashQueueEntry.key, paramArrayOfbyte))
                return (myHashQueueEntry.level <= this.head.level);
            myHashQueueEntry = myHashQueueEntry.next;
        }
        return false;
    }

    public int size() {
        return this.count;
    }

    public LinkedList getPath(MyHashQueueEntry paramMyHashQueueEntry, String[] paramArrayOfString) {
        LinkedList linkedList = new LinkedList();
        while (paramMyHashQueueEntry != null) {
            if (paramMyHashQueueEntry.parent != null)
                linkedList.addFirst(paramArrayOfString[paramMyHashQueueEntry.action]);
            paramMyHashQueueEntry = paramMyHashQueueEntry.parent;
        }
        return linkedList;
    }

    public int depth(MyHashQueueEntry paramMyHashQueueEntry) {
        byte b = 0;
        while (paramMyHashQueueEntry != null) {
            b++;
            paramMyHashQueueEntry = paramMyHashQueueEntry.parent;
        }
        return b;
    }
}
