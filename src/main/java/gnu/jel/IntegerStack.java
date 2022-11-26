package gnu.jel;

class IntegerStack {
    private int[] data;
    private int count = 0;

    public IntegerStack() {
        this(30);
    }

    public IntegerStack(int paramInt) {
        this.data = new int[paramInt];
    }

    private synchronized void increaseCapacity(int paramInt) {
        int[] arrayOfInt = this.data;
        int i = this.data.length;
        int j = i * 2;
        if (j < paramInt)
            j = paramInt;
        this.data = new int[j];
        System.arraycopy(arrayOfInt, 0, this.data, 0, this.count);
    }

    public final int peek() {
        return this.data[this.count - 1];
    }

    public final int pop() {
        return this.data[--this.count];
    }

    public final void pop_throw() {
        this.count--;
    }

    public final void push(int paramInt) {
        if (this.count >= this.data.length)
            increaseCapacity(this.count + 1);
        this.data[this.count++] = paramInt;
    }

    public final int size() {
        return this.count;
    }

    public static void swap(IntegerStack paramIntegerStack1, int paramInt1, IntegerStack paramIntegerStack2, int paramInt2) {
        int i = paramIntegerStack1.count - paramInt1;
        int j = paramIntegerStack2.count - paramInt2;
        boolean bool1 = (i <= 0) ? false : true;
        boolean bool2 = (j <= 0) ? false : true;
        if (bool1 || bool2) {
            int k = paramInt1 + j;
            int m = paramInt2 + i;
            if (k > paramIntegerStack1.data.length)
                paramIntegerStack1.increaseCapacity(k);
            if (m > paramIntegerStack2.data.length)
                paramIntegerStack2.increaseCapacity(m);
            int[] arrayOfInt = null;
            if (bool1) {
                arrayOfInt = new int[i];
                System.arraycopy(paramIntegerStack1.data, paramInt1, arrayOfInt, 0, i);
            }
            if (bool2)
                System.arraycopy(paramIntegerStack2.data, paramInt2, paramIntegerStack1.data, paramInt1, j);
            if (bool1)
                System.arraycopy(arrayOfInt, 0, paramIntegerStack2.data, paramInt2, i);
            paramIntegerStack1.count = k;
            paramIntegerStack2.count = m;
        }
    }
}
