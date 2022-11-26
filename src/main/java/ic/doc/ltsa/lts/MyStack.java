package ic.doc.ltsa.lts;

class MyStack {
    protected StackEntries head = null;
    protected int depth = 0;

    boolean empty() {
        return (this.head == null);
    }

    void push(byte[] paramArrayOfbyte) {
        if (this.head == null) {
            this.head = new StackEntries(null);
        } else if (this.head.full()) {
            this.head = new StackEntries(this.head);
        }
        this.head.push(paramArrayOfbyte);
        this.depth++;
    }

    byte[] pop() {
        byte[] arrayOfByte = this.head.pop();
        this.depth--;
        if (this.head.empty())
            this.head = this.head.next;
        return arrayOfByte;
    }

    byte[] peek() {
        return this.head.peek();
    }

    void mark() {
        this.head.mark();
    }

    boolean marked() {
        return this.head.marked();
    }

    int getDepth() {
        return this.depth;
    }
}
