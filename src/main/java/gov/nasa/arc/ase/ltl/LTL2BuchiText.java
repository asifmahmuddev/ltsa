package gov.nasa.arc.ase.ltl;

import gov.nasa.arc.ase.extra.JPFErrorException;
import gov.nasa.arc.ase.util.graph.Degeneralize;
import gov.nasa.arc.ase.util.graph.Graph;
import gov.nasa.arc.ase.util.graph.SCCReduction;
import gov.nasa.arc.ase.util.graph.SFSReduction;
import gov.nasa.arc.ase.util.graph.SuperSetReduction;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class LTL2BuchiText {
    public static void main(String[] paramArrayOfString) {
        if (paramArrayOfString.length > 1) {
            System.out.println("usage:");
            System.out.println("\tjava gov.nasa.arc.ase.ltl.LTL2Buchi [<filename>|<ltl-formula>]");
            return;
        }
        String str = null;
        if (paramArrayOfString.length == 0) {
            str = readLTL();
        } else {
            str = paramArrayOfString[0];
            if (str.endsWith(".ltl")) {
                str = loadLTL(str);
            } else if (str.equals("-")) {
                str = readLTL();
            }
        }
        try {
            Graph graph = translate(str);
            graph.save(1);
            System.out.println("\n***********************\n");
        } catch (ParseErrorException parseErrorException) {
            System.out.println("Error: " + parseErrorException);
        }
    }

    public static void reset_all_static() {
        Node.reset_static();
        Formula.reset_static();
        Pool.reset_static();
    }

    public static Graph translate(String paramString) throws ParseErrorException {
        System.out.println("Translating formula: " + paramString);
        System.out.println();
        paramString = Rewriter.rewrite(paramString);
        System.out.println("Rewritten as       : " + paramString);
        System.out.println();
        Graph graph1 = Translator.translate(paramString);
        System.out.println("\n***********************");
        System.out.println("Generalized buchi automaton generated");
        System.out.println("\t" + graph1.getNodeCount() + " states " + graph1.getEdgeCount() + " transitions");
        System.out.println();
        graph1 = SuperSetReduction.reduce(graph1);
        System.out.println("Superset reduction");
        System.out.println("\t" + graph1.getNodeCount() + " states " + graph1.getEdgeCount() + " transitions");
        System.out.println();
        Graph graph2 = Degeneralize.degeneralize(graph1);
        System.out.println("Degeneralized buchi automaton generated: ba.sm");
        System.out.println("\t" + graph2.getNodeCount() + " states " + graph2.getEdgeCount() + " transitions");
        System.out.println();
        graph2 = SCCReduction.reduce(graph2);
        System.out.println("Strongly connected component reduction: scc-ba.sm");
        System.out.println("\t" + graph2.getNodeCount() + " states " + graph2.getEdgeCount() + " transitions");
        System.out.println();
        graph2 = SFSReduction.reduce(graph2);
        System.out.println("Fair simulation applied");
        System.out.println("\t" + graph2.getNodeCount() + " states " + graph2.getEdgeCount() + " transitions");
        System.out.println("***********************\n");
        return graph2;
    }

    private static String loadLTL(String paramString) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(paramString));
            return bufferedReader.readLine();
        } catch (FileNotFoundException fileNotFoundException) {
            throw new JPFErrorException("Can't load LTL formula: " + paramString);
        } catch (IOException iOException) {
            throw new JPFErrorException("Error read on LTL formula: " + paramString);
        }
    }

    private static String readLTL() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Insert LTL formula: ");
            return bufferedReader.readLine();
        } catch (IOException iOException) {
            throw new JPFErrorException("Invalid LTL formula");
        }
    }
}
