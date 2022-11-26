package gov.nasa.arc.ase.util;

public class hms {
    public static String format(long paramLong) {
        StringBuffer stringBuffer = new StringBuffer();
        long l1 = paramLong / 3600000L;
        long l2 = paramLong / 60000L % 60L;
        long l3 = paramLong / 1000L % 60L;
        long l4 = paramLong % 1000L;
        if (l1 != 0L) {
            stringBuffer.append(l1);
            stringBuffer.append(':');
            format(stringBuffer, l2, 2);
            stringBuffer.append(':');
            format(stringBuffer, l3, 2);
            stringBuffer.append('.');
            format(stringBuffer, l4, 3);
        } else if (l2 != 0L) {
            stringBuffer.append(l2);
            stringBuffer.append(':');
            format(stringBuffer, l3, 2);
            stringBuffer.append('.');
            format(stringBuffer, l4, 3);
        } else {
            stringBuffer.append(l3);
            stringBuffer.append('.');
            format(stringBuffer, l4, 3);
        }
        return stringBuffer.toString();
    }

    private static void format(StringBuffer paramStringBuffer, long paramLong, int paramInt) {
        if (paramInt <= 1) {
            paramStringBuffer.append(paramLong);
        } else {
            if (paramLong < Math.pow(10.0D, (paramInt - 1)))
                paramStringBuffer.append('0');
            format(paramStringBuffer, paramLong, paramInt - 1);
        }
    }
}
