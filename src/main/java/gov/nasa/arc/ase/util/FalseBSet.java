package gov.nasa.arc.ase.util;

public class FalseBSet implements BSet {
    public boolean get(int paramInt) {
        return false;
    }

    public boolean equals(Object paramObject) {
        if (paramObject == null)
            return false;
        if (!(paramObject instanceof FalseBSet))
            return false;
        return true;
    }

    public String toString() {
        return "-";
    }
}
