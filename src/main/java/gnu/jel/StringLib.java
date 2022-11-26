package gnu.jel;

public class StringLib {
    public static char charAt(String paramString, int paramInt) {
        return paramString.charAt(paramInt);
    }

    public static int compareTo(String paramString1, String paramString2) {
        return paramString1.compareTo(paramString2);
    }

    public static String concat(String paramString1, String paramString2) {
        return paramString1.concat(paramString2);
    }

    public static boolean endsWith(String paramString1, String paramString2) {
        return paramString1.endsWith(paramString2);
    }

    public static boolean equals(String paramString, Object paramObject) {
        return paramString.equals(paramObject);
    }

    public static int indexOf(String paramString, int paramInt) {
        return paramString.indexOf(paramInt);
    }

    public static int indexOf(String paramString, int paramInt1, int paramInt2) {
        return paramString.indexOf(paramInt1, paramInt2);
    }

    public static int indexOf(String paramString1, String paramString2) {
        return paramString1.indexOf(paramString2);
    }

    public static int indexOf(String paramString1, String paramString2, int paramInt) {
        return paramString1.indexOf(paramString2, paramInt);
    }

    public static String intern(String paramString) {
        return paramString.intern();
    }

    public static int lastIndexOf(String paramString, int paramInt) {
        return paramString.lastIndexOf(paramInt);
    }

    public static int lastIndexOf(String paramString, int paramInt1, int paramInt2) {
        return paramString.lastIndexOf(paramInt1, paramInt2);
    }

    public static int lastIndexOf(String paramString1, String paramString2) {
        return paramString1.lastIndexOf(paramString2);
    }

    public static int lastIndexOf(String paramString1, String paramString2, int paramInt) {
        return paramString1.lastIndexOf(paramString2, paramInt);
    }

    public static int length(String paramString) {
        return paramString.length();
    }

    public static boolean regionMatches(String paramString1, int paramInt1, String paramString2, int paramInt2, int paramInt3) {
        return paramString1.regionMatches(paramInt1, paramString2, paramInt2, paramInt3);
    }

    public static boolean regionMatches(boolean paramBoolean, String paramString1, int paramInt1, String paramString2, int paramInt2, int paramInt3) {
        return paramString1.regionMatches(paramBoolean, paramInt1, paramString2, paramInt2, paramInt3);
    }

    public static String replace(String paramString, char paramChar1, char paramChar2) {
        return paramString.replace(paramChar1, paramChar2);
    }

    public static boolean startsWith(String paramString1, String paramString2) {
        return paramString1.startsWith(paramString2);
    }

    public static boolean startsWith(String paramString1, String paramString2, int paramInt) {
        return paramString1.startsWith(paramString2, paramInt);
    }

    public static String substring(String paramString, int paramInt) {
        return paramString.substring(paramInt);
    }

    public static String substring(String paramString, int paramInt1, int paramInt2) {
        return paramString.substring(paramInt1, paramInt2);
    }

    public static String toLowerCase(String paramString) {
        return paramString.toLowerCase();
    }

    public static String toUpperCase(String paramString) {
        return paramString.toUpperCase();
    }

    public static String trim(String paramString) {
        return paramString.trim();
    }

    public static String valueOf(char paramChar) {
        return String.valueOf(paramChar);
    }

    public static String valueOf(double paramDouble) {
        return String.valueOf(paramDouble);
    }

    public static String valueOf(float paramFloat) {
        return String.valueOf(paramFloat);
    }

    public static String valueOf(int paramInt) {
        return String.valueOf(paramInt);
    }

    public static String valueOf(long paramLong) {
        return String.valueOf(paramLong);
    }

    public static String valueOf(Object paramObject) {
        return String.valueOf(paramObject);
    }

    public static String valueOf(boolean paramBoolean) {
        return String.valueOf(paramBoolean);
    }
}
