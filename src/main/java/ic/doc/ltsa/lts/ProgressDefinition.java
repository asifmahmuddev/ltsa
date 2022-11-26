package ic.doc.ltsa.lts;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

class ProgressDefinition {
    Symbol name;
    ActionLabels pactions;
    ActionLabels cactions;
    ActionLabels range;
    static Hashtable definitions;

    public static void compile() {
        ProgressTest.init();
        Enumeration enumeration = definitions.elements();
        while (enumeration.hasMoreElements()) {
            ProgressDefinition progressDefinition = enumeration.nextElement();
            progressDefinition.makeProgressTest();
        }
    }

    public void makeProgressTest() {
        Vector vector1 = null;
        Vector vector2 = null;
        String str = this.name.toString();
        if (this.range == null) {
            vector1 = this.pactions.getActions(null, null);
            if (this.cactions != null)
                vector2 = this.cactions.getActions(null, null);
            new ProgressTest(str, vector1, vector2);
        } else {
            Hashtable hashtable = new Hashtable();
            this.range.initContext(hashtable, null);
            while (this.range.hasMoreNames()) {
                String str1 = this.range.nextName();
                vector1 = this.pactions.getActions(hashtable, null);
                if (this.cactions != null)
                    vector2 = this.cactions.getActions(hashtable, null);
                new ProgressTest(str + "." + str1, vector1, vector2);
            }
            this.range.clearContext();
        }
    }
}
