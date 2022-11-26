package gov.nasa.arc.ase.ltl;

import gov.nasa.arc.ase.extra.JPFErrorException;
import gov.nasa.arc.ase.util.graph.Graph;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Translator {
    public static void main(String[] paramArrayOfString) {
        String str1 = null;
        String str2 = null;
        if (paramArrayOfString.length == 2) {
            str1 = paramArrayOfString[0];
            str2 = paramArrayOfString[1];
        } else if (paramArrayOfString.length == 1) {
            str1 = paramArrayOfString[0];
        } else {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("\nPlease enter LTL formula followed by enter");
            try {
                str1 = bufferedReader.readLine();
            } catch (IOException iOException) {
                System.out.println(" => <input error:" + iOException.getMessage() + ">");
            }
        }
        if (str2 == null) {
            translate(str1).save(1);
        } else {
            try {
                translate(str1).save(str2);
            } catch (IOException iOException) {
                System.out.println("Can't save file: " + str2);
            }
        }
    }

    public static Graph translate(String paramString) {
        try {
            Formula formula = Formula.parse(paramString);
            Node node = Node.createInitial(formula);
            State[] arrayOfState = node.expand(new Automaton()).structForRuntAnalysis();
            return Automaton.SMoutput(arrayOfState);
        } catch (ParseErrorException parseErrorException) {
            throw new JPFErrorException("parse error: " + parseErrorException.getMessage());
        }
    }
}
