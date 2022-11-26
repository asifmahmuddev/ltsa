package ic.doc.ltsa.lts;

import java.util.Hashtable;

public class SymbolTable {
    private static Hashtable keyword;

    public static void init() {
        keyword = new Hashtable();
        keyword.put("const", new Integer(1));
        keyword.put("property", new Integer(2));
        keyword.put("range", new Integer(3));
        keyword.put("if", new Integer(4));
        keyword.put("then", new Integer(5));
        keyword.put("else", new Integer(6));
        keyword.put("forall", new Integer(7));
        keyword.put("when", new Integer(8));
        keyword.put("set", new Integer(9));
        keyword.put("progress", new Integer(10));
        keyword.put("menu", new Integer(11));
        keyword.put("animation", new Integer(12));
        keyword.put("actions", new Integer(13));
        keyword.put("controls", new Integer(14));
        keyword.put("deterministic", new Integer(15));
        keyword.put("minimal", new Integer(16));
        keyword.put("compose", new Integer(17));
        keyword.put("target", new Integer(18));
        keyword.put("import", new Integer(19));
        keyword.put("assert", new Integer(21));
        keyword.put("state", new Integer(22));
        keyword.put("initially", new Integer(24));
    }

    public static Object get(String paramString) {
        return keyword.get(paramString);
    }
}
