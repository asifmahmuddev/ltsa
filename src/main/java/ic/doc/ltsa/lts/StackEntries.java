package ic.doc.ltsa.lts;

class StackEntries {
    static final int N = 1024;
    byte[][] val = new byte[1024][];
    boolean[] marks = new boolean[1024];
    int index;
    StackEntries next;

    StackEntries(StackEntries paramStackEntries) {
        this.index = 0;
        this.next = paramStackEntries;
    }

    boolean empty() {
        return (this.index == 0);
    }

    boolean full() {
        return (this.index == 1024);
    }

    void push(byte[] paramArrayOfbyte) {
        this.val[this.index] = paramArrayOfbyte;
        this.marks[this.index] = false;
        this.index++;
    }

    byte[] pop() {
        this.index--;
        return this.val[this.index];
    }

    byte[] peek() {
        return this.val[this.index - 1];
    }

    void mark() {
        this.marks[this.index - 1] = true;
    }

    boolean marked() {
        return this.marks[this.index - 1];
    }
}
