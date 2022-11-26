package gov.nasa.arc.ase.util;

public class kMG {
    private static final long kilo = 1024L;
    private static final long mega = 1048576L;
    private static final long giga = 1073741824L;

    public static String format(long paramLong) {
        String str;
        if (paramLong >= 1073741824L) {
            d = paramLong / 1.073741824E9D;
            str = "G";
        } else if (paramLong >= 1048576L) {
            d = paramLong / 1048576.0D;
            str = "M";
        } else if (paramLong >= 1024L) {
            d = paramLong / 1024.0D;
            str = "k";
        } else {
            d = paramLong;
            str = "";
        }
        double d = Math.rint(d * 100.0D) / 100.0D;
        return d + str;
    }
}
