package ic.doc.ltsa.lts;

public class MyHashProg implements StackCheck {
    private MyHashProgEntry[] table;
    private int count = 0;

    public MyHashProg() {
        this.table = new MyHashProgEntry[100001];
    }

    public MyHashProg(int paramInt) {
        this.table = new MyHashProgEntry[paramInt];
    }

    public void add(byte[] paramArrayOfbyte, MyHashProgEntry paramMyHashProgEntry) {
        MyHashProgEntry myHashProgEntry = new MyHashProgEntry(paramArrayOfbyte, paramMyHashProgEntry);
        int i = StateCodec.hash(paramArrayOfbyte) % this.table.length;
        myHashProgEntry.next = this.table[i];
        this.table[i] = myHashProgEntry;
        this.count++;
    }

    public MyHashProgEntry get(byte[] paramArrayOfbyte) {
        int i = StateCodec.hash(paramArrayOfbyte) % this.table.length;
        MyHashProgEntry myHashProgEntry = this.table[i];
        while (myHashProgEntry != null) {
            if (StateCodec.equals(myHashProgEntry.key, paramArrayOfbyte))
                return myHashProgEntry;
            myHashProgEntry = myHashProgEntry.next;
        }
        return null;
    }

    public boolean onStack(byte[] paramArrayOfbyte) {
        MyHashProgEntry myHashProgEntry = get(paramArrayOfbyte);
        if (myHashProgEntry == null)
            return false;
        return (myHashProgEntry.isReturn && !myHashProgEntry.isProcessed);
    }

    public int size() {
        return this.count;
    }
}
