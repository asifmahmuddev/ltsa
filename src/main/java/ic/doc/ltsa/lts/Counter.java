package ic.doc.ltsa.lts;

public class Counter {
    int count;

    Counter(int paramInt) {
        this.count = paramInt;
    }

    public Integer label() {
        return new Integer(this.count++);
    }

    public Integer lastLabel() {
        return new Integer(this.count);
    }

    public Integer interval(int paramInt) {
        int i = this.count;
        this.count += paramInt;
        return new Integer(i);
    }
}
