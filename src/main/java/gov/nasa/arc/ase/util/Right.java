package gov.nasa.arc.ase.util;

public class Right {
    public static String format(String paramString, int paramInt, char paramChar) {
        while (paramString.length() < paramInt)
            paramString = paramChar + paramString;
        return paramString;
    }

    public static String format(String paramString, int paramInt) {
        return format(paramString, paramInt, ' ');
    }

    public static String format(int paramInt1, int paramInt2) {
        String str = Integer.toString(paramInt1);
        while (str.length() < paramInt2)
            str = " " + str;
        return str;
    }
}
