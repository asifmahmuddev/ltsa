package ic.doc.ltsa.lts;

public class MyList {
    protected MyListEntry head = null;
    protected MyListEntry tail = null;
    protected int count = 0;

    public void add(int paramInt1, byte[] paramArrayOfbyte, int paramInt2) {
        MyListEntry myListEntry = new MyListEntry(paramInt1, paramArrayOfbyte, paramInt2);
        if (this.head == null) {
            this.head = this.tail = myListEntry;
        } else {
            this.tail.next = myListEntry;
            this.tail = myListEntry;
        }
        this.count++;
    }

    public void next() {
        if (this.head != null)
            this.head = this.head.next;
    }

    public boolean empty() {
        return (this.head == null);
    }

    public int getFrom() {
        return (this.head != null) ? this.head.fromState : -1;
    }

    public byte[] getTo() {
        return (this.head != null) ? this.head.toState : null;
    }

    public int getAction() {
        return (this.head != null) ? this.head.actionNo : -1;
    }

    public int size() {
        return this.count;
    }
}
