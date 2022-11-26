package gov.nasa.arc.ase.util;

public class Percent {
    public static String format(long paramLong1, long paramLong2) {
        double d1 = paramLong1;
        double d2 = paramLong2;
        double d3 = 100.0D * paramLong1 / paramLong2 + 0.5D;
        return (int) d3 + "%";
    }
}
