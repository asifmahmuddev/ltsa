package ic.doc.ltsa.lts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Enumeration;
import java.util.Hashtable;

public class AutCompactState extends CompactState {
    public AutCompactState(Symbol paramSymbol, File paramFile) {
        this.name = paramSymbol.toString();
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(paramFile));
        } catch (Exception exception) {
            Diagnostics.fatal("Error opening file" + exception, paramSymbol);
        }
        try {
            String str1 = bufferedReader.readLine();
            if (str1 == null)
                Diagnostics.fatal("file is empty", paramSymbol);
            this.maxStates = statesAUTheader(str1);
            this.states = new EventState[this.maxStates];
            Hashtable hashtable = new Hashtable();
            Counter counter = new Counter(0);
            hashtable.put("tau", counter.label());
            String str2 = null;
            int i = transitionsAUTheader(str1);
            byte b = 0;
            while ((str2 = bufferedReader.readLine()) != null) {
                parseAUTtransition(str2, hashtable, counter);
                b++;
            }
            if (b != i)
                Diagnostics.fatal("transitions read different from .aut header", paramSymbol);
            this.alphabet = new String[hashtable.size()];
            Enumeration enumeration = hashtable.keys();
            while (enumeration.hasMoreElements()) {
                String str = enumeration.nextElement();
                int j = ((Integer) hashtable.get(str)).intValue();
                this.alphabet[j] = str;
            }
        } catch (Exception exception) {
            Diagnostics.fatal("Error reading/translating file" + exception, paramSymbol);
        }
    }

    protected int statesAUTheader(String paramString) {
        int i = paramString.lastIndexOf(',');
        String str = paramString.substring(i + 1, paramString.indexOf(')')).trim();
        return Integer.parseInt(str);
    }

    protected int transitionsAUTheader(String paramString) {
        int i = paramString.indexOf(',');
        int j = paramString.lastIndexOf(',');
        String str = paramString.substring(i + 1, j).trim();
        return Integer.parseInt(str);
    }

    protected void parseAUTtransition(String paramString, Hashtable paramHashtable, Counter paramCounter) {
        int i = paramString.indexOf('(');
        int j = paramString.indexOf(',');
        String str1 = paramString.substring(i + 1, j).trim();
        int k = Integer.parseInt(str1);
        int m = paramString.indexOf(',', j + 1);
        String str2 = paramString.substring(j + 1, m).trim();
        if (str2.charAt(0) == '"')
            str2 = str2.substring(1, str2.length() - 1).trim();
        int n = paramString.indexOf(')');
        str1 = paramString.substring(m + 1, n).trim();
        int i1 = Integer.parseInt(str1);
        Integer integer = (Integer) paramHashtable.get(str2);
        if (integer == null) {
            integer = paramCounter.label();
            paramHashtable.put(str2, integer);
        }
        this.states[k] = EventState.add(this.states[k], new EventState(integer.intValue(), i1));
    }
}
