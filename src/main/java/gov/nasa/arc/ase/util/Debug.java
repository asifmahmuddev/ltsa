package gov.nasa.arc.ase.util;

public class Debug {
    public static final int ERROR = 0;
    public static final int WARNING = 1;
    public static final int MESSAGE = 2;
    public static final int DEBUG = 3;
    private static final int LAST_LEVEL = 4;
    public static final int DEFAULT = 0;
    public static final int RACE = 1;
    public static final int LOCK_ORDER = 2;
    public static final int DEPEND = 3;
    public static final int DISTRIBUTED = 4;
    public static final int SEARCH = 5;
    public static final int TRACE = 6;
    private static final int LAST_KIND = 7;
    private static int[] enabled = new int[7];
    private static String[] levels = new String[]{"error", "warning", "message", "debug"};
    private static String[] kinds = new String[]{"default", "race", "lock-order", "depend", "distributed", "search", "trace"};

    public static int mapLevel(String paramString) {
        for (byte b = 0; b < 4; b++) {
            if (paramString.equals(levels[b]))
                return b;
        }
        return -1;
    }

    public static int mapKind(String paramString) {
        for (byte b = 0; b < 7; b++) {
            if (paramString.equals(kinds[b]))
                return b;
        }
        return -1;
    }

    public static void setDebugLevel(int paramInt) {
        if (paramInt < 0 || paramInt >= 4)
            throw new IllegalArgumentException("0 <= level < 4");
        enabled[0] = paramInt;
    }

    public static void setDebugLevel(String paramString) {
        int i = mapLevel(paramString);
        if (i == -1)
            throw new IllegalArgumentException(paramString + " is not a valid level");
        enabled[0] = i;
    }

    public static void setDebugLevel(int paramInt1, int paramInt2) {
        if (paramInt1 < 0 || paramInt1 >= 4)
            throw new IllegalArgumentException("0 <= level < 4");
        if (paramInt2 < 0 || paramInt2 >= 7)
            throw new IllegalArgumentException("0 <= kind < 7");
        enabled[paramInt2] = paramInt1;
    }

    public static void setDebugLevel(int paramInt, String paramString) {
        if (paramInt < 0 || paramInt >= 4)
            throw new IllegalArgumentException("0 <= level < 4");
        int i = mapKind(paramString);
        if (i == -1)
            throw new IllegalArgumentException(paramString + " is not a valid kind");
        enabled[i] = paramInt;
    }

    public static void setDebugLevel(String paramString, int paramInt) {
        if (paramInt < 0 || paramInt >= 7)
            throw new IllegalArgumentException("0 <= kind < 7");
        int i = mapLevel(paramString);
        if (i == -1)
            throw new IllegalArgumentException(paramString + " is not a valid level");
        enabled[paramInt] = i;
    }

    public static void setDebugLevel(String paramString1, String paramString2) {
        int i = mapLevel(paramString1);
        if (i == -1)
            throw new IllegalArgumentException(paramString1 + " is not a valid level");
        int j = mapKind(paramString2);
        if (j == -1)
            throw new IllegalArgumentException(paramString2 + " is not a valid kind");
        enabled[j] = i;
    }

    public static int getDebugLevel() {
        return enabled[0];
    }

    public static int getDebugLevel(int paramInt) {
        return enabled[paramInt];
    }

    public static int getDebugLevel(String paramString) {
        int i = mapKind(paramString);
        if (i == -1)
            throw new IllegalArgumentException(paramString + " is not a valid kind");
        return enabled[i];
    }

    public static void print(int paramInt, Object paramObject) {
        if (paramInt <= enabled[0])
            System.err.print(paramObject);
    }

    public static void print(int paramInt, String paramString) {
        if (paramInt <= enabled[0])
            System.err.print(paramString);
    }

    public static void println(int paramInt) {
        if (paramInt <= enabled[0])
            System.err.println();
    }

    public static void println(int paramInt, Object paramObject) {
        if (paramInt <= enabled[0])
            System.err.println(paramObject);
    }

    public static void println(int paramInt, String paramString) {
        if (paramInt <= enabled[0])
            System.err.println(paramString);
    }

    public static void print(int paramInt1, int paramInt2, Object paramObject) {
        if (paramInt1 <= enabled[paramInt2])
            System.err.print(paramObject);
    }

    public static void print(int paramInt1, int paramInt2, String paramString) {
        if (paramInt1 <= enabled[paramInt2])
            System.err.print(paramString);
    }

    public static void println(int paramInt1, int paramInt2) {
        if (paramInt1 <= enabled[paramInt2])
            System.err.println();
    }

    public static void println(int paramInt1, int paramInt2, Object paramObject) {
        if (paramInt1 <= enabled[paramInt2])
            System.err.println(paramObject);
    }

    public static void println(int paramInt1, int paramInt2, String paramString) {
        if (paramInt1 <= enabled[paramInt2])
            System.err.println(paramString);
    }

    public static String status() {
        StringBuffer stringBuffer = new StringBuffer();
        for (byte b = 1; b < 7; b++) {
            int i = enabled[b];
            if (i != 0) {
                if (stringBuffer.length() != 0)
                    stringBuffer.append(",");
                stringBuffer.append(kinds[b]);
                stringBuffer.append("=");
                stringBuffer.append(levels[i]);
            }
        }
        return stringBuffer.toString();
    }
}
