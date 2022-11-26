package ic.doc.ltsa.lts;

public class Diagnostics {
    private static LTSOutput output = null;
    public static boolean warningFlag = true;
    public static boolean warningsAreErrors = false;

    public static void init(LTSOutput paramLTSOutput) {
        output = paramLTSOutput;
    }

    public static void fatal(String paramString) {
        throw new LTSException(paramString);
    }

    public static void fatal(String paramString, Object paramObject) {
        throw new LTSException(paramString, paramObject);
    }

    public static void fatal(String paramString, Symbol paramSymbol) {
        if (paramSymbol != null)
            throw new LTSException(paramString, new Integer(paramSymbol.startPos));
        throw new LTSException(paramString);
    }

    public static void warning(String paramString1, String paramString2, Symbol paramSymbol) {
        if (warningsAreErrors) {
            fatal(paramString2, paramSymbol);
        } else if (warningFlag) {
            if (output == null)
                fatal("Diagnostic not initialised");
            output.outln("Warning - " + paramString1);
        }
    }
}
