package uk.ac.ic.doc.natutil;

public class Assertion extends RuntimeException {
    public static boolean DEBUG = Boolean.getBoolean("uk.ac.ic.doc.natutil.assert");

    private Assertion() {
        super("Assertion failed");
    }

    private Assertion(String paramString) {
        super(paramString);
    }

    public static void check(boolean paramBoolean, String paramString) {
        if (DEBUG && !paramBoolean)
            throw new Assertion(paramString);
    }
}
