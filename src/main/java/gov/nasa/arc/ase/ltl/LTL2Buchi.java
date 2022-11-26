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
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class LTL2Buchi {
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
            System.out.println("\n\n");
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
        try {
            paramString = Rewriter.rewrite(paramString);
            Graph graph1 = Translator.translate(paramString);
            graph1.save("gba.sm");
            graph1.save("gba.txt", 1);
            FileWriter fileWriter = new FileWriter("gba.txt", true);
            try {
                fileWriter.write("\n" + graph1.getNodeCount() + " states " + graph1.getEdgeCount() + " transitions");
                fileWriter.close();
            } catch (IOException iOException) {
                System.out.println("Error: " + iOException);
            }
            graph1 = SuperSetReduction.reduce(graph1);
            graph1.save("ssr-gba.sm");
            graph1.save("ssr-gba.txt", 1);
            fileWriter = new FileWriter("ssr-gba.txt", true);
            try {
                fileWriter.write("\n" + graph1.getNodeCount() + " states " + graph1.getEdgeCount() + " transitions");
                fileWriter.close();
            } catch (IOException iOException) {
                System.out.println("Error: " + iOException);
            }
            Graph graph2 = Degeneralize.degeneralize(graph1);
            graph2.save("ba.sm");
            graph2.save("ba.txt", 1);
            fileWriter = new FileWriter("ba.txt", true);
            try {
                fileWriter.write("\n" + graph2.getNodeCount() + " states " + graph2.getEdgeCount() + " transitions");
                fileWriter.close();
            } catch (IOException iOException) {
                System.out.println("Error: " + iOException);
            }
            graph2 = SCCReduction.reduce(graph2);
            graph2.save("scc-ba.sm");
            graph2.save("scc-ba.txt", 1);
            fileWriter = new FileWriter("scc-ba.txt", true);
            try {
                fileWriter.write("\n" + graph2.getNodeCount() + " states " + graph2.getEdgeCount() + " transitions");
                fileWriter.close();
            } catch (IOException iOException) {
                System.out.println("Error: " + iOException);
            }
            graph2 = SFSReduction.reduce(graph2);
            graph2.save("fairSim-final.sm");
            graph2.save("sfs-ba.txt", 1);
            fileWriter = new FileWriter("sfs-ba.txt", true);
            try {
                fileWriter.write("\n" + graph2.getNodeCount() + " states " + graph2.getEdgeCount() + " transitions");
                fileWriter.close();
            } catch (IOException iOException) {
                System.out.println("Error: " + iOException);
            }
            return graph2;
        } catch (IOException iOException) {
            throw new JPFErrorException(iOException.getMessage());
        }
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
