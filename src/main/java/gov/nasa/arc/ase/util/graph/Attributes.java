package gov.nasa.arc.ase.util.graph;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

public class Attributes {
    private Hashtable ht;

    public Attributes() {
        this.ht = new Hashtable();
    }

    public Attributes(Attributes paramAttributes) {
        this.ht = new Hashtable();
        for (Enumeration enumeration = paramAttributes.ht.keys(); enumeration.hasMoreElements();) {
            Object object = enumeration.nextElement();
            this.ht.put(object, paramAttributes.ht.get(object));
        }
    }

    public Attributes(String paramString) {
        this.ht = new Hashtable();
        if (paramString.equals("-"))
            return;
        StringTokenizer stringTokenizer = new StringTokenizer(paramString, ",");
        while (stringTokenizer.hasMoreTokens()) {
            String str2, str3, str1 = stringTokenizer.nextToken();
            int i = str1.indexOf("=");
            if (i == -1) {
                str2 = str1;
                str3 = "";
            } else {
                str2 = str1.substring(0, i);
                str3 = str1.substring(i + 1);
            }
            this.ht.put(str2, str3);
        }
    }

    public int getInt(String paramString) {
        Object object = this.ht.get(paramString);
        if (object == null)
            return 0;
        try {
            return Integer.parseInt((String) object);
        } catch (NumberFormatException numberFormatException) {
            return 0;
        }
    }

    public String getString(String paramString) {
        return (String) this.ht.get(paramString);
    }

    public boolean getBoolean(String paramString) {
        return (this.ht.get(paramString) != null);
    }

    public void setInt(String paramString, int paramInt) {
        this.ht.put(paramString, Integer.toString(paramInt));
    }

    public void setString(String paramString1, String paramString2) {
        this.ht.put(paramString1, paramString2);
    }

    public void setBoolean(String paramString, boolean paramBoolean) {
        if (paramBoolean) {
            this.ht.put(paramString, "");
        } else {
            this.ht.remove(paramString);
        }
    }

    public void unset(String paramString) {
        this.ht.remove(paramString);
    }

    public String toString() {
        if (this.ht.size() == 0)
            return "-";
        StringBuffer stringBuffer = new StringBuffer();
        for (Enumeration enumeration = this.ht.keys(); enumeration.hasMoreElements();) {
            Object object = enumeration.nextElement();
            String str = (String) this.ht.get(object);
            stringBuffer.append(object);
            if (!str.equals("")) {
                stringBuffer.append('=');
                stringBuffer.append(str);
            }
            if (enumeration.hasMoreElements())
                stringBuffer.append(',');
        }
        return stringBuffer.toString();
    }
}
