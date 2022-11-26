package gov.nasa.arc.ase.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

public class Cache {
    public static interface Listener {
        void elementRemoved(Object param1Object);
    }

    private class EntryComparator implements Comparator {
        private final Cache this$0;

        private EntryComparator(Cache this$0) {
            Cache.this = Cache.this;
        }

        public int compare(Object param1Object1, Object param1Object2) {
            Map.Entry entry1 = (Map.Entry) param1Object1;
            Map.Entry entry2 = (Map.Entry) param1Object2;
            Cache.Index index1 = (Cache.Index) entry1.getValue();
            Cache.Index index2 = (Cache.Index) entry2.getValue();
            return index1.cnt - index2.cnt;
        }
    }

    private class Index {
        int cnt;
        private final Cache this$0;

        public Index(Cache this$0) {
            this.this$0 = this$0;
            this.cnt = 0;
        }

        public Index(Cache this$0, int param1Int) {
            this.this$0 = this$0;
            this.cnt = param1Int;
        }

        public Object clone() {
            return new Index(this.this$0, this.cnt);
        }

        public boolean equals(Object param1Object) {
            return (((Index) param1Object).cnt == this.cnt);
        }

        public String toString() {
            return (new Integer(this.cnt)).toString();
        }
    }

    private Hashtable ht = new Hashtable();
    private Index lowest = new Index(this, 0);
    private Index highest = new Index(this, 0);
    private int count = 0;
    private int size;
    private Listener listener;

    public Cache(int paramInt) {
        this.size = paramInt;
        this.listener = null;
    }

    public Cache(int paramInt, Listener paramListener) {
        this.size = paramInt;
        this.listener = paramListener;
    }

    public boolean put(Object paramObject) {
        if (this.ht.containsKey(paramObject)) {
            Index index = (Index) this.ht.get(paramObject);
            if (index.cnt != this.highest.cnt - 1) {
                int i = index.cnt;
                index.cnt = this.highest.cnt;
                if (i == this.lowest.cnt)
                    this.lowest.cnt = lowestValue();
                next();
            }
            return false;
        }
        if (this.count == this.size) {
            removeLowest();
        } else {
            this.count++;
        }
        this.ht.put(paramObject, new Index(this, this.highest.cnt));
        next();
        return true;
    }

    public String toString() {
        return this.ht.toString();
    }

    public void setSize(int paramInt) {
        if (paramInt > this.count) {
            this.size = paramInt;
        } else {
            while (this.count > paramInt) {
                removeLowest();
                this.count--;
            }
            this.size = paramInt;
        }
    }

    private void next() {
        this.highest.cnt++;
        if (this.highest.cnt < 0) {
            ArrayList arrayList = new ArrayList(this.ht.entrySet());
            Collections.sort(arrayList, new EntryComparator());
            byte b = 0;
            this.ht = new Hashtable();
            for (Map.Entry entry : arrayList) {
                this.ht.put(entry.getKey(), new Index(this, b));
                b++;
            }
            this.lowest.cnt = 0;
            this.highest.cnt = arrayList.size();
        }
    }

    private Index getValue(Index paramIndex) {
        for (Enumeration enumeration = this.ht.keys(); enumeration.hasMoreElements();) {
            Object object = enumeration.nextElement();
            Object object1 = this.ht.get(object);
            if (object1.equals(paramIndex))
                return (Index) object1;
        }
        return null;
    }

    private Object getKey(Index paramIndex) {
        for (Enumeration enumeration = this.ht.keys(); enumeration.hasMoreElements();) {
            Object object = enumeration.nextElement();
            Object object1 = this.ht.get(object);
            if (object1.equals(paramIndex))
                return object;
        }
        return null;
    }

    private int lowestValue() {
        int i = 0;
        boolean bool = false;
        for (Enumeration enumeration = this.ht.elements(); enumeration.hasMoreElements();) {
            Index index = enumeration.nextElement();
            if (!bool || index.cnt < i) {
                bool = true;
                i = index.cnt;
            }
        }
        return i;
    }

    private void removeLowest() {
        Object object = getKey(this.lowest);
        this.ht.remove(object);
        this.lowest.cnt = lowestValue();
        if (this.count == 1)
            this.highest.cnt = 0;
        if (this.listener != null)
            this.listener.elementRemoved(object);
    }
}
