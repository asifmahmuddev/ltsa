package gov.nasa.arc.ase.util;

import java.util.Hashtable;
import java.util.TreeSet;

public class HashPool {
    private Hashtable pool;
    private int size;
    private int length;
    private Object[] objects;
    private static final int DELTA = 10;

    public class PoolEntry implements Comparable {
        private Object obj;
        private int idx;
        private final HashPool this$0;

        public PoolEntry(HashPool this$0, Object param1Object, int param1Int) {
            this.this$0 = this$0;
            this.obj = param1Object;
            this.idx = param1Int;
        }

        public Object getObject() {
            return this.obj;
        }

        public int getIndex() {
            return this.idx;
        }

        public int hashCode() {
            return this.idx;
        }

        public boolean equals(Object param1Object) {
            PoolEntry poolEntry = (PoolEntry) param1Object;
            return (poolEntry.idx == this.idx);
        }

        public String toString() {
            return this.idx + " => " + this.obj;
        }

        public int compareTo(Object param1Object) {
            PoolEntry poolEntry = (PoolEntry) param1Object;
            return this.idx - poolEntry.idx;
        }
    }

    public HashPool() {
        this.pool = new Hashtable();
        this.objects = new Object[this.length = 10];
        this.objects[0] = null;
        this.size = 1;
    }

    public synchronized PoolEntry put(Object paramObject) {
        PoolEntry poolEntry = (PoolEntry) this.pool.get(paramObject);
        if (poolEntry != null)
            return poolEntry;
        if (this.length < this.size + 1) {
            Object[] arrayOfObject = new Object[this.length + 10];
            System.arraycopy(this.objects, 0, arrayOfObject, 0, this.length);
            this.objects = arrayOfObject;
            this.length += 10;
        }
        this.objects[this.size] = paramObject;
        this.pool.put(paramObject, poolEntry = new PoolEntry(this, paramObject, this.size++));
        return poolEntry;
    }

    public Object get(Object paramObject) {
        if (paramObject == null)
            return null;
        PoolEntry poolEntry = (PoolEntry) this.pool.get(paramObject);
        if (poolEntry != null)
            return poolEntry.obj;
        return (put(paramObject)).obj;
    }

    public Object getObject(int paramInt) {
        return this.objects[paramInt];
    }

    public PoolEntry getEntry(Object paramObject) {
        if (paramObject == null)
            return null;
        PoolEntry poolEntry = (PoolEntry) this.pool.get(paramObject);
        if (poolEntry != null)
            return poolEntry;
        return put(paramObject);
    }

    public int getIndex(Object paramObject) {
        if (paramObject == null)
            return -1;
        PoolEntry poolEntry = (PoolEntry) this.pool.get(paramObject);
        if (poolEntry != null)
            return poolEntry.idx;
        return (put(paramObject)).idx;
    }

    public synchronized int size() {
        return this.size;
    }

    public synchronized void print() {
        System.out.println("{");
        TreeSet treeSet = new TreeSet(this.pool.values());
        for (String str : treeSet)
            System.out.println("\t" + str);
        System.out.println("}");
    }
}
