package gov.nasa.arc.ase.util;

public class Maximum {
    private int max = 0;

    public Maximum() {
    }

    public Maximum(int paramInt) {
        this.max = paramInt;
    }

    public String toString() {
        return Integer.toString(this.max);
    }

    public void max(int paramInt) {
        this.max = Math.max(this.max, paramInt);
    }

    public int get() {
        return this.max;
    }
}
