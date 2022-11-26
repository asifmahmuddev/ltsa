package ic.doc.ltsa.lts;

import java.util.Enumeration;
import java.util.Vector;

public class Alphabet {
    PrefixTree root = null;
    String[] myAlpha;
    CompactState sm;
    public int maxLevel = 0;

    public Alphabet(CompactState paramCompactState) {
        this.sm = paramCompactState;
        this.myAlpha = new String[paramCompactState.alphabet.length];
        for (byte b1 = 0; b1 < paramCompactState.alphabet.length; b1++)
            this.myAlpha[b1] = paramCompactState.alphabet[b1];
        sort(this.myAlpha, 1);
        for (byte b2 = 1; b2 < this.myAlpha.length; b2++)
            this.root = PrefixTree.addName(this.root, this.myAlpha[b2]);
        if (this.root != null)
            this.maxLevel = this.root.maxDepth();
    }

    public Alphabet(String[] paramArrayOfString) {
        if (paramArrayOfString.length > 1)
            sort(paramArrayOfString, 0);
        for (byte b = 0; b < paramArrayOfString.length; b++)
            this.root = PrefixTree.addName(this.root, paramArrayOfString[b]);
    }

    public Alphabet(Vector paramVector) {
        this((String[]) paramVector.toArray((Object[]) new String[paramVector.size()]));
    }

    public String toString() {
        if (this.root == null)
            return "{}";
        return this.root.toString();
    }

    public void print(LTSOutput paramLTSOutput, int paramInt) {
        paramLTSOutput.outln("Process:\n\t" + this.sm.name);
        paramLTSOutput.outln("Alphabet:");
        if (this.root == null) {
            paramLTSOutput.outln("\t{}");
            return;
        }
        if (paramInt == 0) {
            paramLTSOutput.outln("\t" + this.root.toString());
        } else {
            paramLTSOutput.out("\t{ ");
            Vector vector = new Vector();
            this.root.getStrings(vector, paramInt - 1, null);
            Enumeration enumeration = vector.elements();
            boolean bool = true;
            while (enumeration.hasMoreElements()) {
                String str = enumeration.nextElement();
                if (!bool)
                    paramLTSOutput.out("\t  ");
                if (enumeration.hasMoreElements()) {
                    paramLTSOutput.outln(str + ",");
                } else {
                    paramLTSOutput.outln(str);
                }
                bool = false;
            }
            paramLTSOutput.outln("\t}");
        }
    }

    private void sort(String[] paramArrayOfString, int paramInt) {
        for (int i = paramInt; i < paramArrayOfString.length - 1; i++) {
            int j = i;
            for (int k = i + 1; k < paramArrayOfString.length; k++) {
                if (paramArrayOfString[k].compareTo(paramArrayOfString[j]) < 0)
                    j = k;
            }
            String str = paramArrayOfString[i];
            paramArrayOfString[i] = paramArrayOfString[j];
            paramArrayOfString[j] = str;
        }
    }
}
