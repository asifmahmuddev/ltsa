package ic.doc.ltsa.lts.ltl;

import ic.doc.ltsa.lts.CompactState;
import ic.doc.ltsa.lts.Diagnostics;
import ic.doc.ltsa.lts.EventState;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

public class LabelFactory {
    SortedSet allprops;
    FormulaFactory fac;
    String name;
    HashMap tr;
    BitSet[] ps;
    BitSet[] nps;
    Vector propProcs;
    SortedSet allActions;

    public LabelFactory(String paramString, FormulaFactory paramFormulaFactory) {
        this.allprops = paramFormulaFactory.getProps();
        this.fac = paramFormulaFactory;
        this.name = paramString;
        this.tr = new HashMap();
        initPropSets();
        compileProps();
    }

    HashMap getTransLabels() {
        return this.tr;
    }

    Vector getPrefix() {
        Vector vector = new Vector();
        Formula formula = this.allprops.first();
        vector.add("_" + formula);
        return vector;
    }

    String makeLabel(SortedSet paramSortedSet) {
        StringBuffer stringBuffer = new StringBuffer();
        Iterator iterator = this.allprops.iterator();
        boolean bool = false;
        BitSet bitSet = new BitSet();
        byte b = 0;
        while (iterator.hasNext()) {
            Formula formula = iterator.next();
            if (paramSortedSet.contains(formula)) {
                if (bool) {
                    stringBuffer.append("&");
                    bitSet.and(this.ps[b]);
                } else {
                    bitSet.or(this.ps[b]);
                    bool = true;
                }
                stringBuffer.append(formula);
            } else if (paramSortedSet.contains(this.fac.makeNot(formula))) {
                if (bool) {
                    stringBuffer.append("&");
                    bitSet.and(this.nps[b]);
                } else {
                    bitSet.or(this.nps[b]);
                    bool = true;
                }
                stringBuffer.append("!" + formula);
            }
            b++;
        }
        String str = stringBuffer.toString();
        this.tr.put(str, bitSet);
        return str;
    }

    public String[] makeAlphabet() {
        return makeAlphabet(null, null, null);
    }

    private String[] makeAlphabet(PredicateDefinition paramPredicateDefinition, BitSet paramBitSet1, BitSet paramBitSet2) {
        int i = 0;
        if (paramPredicateDefinition == null) {
            i = 1;
        } else {
            i = paramPredicateDefinition.trueActions.size() + paramPredicateDefinition.falseActions.size();
        }
        int j = (1 << this.allprops.size()) + 1 + i;
        String[] arrayOfString = new String[j];
        for (byte b = 0; b < j - i; b++) {
            StringBuffer stringBuffer = new StringBuffer();
            Iterator iterator = this.allprops.iterator();
            boolean bool = false;
            byte b1 = 0;
            while (iterator.hasNext()) {
                Formula formula = iterator.next();
                if (bool)
                    stringBuffer.append(".");
                bool = true;
                stringBuffer.append("_" + formula + "." + ((b >> b1) % 2));
                b1++;
            }
            arrayOfString[b + 1] = stringBuffer.toString();
        }
        arrayOfString[0] = "tau";
        if (paramPredicateDefinition == null) {
            arrayOfString[j - 1] = "@" + this.name;
        } else {
            int k = j - i;
            Iterator iterator = paramPredicateDefinition.falseActions.iterator();
            while (iterator.hasNext()) {
                arrayOfString[k] = iterator.next();
                paramBitSet2.set(k);
                k++;
            }
            iterator = paramPredicateDefinition.trueActions.iterator();
            while (iterator.hasNext()) {
                arrayOfString[k] = iterator.next();
                paramBitSet1.set(k);
                k++;
            }
        }
        return arrayOfString;
    }

    void initPropSets() {
        int i = this.allprops.size();
        this.ps = new BitSet[i];
        this.nps = new BitSet[i];
        BitSet bitSet = new BitSet(1 << i);
        for (byte b1 = 0; b1 < i; b1++) {
            this.ps[b1] = new BitSet(1 << i);
            this.nps[b1] = new BitSet(1 << i);
        }
        for (byte b2 = 0; b2 < 1 << i; b2++) {
            bitSet.set(b2);
            for (byte b = 0; b < i; b++) {
                if ((b2 >> b) % 2 == 1) {
                    this.ps[b].set(b2);
                } else {
                    this.nps[b].set(b2);
                }
            }
        }
        this.tr.put("true", bitSet);
    }

    protected void compileProps() {
        this.propProcs = new Vector();
        this.allActions = new TreeSet();
        Iterator iterator = this.allprops.iterator();
        byte b = 0;
        while (iterator.hasNext()) {
            Proposition proposition = iterator.next();
            PredicateDefinition predicateDefinition = PredicateDefinition.get(proposition.toString());
            if (predicateDefinition == null)
                Diagnostics.fatal("Proposition " + proposition + " not found", proposition.sym);
            this.allActions.addAll(predicateDefinition.trueActions);
            this.allActions.addAll(predicateDefinition.falseActions);
            this.propProcs.add(makePropProcess(predicateDefinition, b));
            b++;
        }
        this.propProcs.add(makeSyncProcess());
    }

    CompactState makePropProcess(PredicateDefinition paramPredicateDefinition, int paramInt) {
        CompactState compactState = new CompactState();
        compactState.name = paramPredicateDefinition.name.toString();
        compactState.maxStates = 2;
        compactState.states = new EventState[compactState.maxStates];
        BitSet bitSet1 = new BitSet();
        BitSet bitSet2 = new BitSet();
        compactState.alphabet = makeAlphabet(paramPredicateDefinition, bitSet1, bitSet2);
        boolean bool1 = paramPredicateDefinition.initial ? true : false;
        boolean bool2 = paramPredicateDefinition.initial ? false : true;
        for (byte b1 = 0; b1 < bitSet1.size(); b1++) {
            if (bitSet1.get(b1))
                compactState.states[bool1] = EventState.add(compactState.states[bool1], new EventState(b1, bool2));
        }
        for (byte b2 = 0; b2 < bitSet2.size(); b2++) {
            if (bitSet2.get(b2))
                compactState.states[bool2] = EventState.add(compactState.states[bool2], new EventState(b2, bool1));
        }
        for (byte b3 = 0; b3 < bitSet2.size(); b3++) {
            if (bitSet2.get(b3))
                compactState.states[bool1] = EventState.add(compactState.states[bool1], new EventState(b3, bool1));
        }
        for (byte b4 = 0; b4 < bitSet1.size(); b4++) {
            if (bitSet1.get(b4))
                compactState.states[bool2] = EventState.add(compactState.states[bool2], new EventState(b4, bool2));
        }
        for (byte b5 = 0; b5 < this.nps[paramInt].size(); b5++) {
            if (this.nps[paramInt].get(b5))
                compactState.states[bool1] = EventState.add(compactState.states[bool1], new EventState(b5 + 1, bool1));
        }
        for (byte b6 = 0; b6 < this.ps[paramInt].size(); b6++) {
            if (this.ps[paramInt].get(b6))
                compactState.states[bool2] = EventState.add(compactState.states[bool2], new EventState(b6 + 1, bool2));
        }
        return compactState;
    }

    CompactState makeSyncProcess() {
        CompactState compactState = new CompactState();
        compactState.name = "SYNC";
        compactState.maxStates = 2;
        compactState.states = new EventState[compactState.maxStates];
        String[] arrayOfString1 = makeAlphabet();
        String[] arrayOfString2 = new String[this.allActions.size()];
        byte b1 = 0;
        for (Iterator iterator = this.allActions.iterator(); iterator.hasNext(); arrayOfString2[b1++] = iterator.next());
        compactState.alphabet = new String[arrayOfString1.length - 1 + arrayOfString2.length];
        compactState.alphabet[0] = "tau";
        for (byte b2 = 1; b2 < arrayOfString1.length - 1; b2++) {
            compactState.alphabet[b2] = arrayOfString1[b2];
            compactState.states[0] = EventState.add(compactState.states[0], new EventState(b2, 1));
        }
        for (byte b3 = 0; b3 < arrayOfString2.length; b3++) {
            compactState.alphabet[b3 + arrayOfString1.length - 1] = arrayOfString2[b3];
            compactState.states[1] = EventState.add(compactState.states[1], new EventState(b3 + arrayOfString1.length - 1, 0));
        }
        return compactState;
    }
}
