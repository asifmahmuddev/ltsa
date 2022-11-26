package ic.doc.ltsa.lts;

import java.util.Vector;

class PrefixTree {
    String name;
    int value;
    boolean isInt = false;
    PrefixTree subname = null;
    PrefixTree list = null;
    boolean lastprefix = false;

    PrefixTree(String paramString) {
        this.name = paramString;
        checkInt();
    }

    static PrefixTree addName(PrefixTree paramPrefixTree, String paramString) {
        if (paramPrefixTree == null)
            paramPrefixTree = new PrefixTree(prefix(paramString, 0));
        paramPrefixTree.add(paramString, 0);
        return paramPrefixTree;
    }

    private void add(String paramString, int paramInt) {
        String str = prefix(paramString, paramInt);
        if (str == null)
            return;
        if (str.equals(this.name) && !this.lastprefix) {
            String str1 = prefix(paramString, paramInt + 1);
            if (str1 == null) {
                this.lastprefix = true;
                return;
            }
            if (this.subname == null)
                this.subname = new PrefixTree(str1);
            this.subname.add(paramString, paramInt + 1);
        } else {
            if (this.list == null)
                this.list = new PrefixTree(str);
            this.list.add(paramString, paramInt);
        }
    }

    public static boolean equals(PrefixTree paramPrefixTree1, PrefixTree paramPrefixTree2) {
        if (paramPrefixTree1 == paramPrefixTree2)
            return true;
        if (paramPrefixTree1 == null || paramPrefixTree2 == null)
            return false;
        if (!paramPrefixTree1.name.equals(paramPrefixTree2.name))
            return false;
        return (equals(paramPrefixTree1.subname, paramPrefixTree2.subname) && equals(paramPrefixTree1.list, paramPrefixTree2.list));
    }

    PrefixTree[] getSubLists() {
        Vector vector = new Vector();
        PrefixTree prefixTree1 = this;
        PrefixTree prefixTree2 = this.list;
        vector.addElement(prefixTree1);
        while (prefixTree2 != null) {
            if (!equals(prefixTree1.subname, prefixTree2.subname) || prefixTree1.isInt != prefixTree2.isInt) {
                vector.addElement(prefixTree2);
                prefixTree1 = prefixTree2;
            }
            prefixTree2 = prefixTree2.list;
        }
        vector.addElement(null);
        PrefixTree[] arrayOfPrefixTree = new PrefixTree[vector.size()];
        vector.copyInto((Object[]) arrayOfPrefixTree);
        return arrayOfPrefixTree;
    }

    void checkInt() {
        try {
            this.value = Integer.parseInt(this.name);
            this.isInt = true;
        } catch (NumberFormatException numberFormatException) {
        }
    }

    static String prefix(String paramString, int paramInt) {
        int i = 0;
        for (byte b = 0; b < paramInt; b++) {
            i = paramString.indexOf('.', i);
            if (i < 0)
                return null;
            i++;
        }
        int j = paramString.indexOf('.', i);
        if (j < 0)
            return paramString.substring(i);
        return paramString.substring(i, j);
    }

    public void getStrings(Vector paramVector, int paramInt, String paramString) {
        PrefixTree prefixTree = this;
        while (prefixTree != null) {
            String str;
            if (paramString == null) {
                str = prefixTree.item();
            } else {
                str = paramString + dotted(prefixTree.item());
            }
            if (prefixTree.subname == null) {
                paramVector.addElement(str);
            } else if (paramInt > 0) {
                prefixTree.subname.getStrings(paramVector, paramInt - 1, str);
            } else {
                paramVector.addElement(str + dotted(prefixTree.subname.toString()));
            }
            prefixTree = prefixTree.list;
        }
    }

    public int maxDepth() {
        PrefixTree prefixTree = this;
        int i = 0;
        while (prefixTree != null) {
            if (prefixTree.subname == null) {
                i = Math.max(i, 1);
            } else {
                i = Math.max(1 + prefixTree.subname.maxDepth(), i);
            }
            prefixTree = prefixTree.list;
        }
        return i;
    }

    public String toString() {
        String str;
        PrefixTree[] arrayOfPrefixTree = getSubLists();
        if (arrayOfPrefixTree.length > 2) {
            str = "{";
        } else {
            str = "";
        }
        for (byte b = 0; b < arrayOfPrefixTree.length - 1; b++) {
            if (b < arrayOfPrefixTree.length - 2) {
                str = str + listString(arrayOfPrefixTree[b], arrayOfPrefixTree[b + 1]) + ", ";
            } else {
                str = str + listString(arrayOfPrefixTree[b], arrayOfPrefixTree[b + 1]);
            }
        }
        return (arrayOfPrefixTree.length > 2) ? (str + "}") : str;
    }

    static String listString(PrefixTree paramPrefixTree1, PrefixTree paramPrefixTree2) {
        String str;
        if (paramPrefixTree1.list == paramPrefixTree2) {
            str = paramPrefixTree1.item();
        } else if (intRange(paramPrefixTree1, paramPrefixTree2)) {
            str = rangeString(paramPrefixTree1, paramPrefixTree2);
        } else {
            str = "{" + paramPrefixTree1.item();
            PrefixTree prefixTree = paramPrefixTree1.list;
            while (prefixTree != paramPrefixTree2) {
                str = str + ", " + prefixTree.item();
                prefixTree = prefixTree.list;
            }
            str = str + "}";
        }
        if (paramPrefixTree1.subname != null)
            return str + dotted(paramPrefixTree1.subname.toString());
        return str;
    }

    private static String dotted(String paramString) {
        if (paramString.charAt(0) == '[')
            return paramString;
        return "." + paramString;
    }

    String item() {
        if (this.isInt)
            return "[" + this.name + "]";
        return this.name;
    }

    static boolean intRange(PrefixTree paramPrefixTree1, PrefixTree paramPrefixTree2) {
        PrefixTree prefixTree = paramPrefixTree1;
        while (prefixTree != paramPrefixTree2) {
            if (!prefixTree.isInt)
                return false;
            prefixTree = prefixTree.list;
        }
        return true;
    }

    static String rangeString(PrefixTree paramPrefixTree1, PrefixTree paramPrefixTree2) {
        PrefixTree prefixTree = paramPrefixTree1;
        byte b1 = 0;
        while (prefixTree != paramPrefixTree2) {
            prefixTree = prefixTree.list;
            b1++;
        }
        int[] arrayOfInt = new int[b1];
        prefixTree = paramPrefixTree1;
        for (byte b2 = 0; b2 < arrayOfInt.length;) {
            arrayOfInt[b2] = prefixTree.value;
            prefixTree = prefixTree.list;
            b2++;
        }
        sort(arrayOfInt);
        if (isOneRange(arrayOfInt))
            return "[" + arrayOfInt[0] + ".." + arrayOfInt[arrayOfInt.length - 1] + "]";
        int i = 0;
        String str = "{";
        while (i < arrayOfInt.length) {
            int j;
            for (j = i; j < arrayOfInt.length - 1 && arrayOfInt[j + 1] - arrayOfInt[j] == 1; j++);
            if (j == i) {
                str = str + "[" + arrayOfInt[i] + "]";
            } else {
                str = str + "[" + arrayOfInt[i] + ".." + arrayOfInt[j] + "]";
            }
            i = j + 1;
            if (i < arrayOfInt.length)
                str = str + ", ";
        }
        str = str + "}";
        return str;
    }

    private static boolean isOneRange(int[] paramArrayOfint) {
        for (byte b = 0; b < paramArrayOfint.length - 1; b++) {
            if (paramArrayOfint[b + 1] - paramArrayOfint[b] != 1)
                return false;
        }
        return true;
    }

    private static void sort(int[] paramArrayOfint) {
        for (byte b = 0; b < paramArrayOfint.length - 1; b++) {
            int i = b;
            for (int j = b + 1; j < paramArrayOfint.length; j++) {
                if (paramArrayOfint[j] < paramArrayOfint[i])
                    i = j;
            }
            int k = paramArrayOfint[b];
            paramArrayOfint[b] = paramArrayOfint[i];
            paramArrayOfint[i] = k;
        }
    }
}
