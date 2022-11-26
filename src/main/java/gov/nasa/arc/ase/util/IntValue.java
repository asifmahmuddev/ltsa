package gov.nasa.arc.ase.util;

public class IntValue {
    private int intvalue = 0;

    public IntValue() {
    }

    public IntValue(int paramInt) {
        this.intvalue = paramInt;
    }

    public String toString() {
        return Integer.toString(this.intvalue);
    }

    public void set(int paramInt) {
        this.intvalue = paramInt;
    }

    public int get() {
        return this.intvalue;
    }
}
