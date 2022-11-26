package gov.nasa.arc.ase.extra;

import java.io.File;
import java.util.StringTokenizer;

public class ClassPath {
    private static String[] classPath;
    static {
        StringTokenizer stringTokenizer = new StringTokenizer(System.getProperty("java.class.path"), File.pathSeparator);
        classPath = new String[stringTokenizer.countTokens()];
        for (byte b = 0; stringTokenizer.hasMoreTokens(); b++)
            classPath[b] = stringTokenizer.nextToken();
    }

    public static int length() {
        return classPath.length;
    }

    public static String get(int paramInt) {
        return classPath[paramInt];
    }
}
