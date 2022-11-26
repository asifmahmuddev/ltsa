package com.sun.xml.parser;

import java.util.Enumeration;

final class SimpleHashtable implements Enumeration {
    private Entry[] table;
    private Entry current = null;
    private int currentBucket = 0;
    private int count;
    private int threshold;
    private static final float loadFactor = 0.75F;

    public SimpleHashtable(int paramInt) {
        if (paramInt < 0)
            throw new IllegalArgumentException("Illegal Capacity: " + paramInt);
        if (paramInt == 0)
            paramInt = 1;
        this.table = new Entry[paramInt];
        this.threshold = (int) (paramInt * 0.75F);
    }

    public SimpleHashtable() {
        this(11);
    }

    public void clear() {
        this.count = 0;
        this.currentBucket = 0;
        this.current = null;
        for (byte b = 0; b < this.table.length; b++)
            this.table[b] = null;
    }

    public int size() {
        return this.count;
    }

    public Enumeration keys() {
        this.currentBucket = 0;
        this.current = null;
        return this;
    }

    public boolean hasMoreElements() {
        if (this.current != null)
            return true;
        while (this.currentBucket < this.table.length) {
            this.current = this.table[this.currentBucket++];
            if (this.current != null)
                return true;
        }
        return false;
    }

    public Object nextElement() {
        if (this.current == null)
            throw new IllegalStateException();
        Object object = this.current.key;
        this.current = this.current.next;
        return object;
    }

    public Object get(String paramString) {
        Entry[] arrayOfEntry = this.table;
        int i = paramString.hashCode();
        int j = (i & Integer.MAX_VALUE) % arrayOfEntry.length;
        for (Entry entry = arrayOfEntry[j]; entry != null; entry = entry.next) {
            if (entry.hash == i && entry.key == paramString)
                return entry.value;
        }
        return null;
    }

    public Object getNonInterned(String paramString) {
        Entry[] arrayOfEntry = this.table;
        int i = paramString.hashCode();
        int j = (i & Integer.MAX_VALUE) % arrayOfEntry.length;
        for (Entry entry = arrayOfEntry[j]; entry != null; entry = entry.next) {
            if (entry.hash == i && entry.key.equals(paramString))
                return entry.value;
        }
        return null;
    }

    private void rehash() {
        int i = this.table.length;
        Entry[] arrayOfEntry1 = this.table;
        int j = i * 2 + 1;
        Entry[] arrayOfEntry2 = new Entry[j];
        this.threshold = (int) (j * 0.75F);
        this.table = arrayOfEntry2;
        for (int k = i; k-- > 0;) {
            for (Entry entry = arrayOfEntry1[k]; entry != null;) {
                Entry entry1 = entry;
                entry = entry.next;
                int m = (entry1.hash & Integer.MAX_VALUE) % j;
                entry1.next = arrayOfEntry2[m];
                arrayOfEntry2[m] = entry1;
            }
        }
    }

    public Object put(Object paramObject1, Object paramObject2) {
        if (paramObject2 == null)
            throw new NullPointerException();
        Entry[] arrayOfEntry = this.table;
        int i = paramObject1.hashCode();
        int j = (i & Integer.MAX_VALUE) % arrayOfEntry.length;
        for (Entry entry1 = arrayOfEntry[j]; entry1 != null; entry1 = entry1.next) {
            if (entry1.hash == i && entry1.key == paramObject1) {
                Object object = entry1.value;
                entry1.value = paramObject2;
                return object;
            }
        }
        if (this.count >= this.threshold) {
            rehash();
            arrayOfEntry = this.table;
            j = (i & Integer.MAX_VALUE) % arrayOfEntry.length;
        }
        Entry entry2 = new Entry(i, paramObject1, paramObject2, arrayOfEntry[j]);
        arrayOfEntry[j] = entry2;
        this.count++;
        return null;
    }

    private static class Entry {
        int hash;
        Object key;
        Object value;
        Entry next;

        protected Entry(int param1Int, Object param1Object1, Object param1Object2, Entry param1Entry) {
            this.hash = param1Int;
            this.key = param1Object1;
            this.value = param1Object2;
            this.next = param1Entry;
        }
    }
}
