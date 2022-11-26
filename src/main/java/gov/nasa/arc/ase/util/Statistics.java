package gov.nasa.arc.ase.util;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

public class Statistics {
    private Hashtable ht = new Hashtable();

    public void set(Category paramCategory, Object paramObject) {
        if (this.ht.containsKey(paramCategory))
            this.ht.remove(paramCategory);
        this.ht.put(paramCategory, paramObject);
    }

    public void set(String paramString, Object paramObject) {
        set(new Category(paramString), paramObject);
    }

    public Object get(Category paramCategory) {
        if (this.ht.containsKey(paramCategory))
            return this.ht.get(paramCategory);
        return null;
    }

    public Object get(String paramString) {
        return get(new Category(paramString));
    }

    public void print() {
        ArrayList arrayList = new ArrayList(this.ht.entrySet());
        Collections.sort(arrayList, new Comparator(this) {
            private final Statistics this$0;

            public int compare(Object param1Object1, Object param1Object2) {
                Map.Entry entry1 = (Map.Entry) param1Object1;
                Map.Entry entry2 = (Map.Entry) param1Object2;
                return ((Comparable) entry1.getKey()).compareTo(entry2.getKey());
            }
        });
        Category category = null;
        for (Map.Entry entry : arrayList) {
            Object object = entry.getValue();
            Category category1 = (Category) entry.getKey();
            category1.print(category);
            if (object != null)
                Debug.print(1, ": " + object);
            Debug.println(1);
            category = category1;
        }
    }

    public void save(PrintStream paramPrintStream) {
        Set set = this.ht.entrySet();
        for (Map.Entry entry : set) {
            Object object = entry.getValue();
            Category category = (Category) entry.getKey();
            paramPrintStream.print(category);
            if (object != null)
                paramPrintStream.print(": " + object);
            paramPrintStream.println();
        }
    }
}
