package gov.nasa.arc.ase.util;

public class Counter {
    private int counter = 0;

    public Counter() {
    }

    public Counter(int paramInt) {
        this.counter = paramInt;
    }

    public String toString() {
        return Integer.toString(this.counter);
    }

    public void inc() {
        this.counter++;
    }

    public void inc(int paramInt) {
        this.counter += paramInt;
    }

    public int get() {
        return this.counter;
    }
}
