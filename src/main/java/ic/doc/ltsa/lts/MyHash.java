package ic.doc.ltsa.lts;

public class MyHash implements StackCheck {
    private MyHashEntry[] table;
    private int count = 0;

    public MyHash(int paramInt) {
        this.table = new MyHashEntry[paramInt];
    }

    public void put(byte[] paramArrayOfbyte) {
        MyHashEntry myHashEntry = new MyHashEntry(paramArrayOfbyte);
        int i = StateCodec.hash(paramArrayOfbyte) % this.table.length;
        myHashEntry.next = this.table[i];
        this.table[i] = myHashEntry;
        this.count++;
    }

    public void put(byte[] paramArrayOfbyte, int paramInt) {
        int i = StateCodec.hash(paramArrayOfbyte) % this.table.length;
        MyHashEntry myHashEntry = this.table[i];
        while (myHashEntry != null) {
            if (StateCodec.equals(myHashEntry.key, paramArrayOfbyte)) {
                myHashEntry.value = paramInt;
                return;
            }
            myHashEntry = myHashEntry.next;
        }
        myHashEntry = new MyHashEntry(paramArrayOfbyte, paramInt);
        myHashEntry.next = this.table[i];
        this.table[i] = myHashEntry;
        this.count++;
    }

    public void remove(byte[] paramArrayOfbyte) {
        int i = StateCodec.hash(paramArrayOfbyte) % this.table.length;
        MyHashEntry myHashEntry1 = this.table[i];
        MyHashEntry myHashEntry2 = myHashEntry1;
        while (myHashEntry1 != null) {
            if (StateCodec.equals(myHashEntry1.key, paramArrayOfbyte)) {
                if (myHashEntry2 == this.table[i]) {
                    this.table[i] = myHashEntry1.next;
                } else {
                    myHashEntry2 = myHashEntry1.next;
                }
                return;
            }
            myHashEntry2 = myHashEntry1;
            myHashEntry1 = myHashEntry1.next;
        }
    }

    public boolean onStack(byte[] paramArrayOfbyte) {
        return containsKey(paramArrayOfbyte);
    }

    public boolean containsKey(byte[] paramArrayOfbyte) {
        int i = StateCodec.hash(paramArrayOfbyte) % this.table.length;
        MyHashEntry myHashEntry = this.table[i];
        while (myHashEntry != null) {
            if (StateCodec.equals(myHashEntry.key, paramArrayOfbyte))
                return true;
            myHashEntry = myHashEntry.next;
        }
        return false;
    }

    public int get(byte[] paramArrayOfbyte) {
        int i = StateCodec.hash(paramArrayOfbyte) % this.table.length;
        MyHashEntry myHashEntry = this.table[i];
        while (myHashEntry != null) {
            if (StateCodec.equals(myHashEntry.key, paramArrayOfbyte))
                return myHashEntry.value;
            myHashEntry = myHashEntry.next;
        }
        return -99999;
    }

    public int size() {
        return this.count;
    }
}
