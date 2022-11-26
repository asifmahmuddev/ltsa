package ic.doc.ltsa.lts;

import java.util.BitSet;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class ProgressTest {
    String name;
    Vector pactions;
    BitSet pset;
    Vector cactions;
    BitSet cset;
    static Vector tests;

    public static void init() {
        tests = new Vector();
    }

    public ProgressTest(String paramString, Vector paramVector1, Vector paramVector2) {
        this.name = paramString;
        this.cactions = paramVector2;
        this.pactions = paramVector1;
        tests.addElement(this);
    }

    public static void initTests(String[] paramArrayOfString) {
        if (tests == null || tests.size() == 0)
            return;
        Hashtable hashtable = new Hashtable(paramArrayOfString.length);
        for (byte b = 0; b < paramArrayOfString.length; b++)
            hashtable.put(paramArrayOfString[b], new Integer(b));
        Enumeration enumeration = tests.elements();
        while (enumeration.hasMoreElements()) {
            ProgressTest progressTest = enumeration.nextElement();
            progressTest.pset = alphaToBit(progressTest.pactions, hashtable);
            progressTest.cset = alphaToBit(progressTest.cactions, hashtable);
        }
    }

    public static boolean noTests() {
        return (tests == null || tests.size() == 0);
    }

    private static BitSet alphaToBit(Vector paramVector, Hashtable paramHashtable) {
        if (paramVector == null)
            return null;
        BitSet bitSet = new BitSet(paramHashtable.size());
        Enumeration enumeration = paramVector.elements();
        while (enumeration.hasMoreElements()) {
            String str = enumeration.nextElement();
            Integer integer = (Integer) paramHashtable.get(str);
            if (integer != null)
                bitSet.set(integer.intValue());
        }
        return bitSet;
    }
}
