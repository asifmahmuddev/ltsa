package gov.nasa.arc.ase.util;

public class ArrayBSet implements BSet {
    private static final int SHIFT = 5;
    private static final int REMIDER = 32;
    private int[] array;
    private int size;

    public ArrayBSet(boolean[] paramArrayOfboolean) {
        this.size = paramArrayOfboolean.length;
        this.array = new int[this.size + 31 >> 5];
        for (byte b = 0; b < this.size; b++) {
            if (paramArrayOfboolean[b])
                set(b);
        }
    }

    private void set(int paramInt) {
        this.array[paramInt >> 5] = this.array[paramInt >> 5] | 1 << paramInt % 32;
    }

    public boolean get(int paramInt) {
        if (paramInt < 0)
            throw new IndexOutOfBoundsException(paramInt + " < 0");
        if (paramInt >= this.size)
            throw new IndexOutOfBoundsException(paramInt + " >= " + this.size);
        return ((this.array[paramInt >> 5] & 1 << paramInt % 32) != 0);
    }

    public boolean equals(Object paramObject) {
        if (paramObject == null)
            return false;
        if (!(paramObject instanceof ArrayBSet))
            return false;
        ArrayBSet arrayBSet = (ArrayBSet) paramObject;
        if (this.size != arrayBSet.size)
            return false;
        byte b;
        int i;
        for (b = 0, i = this.array.length; b < i; b++) {
            if (this.array[b] != arrayBSet.array[b])
                return false;
        }
        return true;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        byte b;
        int i;
        for (b = 0, i = this.size; b < i; b++) {
            if (get(b)) {
                stringBuffer.append('R');
            } else {
                stringBuffer.append('-');
            }
        }
        return stringBuffer.toString();
    }
}
