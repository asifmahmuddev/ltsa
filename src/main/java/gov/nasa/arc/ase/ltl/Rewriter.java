package gov.nasa.arc.ase.ltl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

public class Rewriter {
    public static void main(String[] paramArrayOfString) {
        int i = 0;
        int j = 0;
        try {
            if (paramArrayOfString.length != 0) {
                for (byte b = 0; b < paramArrayOfString.length; b++) {
                    Formula formula = Formula.parse(paramArrayOfString[b]);
                    i += formula.size();
                    System.out.println(formula = rewrite(formula));
                    j += formula.size();
                    System.err.println((j * 100 / i) + "% (" + i + " => " + j + ")");
                }
            } else {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
                try {
                    while (true) {
                        String str = bufferedReader.readLine();
                        if (str == null)
                            break;
                        if (str.equals(""))
                            continue;
                        Formula formula = Formula.parse(str);
                        i += formula.size();
                        System.out.println(formula = rewrite(formula));
                        j += formula.size();
                        System.err.println((j * 100 / i) + "% (" + i + " => " + j + ")");
                    }
                } catch (IOException iOException) {
                    System.out.println("error");
                }
            }
        } catch (ParseErrorException parseErrorException) {
            System.err.println("parse error: " + parseErrorException.getMessage());
        }
    }

    public static String rewrite(String paramString) throws ParseErrorException {
        return rewrite(Formula.parse(paramString)).toString();
    }

    public static Formula rewrite(Formula paramFormula) {
        Formula[] arrayOfFormula = readRules();
        if (arrayOfFormula == null)
            return paramFormula;
        try {
            boolean bool2, bool1 = false;
            do {
                Formula formula;
                bool2 = false;
                do {
                    formula = paramFormula;
                    for (byte b = 0; b < arrayOfFormula.length; b += 2)
                        paramFormula = applyRule(paramFormula, arrayOfFormula[b], arrayOfFormula[b + 1]);
                    if (formula == paramFormula)
                        continue;
                    bool2 = true;
                } while (formula != paramFormula);
                bool1 = !bool1 ? true : false;
                paramFormula = Formula.parse("!" + paramFormula.toString());
            } while (bool2 || bool1);
            return paramFormula;
        } catch (ParseErrorException parseErrorException) {
            return null;
        }
    }

    public static Formula[] readRules() {
        Formula[] arrayOfFormula = new Formula[0];
        try {
            BufferedReader bufferedReader = new BufferedReader(new StringReader(RulesClass.getRules()));
            while (true) {
                String str = bufferedReader.readLine();
                if (str == null)
                    break;
                if (str.equals(""))
                    continue;
                Formula formula = Formula.parse(str);
                Formula[] arrayOfFormula1 = new Formula[arrayOfFormula.length + 1];
                System.arraycopy(arrayOfFormula, 0, arrayOfFormula1, 0, arrayOfFormula.length);
                arrayOfFormula1[arrayOfFormula.length] = formula;
                arrayOfFormula = arrayOfFormula1;
            }
        } catch (IOException iOException) {
        } catch (ParseErrorException parseErrorException) {
            System.err.println("parse error: " + parseErrorException.getMessage());
            System.exit(1);
        }
        return arrayOfFormula;
    }

    public static Formula applyRule(Formula paramFormula1, Formula paramFormula2, Formula paramFormula3) {
        return paramFormula1.rewrite(paramFormula2, paramFormula3);
    }
}
