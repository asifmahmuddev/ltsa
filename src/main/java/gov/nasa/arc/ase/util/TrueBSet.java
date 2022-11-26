package gov.nasa.arc.ase.util;

public class TrueBSet implements BSet {
    public boolean get(int paramInt) {
        return true;
    }

    public boolean equals(Object paramObject) {
        if (paramObject == null)
            return false;
        if (!(paramObject instanceof TrueBSet))
            return false;
        return true;
    }

    public String toString() {
        return "R";
    }
}
