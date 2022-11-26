package ic.doc.ltsa.lts.ltl;

import gov.nasa.arc.ase.util.graph.Degeneralize;
import gov.nasa.arc.ase.util.graph.Graph;
import gov.nasa.arc.ase.util.graph.SCCReduction;
import gov.nasa.arc.ase.util.graph.SFSReduction;
import gov.nasa.arc.ase.util.graph.SuperSetReduction;
import ic.doc.ltsa.lts.CompositeState;
import ic.doc.ltsa.lts.Diagnostics;
import ic.doc.ltsa.lts.LTSOutput;
import ic.doc.ltsa.lts.Minimiser;
import ic.doc.ltsa.lts.Symbol;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class AssertDefinition {
    Symbol name;
    FormulaFactory fac;
    CompositeState cached;
    static Hashtable definitions;

    private AssertDefinition(Symbol paramSymbol, FormulaFactory paramFormulaFactory) {
        this.name = paramSymbol;
        this.fac = paramFormulaFactory;
        this.cached = null;
    }

    public static void put(Symbol paramSymbol, FormulaFactory paramFormulaFactory) {
        if (definitions == null)
            definitions = new Hashtable();
        if (definitions.put(paramSymbol.toString(), new AssertDefinition(paramSymbol, paramFormulaFactory)) != null)
            Diagnostics.fatal("duplicate LTL property definition: " + paramSymbol, paramSymbol);
    }

    public static void init() {
        definitions = null;
    }

    public static String[] names() {
        if (definitions == null)
            return null;
        int i = definitions.size();
        if (i == 0)
            return null;
        String[] arrayOfString = new String[i];
        Enumeration enumeration = definitions.keys();
        byte b = 0;
        while (enumeration.hasMoreElements())
            arrayOfString[b++] = enumeration.nextElement();
        return arrayOfString;
    }

    public static CompositeState compile(LTSOutput paramLTSOutput, String paramString) {
        if (definitions == null || paramString == null)
            return null;
        AssertDefinition assertDefinition = (AssertDefinition) definitions.get(paramString);
        if (assertDefinition == null)
            return null;
        if (assertDefinition.cached != null)
            return assertDefinition.cached;
        paramLTSOutput.outln("Formula !" + assertDefinition.name.toString() + " = " + assertDefinition.fac.getFormula());
        GeneralizedBuchiAutomata generalizedBuchiAutomata = new GeneralizedBuchiAutomata(assertDefinition.name.toString(), assertDefinition.fac);
        generalizedBuchiAutomata.translate();
        Graph graph1 = generalizedBuchiAutomata.Gmake();
        graph1 = SuperSetReduction.reduce(graph1);
        Graph graph2 = Degeneralize.degeneralize(graph1);
        graph2 = SCCReduction.reduce(graph2);
        graph2 = SFSReduction.reduce(graph2);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Converter converter = new Converter(assertDefinition.name.toString(), graph2, generalizedBuchiAutomata.getLabelFactory());
        paramLTSOutput.outln("Buchi automata:");
        converter.printFSP(new PrintStream(byteArrayOutputStream));
        paramLTSOutput.out(byteArrayOutputStream.toString());
        Vector vector = (generalizedBuchiAutomata.getLabelFactory()).propProcs;
        vector.add(converter);
        CompositeState compositeState = new CompositeState(converter.name, vector);
        compositeState.hidden = generalizedBuchiAutomata.getLabelFactory().getPrefix();
        compositeState.compose(paramLTSOutput);
        compositeState.composition.removeNonDetTau();
        Minimiser minimiser = new Minimiser(compositeState.composition, paramLTSOutput);
        compositeState.composition = minimiser.minimise();
        assertDefinition.cached = compositeState;
        return compositeState;
    }
}
