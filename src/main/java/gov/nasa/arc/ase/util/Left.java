package gov.nasa.arc.ase.util;

public class Left {
    public static String format(String paramString, int paramInt) {
        while (paramString.length() < paramInt)
            paramString = paramString + " ";
        return paramString;
    }

    public static String format(int paramInt1, int paramInt2) {
        String str = Integer.toString(paramInt1);
        while (str.length() < paramInt2)
            str = str + " ";
        return str;
    }
}
