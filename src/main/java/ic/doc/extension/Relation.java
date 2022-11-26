package ic.doc.extension;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class Relation extends Hashtable {
    boolean is_relation = false;

    public Object put(Object paramObject1, Object paramObject2) {
        if (!containsKey(paramObject1))
            return super.put(paramObject1, paramObject2);
        if (!this.is_relation)
            this.is_relation = true;
        V v = get(paramObject1);
        if (v instanceof Vector) {
            Vector vector = (Vector) v;
            if (!vector.contains(paramObject2))
                vector.addElement(paramObject2);
        } else {
            Vector vector = new Vector(4);
            vector.addElement(v);
            if (!vector.equals(v))
                vector.addElement((V) paramObject2);
            super.put(paramObject1, vector);
        }
        return v;
    }

    public boolean isRelation() {
        return this.is_relation;
    }

    public Relation inverse() {
        Relation relation = new Relation();
        Enumeration enumeration = keys();
        while (enumeration.hasMoreElements()) {
            K k = enumeration.nextElement();
            V v = get(k);
            if (!(v instanceof Vector)) {
                relation.put(v, k);
                continue;
            }
            Enumeration enumeration1 = ((Vector) v).elements();
            while (enumeration1.hasMoreElements())
                relation.put(enumeration1.nextElement(), k);
        }
        return relation;
    }

    public void union(Relation paramRelation) {
        if (paramRelation == null)
            return;
        Enumeration enumeration = paramRelation.keys();
        while (enumeration.hasMoreElements()) {
            K k = enumeration.nextElement();
            V v = paramRelation.get(k);
            putValues(k, v);
        }
    }

    public void relabel(Relation paramRelation) {
        Enumeration enumeration = keys();
        while (enumeration.hasMoreElements()) {
            String str = (String) enumeration.nextElement();
            V v = get(str);
            if (paramRelation.containsKey(str)) {
                V v1 = paramRelation.get(str);
                remove(str);
                if (!(v1 instanceof Vector)) {
                    putValues(v1, v);
                    continue;
                }
                Enumeration enumeration1 = ((Vector) v1).elements();
                while (enumeration1.hasMoreElements())
                    putValues(enumeration1.nextElement(), v);
                continue;
            }
            if (hasPrefix(str, paramRelation)) {
                V v1 = paramRelation.get(prefix(str, paramRelation));
                if (!(v1 instanceof Vector)) {
                    String str1 = prefixReplace(str, (String) v1, paramRelation);
                    putValues(str1, v);
                    continue;
                }
                Enumeration enumeration1 = ((Vector) v1).elements();
                while (enumeration1.hasMoreElements()) {
                    String str1 = prefixReplace(str, enumeration1.nextElement(), paramRelation);
                    putValues(str1, v);
                }
            }
        }
    }

    protected void putValues(Object paramObject1, Object paramObject2) {
        if (!(paramObject2 instanceof Vector)) {
            put(paramObject1, paramObject2);
        } else {
            Enumeration enumeration = ((Vector) paramObject2).elements();
            while (enumeration.hasMoreElements())
                put(paramObject1, enumeration.nextElement());
        }
    }

    private static String prefixReplace(String paramString1, String paramString2, Hashtable paramHashtable) {
        int i = maximalPrefix(paramString1, paramHashtable);
        if (i < 0)
            return paramString1;
        return paramString2 + paramString1.substring(i);
    }

    private static int maximalPrefix(String paramString, Hashtable paramHashtable) {
        int i = paramString.lastIndexOf('.');
        if (i < 0)
            return i;
        if (paramHashtable.containsKey(paramString.substring(0, i)))
            return i;
        return maximalPrefix(paramString.substring(0, i), paramHashtable);
    }

    private static boolean hasPrefix(String paramString, Hashtable paramHashtable) {
        return (maximalPrefix(paramString, paramHashtable) >= 0);
    }

    private static String prefix(String paramString, Hashtable paramHashtable) {
        return paramString.substring(0, maximalPrefix(paramString, paramHashtable));
    }
}
