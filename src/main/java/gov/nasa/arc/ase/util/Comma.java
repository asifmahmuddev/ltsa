package gov.nasa.arc.ase.util;

public class Comma {
    private static final long thousand = 1000L;

    public static String format(long paramLong) {
        StringBuffer stringBuffer = new StringBuffer();
        while (paramLong >= 1000L) {
            long l = paramLong % 1000L;
            paramLong /= 1000L;
            stringBuffer.insert(0, l);
            if (l < 10L)
                stringBuffer.insert(0, '0');
            if (l < 100L)
                stringBuffer.insert(0, '0');
            stringBuffer.insert(0, ',');
        }
        stringBuffer.insert(0, paramLong);
        return stringBuffer.toString();
    }
}
