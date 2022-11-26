package gov.nasa.arc.ase.util;

public class HashData {
    private int poly = -2004316433;
    private int m = -1;

    public void add(int paramInt) {
        if (this.m < 0) {
            this.m += this.m;
            this.m ^= this.poly;
        } else {
            this.m += this.m;
        }
        this.m ^= paramInt;
    }

    public int getValue() {
        return this.m >>> 4 ^ this.m & 0xF;
    }
}
