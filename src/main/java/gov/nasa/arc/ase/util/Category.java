package gov.nasa.arc.ase.util;

import java.io.PrintStream;
import java.util.Vector;

public class Category implements Comparable {
    private String[] categories;
    private int length;
    private int delta;

    public Category() {
        this.categories = new String[0];
        this.length = 0;
        this.delta = 1;
    }

    public Category(Category paramCategory) {
        int i = paramCategory.categories.length;
        this.categories = new String[i];
        System.arraycopy(paramCategory.categories, 0, this.categories, 0, i);
        this.length = i;
        this.delta = 1;
    }

    public Category(String paramString) {
        parseFromString(paramString);
    }

    public Category(int paramInt) {
        if (paramInt < 0)
            throw new IllegalArgumentException("size < 0");
        this.categories = new String[paramInt];
        this.length = 0;
        this.delta = 1;
    }

    public Category(int paramInt1, int paramInt2) {
        if (paramInt1 < 0)
            throw new IllegalArgumentException("size < 0");
        if (paramInt2 <= 0)
            throw new IllegalArgumentException("delta <= 0");
        this.categories = new String[paramInt1];
        this.length = 0;
        this.delta = paramInt2;
    }

    public Category(Category paramCategory, int paramInt) {
        if (paramInt <= 0)
            throw new IllegalArgumentException("delta <= 0");
        int i = paramCategory.categories.length;
        this.categories = new String[i];
        System.arraycopy(paramCategory.categories, 0, this.categories, 0, i);
        this.length = i;
        this.delta = paramInt;
    }

    public boolean equals(Object paramObject) {
        Category category = (Category) paramObject;
        String[] arrayOfString1 = this.categories;
        String[] arrayOfString2 = category.categories;
        int i = arrayOfString1.length;
        int j = arrayOfString2.length;
        byte b = 0;
        while (true) {
            if ((((b < i) ? 1 : 0) & ((b < j) ? 1 : 0)) == 0)
                break;
            if (!arrayOfString1[b].equals(arrayOfString2[b]))
                return false;
            b++;
        }
        return true;
    }

    protected void grow() {
        String[] arrayOfString = new String[this.length + this.delta];
        System.arraycopy(this.categories, 0, arrayOfString, 0, this.length);
        this.categories = arrayOfString;
    }

    public void down(String paramString) {
        if (this.length >= this.categories.length)
            grow();
        this.categories[this.length++] = paramString;
    }

    public String up() {
        if (this.length == 0)
            throw new IndexOutOfBoundsException("length == 0");
        return this.categories[--this.length];
    }

    public void print(Category paramCategory) {
        boolean bool = false;
        if (this.length == 0) {
            Debug.print(1, "/");
            return;
        }
        for (byte b = 0; b < this.length; b++) {
            if (b == this.length - 1) {
                for (byte b1 = 0; b1 < b; b1++)
                    Debug.print(1, "  ");
                Debug.print(1, this.categories[b].toString());
            } else if (bool || paramCategory == null) {
                for (byte b1 = 0; b1 < b; b1++)
                    Debug.print(1, "  ");
                Debug.println(1, this.categories[b].toString());
            } else if (!this.categories[b].equals(paramCategory.categories[b])) {
                bool = true;
                for (byte b1 = 0; b1 < b; b1++)
                    Debug.print(1, "  ");
                Debug.println(1, this.categories[b].toString());
            }
        }
    }

    public void save(PrintStream paramPrintStream, Category paramCategory) {
        boolean bool = false;
        if (this.length == 0) {
            paramPrintStream.print("/");
            return;
        }
        for (byte b = 0; b < this.length; b++) {
            if (b == this.length - 1) {
                for (byte b1 = 0; b1 < b; b1++)
                    paramPrintStream.print("  ");
                paramPrintStream.print(this.categories[b].toString());
            } else if (bool || paramCategory == null) {
                for (byte b1 = 0; b1 < b; b1++)
                    paramPrintStream.print("  ");
                paramPrintStream.println(this.categories[b].toString());
            } else if (b >= paramCategory.length || !this.categories[b].equals(paramCategory.categories[b])) {
                bool = true;
                for (byte b1 = 0; b1 < b; b1++)
                    paramPrintStream.print("  ");
                paramPrintStream.println(this.categories[b].toString());
            }
        }
    }

    public String toString() {
        if (this.length == 0)
            return "/";
        StringBuffer stringBuffer = new StringBuffer();
        for (byte b = 0; b < this.length; b++) {
            stringBuffer.append("/");
            stringBuffer.append(this.categories[b]);
        }
        return stringBuffer.toString();
    }

    public int hashCode() {
        int i = 0;
        for (byte b = 0; b < this.length; b++)
            i += this.categories[b].hashCode();
        return i;
    }

    protected void parseFromString(String paramString) {
        if (!paramString.startsWith("/"))
            throw new IllegalArgumentException("category should start with /");
        int i = 1, j = paramString.length();
        Vector vector = new Vector();
        while (i < j) {
            int k = paramString.indexOf('/', i);
            if (k == -1)
                k = j;
            vector.addElement(paramString.substring(i, k));
            i = k + 1;
        }
        this.length = vector.size();
        this.categories = new String[this.length];
        vector.toArray(this.categories);
        this.delta = 1;
    }

    public int compareTo(Object paramObject) {
        Category category = (Category) paramObject;
        String[] arrayOfString1 = this.categories;
        String[] arrayOfString2 = category.categories;
        int i = arrayOfString1.length;
        int j = arrayOfString2.length;
        byte b = 0;
        while (true) {
            if ((((b < i) ? 1 : 0) & ((b < j) ? 1 : 0)) == 0)
                break;
            int k = arrayOfString1[b].compareTo(arrayOfString2[b]);
            if (k != 0)
                return k;
            b++;
        }
        return i - j;
    }
}
