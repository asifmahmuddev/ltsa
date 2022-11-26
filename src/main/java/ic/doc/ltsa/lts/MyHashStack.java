package ic.doc.ltsa.lts;

public class MyHashStack implements StackCheck {
    private MyHashStackEntry[] table;
    private int count = 0;
    private int depth = 0;
    private MyHashStackEntry head = null;

    public MyHashStack(int paramInt) {
        this.table = new MyHashStackEntry[paramInt];
    }

    public void pushPut(byte[] paramArrayOfbyte) {
        MyHashStackEntry myHashStackEntry = new MyHashStackEntry(paramArrayOfbyte);
        int i = StateCodec.hash(paramArrayOfbyte) % this.table.length;
        myHashStackEntry.next = this.table[i];
        this.table[i] = myHashStackEntry;
        this.count++;
        myHashStackEntry.link = this.head;
        this.head = myHashStackEntry;
        this.depth++;
    }

    public void pop() {
        if (this.head == null)
            return;
        this.head.marked = false;
        this.head = this.head.link;
        this.depth--;
    }

    public byte[] peek() {
        return this.head.key;
    }

    public void mark(int paramInt) {
        this.head.marked = true;
        this.head.stateNumber = paramInt;
    }

    public boolean marked() {
        return this.head.marked;
    }

    public boolean empty() {
        return (this.head == null);
    }

    public boolean containsKey(byte[] paramArrayOfbyte) {
        int i = StateCodec.hash(paramArrayOfbyte) % this.table.length;
        MyHashStackEntry myHashStackEntry = this.table[i];
        while (myHashStackEntry != null) {
            if (StateCodec.equals(myHashStackEntry.key, paramArrayOfbyte))
                return true;
            myHashStackEntry = myHashStackEntry.next;
        }
        return false;
    }

    public boolean onStack(byte[] paramArrayOfbyte) {
        int i = StateCodec.hash(paramArrayOfbyte) % this.table.length;
        MyHashStackEntry myHashStackEntry = this.table[i];
        while (myHashStackEntry != null) {
            if (StateCodec.equals(myHashStackEntry.key, paramArrayOfbyte))
                return myHashStackEntry.marked;
            myHashStackEntry = myHashStackEntry.next;
        }
        return false;
    }

    public int get(byte[] paramArrayOfbyte) {
        int i = StateCodec.hash(paramArrayOfbyte) % this.table.length;
        MyHashStackEntry myHashStackEntry = this.table[i];
        while (myHashStackEntry != null) {
            if (StateCodec.equals(myHashStackEntry.key, paramArrayOfbyte))
                return myHashStackEntry.stateNumber;
            myHashStackEntry = myHashStackEntry.next;
        }
        return -99999;
    }

    public int size() {
        return this.count;
    }

    public int getDepth() {
        return this.depth;
    }
}
