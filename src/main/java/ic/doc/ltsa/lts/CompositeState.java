package ic.doc.ltsa.lts;

import ic.doc.extension.Relation;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

public class CompositeState {
    public static boolean reduceFlag = true;
    public String name;
    public Vector machines;
    public CompactState composition;
    public Vector hidden;
    public boolean exposeNotHide = false;
    public boolean priorityIsLow = true;
    public boolean makeDeterministic = false;
    public boolean makeMinimal = false;
    public boolean makeCompose = false;
    public boolean isProperty = false;
    public Vector priorityLabels;
    public CompactState alphaStop;
    protected Vector errorTrace = null;
    private CompactState saved;

    public Vector getErrorTrace() {
        return this.errorTrace;
    }

    public void setErrorTrace(List paramList) {
        if (paramList != null) {
            this.errorTrace = new Vector();
            this.errorTrace.addAll(paramList);
        }
    }

    public void compose(LTSOutput paramLTSOutput) {
        if (this.machines != null && this.machines.size() > 0) {
            Analyser analyser = new Analyser(this, paramLTSOutput, null);
            this.composition = analyser.composeNoHide();
            if (this.makeDeterministic) {
                applyHiding();
                determinise(paramLTSOutput);
            } else if (this.makeMinimal) {
                applyHiding();
                minimise(paramLTSOutput);
            } else {
                applyHiding();
            }
        }
    }

    private void applyHiding() {
        if (this.composition == null)
            return;
        if (this.hidden != null)
            if (!this.exposeNotHide) {
                this.composition.conceal(this.hidden);
            } else {
                this.composition.expose(this.hidden);
            }
    }

    public void analyse(LTSOutput paramLTSOutput) {
        if (this.saved != null) {
            this.machines.remove(this.saved);
            this.saved = null;
        }
        if (this.composition != null) {
            CounterExample counterExample = new CounterExample(this);
            counterExample.print(paramLTSOutput);
            this.errorTrace = counterExample.getErrorTrace();
        } else {
            Analyser analyser = new Analyser(this, paramLTSOutput, null);
            analyser.analyse();
            setErrorTrace(analyser.getErrorTrace());
        }
    }

    public void checkProgress(LTSOutput paramLTSOutput) {
        ProgressCheck progressCheck;
        if (this.saved != null) {
            this.machines.remove(this.saved);
            this.saved = null;
        }
        if (this.composition != null) {
            progressCheck = new ProgressCheck(this.composition, paramLTSOutput);
            progressCheck.doProgressCheck();
        } else {
            Analyser analyser = new Analyser(this, paramLTSOutput, null);
            progressCheck = new ProgressCheck(analyser, paramLTSOutput);
            progressCheck.doProgressCheck();
        }
        this.errorTrace = progressCheck.getErrorTrace();
    }

    public CompositeState(Vector paramVector) {
        this.saved = null;
        this.name = "DEFAULT";
        this.machines = paramVector;
    }

    public CompositeState(String paramString, Vector paramVector) {
        this.saved = null;
        this.name = paramString;
        this.machines = paramVector;
        initAlphaStop();
    }

    public void checkLTL(LTSOutput paramLTSOutput, CompositeState paramCompositeState) {
        CompactState compactState = paramCompositeState.composition;
        if (this.name.equals("DEFAULT") && this.machines.size() == 0) {
            this.machines = paramCompositeState.machines;
            this.composition = paramCompositeState.composition;
        } else {
            if (this.saved != null)
                this.machines.remove(this.saved);
            Vector vector = this.hidden;
            boolean bool = this.exposeNotHide;
            this.hidden = compactState.getAlphabetV();
            this.exposeNotHide = true;
            this.machines.add(this.saved = compactState);
            Analyser analyser = new Analyser(this, paramLTSOutput, null);
            ProgressCheck progressCheck = new ProgressCheck(analyser, paramLTSOutput);
            progressCheck.doLTLCheck();
            this.errorTrace = progressCheck.getErrorTrace();
            this.hidden = vector;
            this.exposeNotHide = bool;
        }
    }

    public void minimise(LTSOutput paramLTSOutput) {
        if (this.composition != null) {
            if (reduceFlag)
                this.composition.removeNonDetTau();
            Minimiser minimiser = new Minimiser(this.composition, paramLTSOutput);
            this.composition = minimiser.minimise();
        }
    }

    public void determinise(LTSOutput paramLTSOutput) {
        if (this.composition != null) {
            Minimiser minimiser = new Minimiser(this.composition, paramLTSOutput);
            this.composition = minimiser.trace_minimise();
            if (this.isProperty)
                this.composition.makeProperty();
        }
    }

    public CompactState create(LTSOutput paramLTSOutput) {
        compose(paramLTSOutput);
        return this.composition;
    }

    public boolean needNotCreate() {
        return (this.hidden == null && this.priorityLabels == null && !this.makeDeterministic && !this.makeMinimal && !this.makeCompose);
    }

    public void prefixLabels(String paramString) {
        this.name = paramString + ":" + this.name;
        this.alphaStop.prefixLabels(paramString);
        for (Enumeration enumeration = this.machines.elements(); enumeration.hasMoreElements();) {
            CompactState compactState = enumeration.nextElement();
            compactState.prefixLabels(paramString);
        }
    }

    public void addAccess(Vector paramVector) {
        int i = paramVector.size();
        if (i == 0)
            return;
        String str = "{";
        Enumeration enumeration = paramVector.elements();
        byte b = 0;
        while (enumeration.hasMoreElements()) {
            String str1 = enumeration.nextElement();
            str = str + str1;
            b++;
            if (b < i)
                str = str + ",";
        }
        this.name = str + "}::" + this.name;
        this.alphaStop.addAccess(paramVector);
        for (Enumeration enumeration1 = this.machines.elements(); enumeration1.hasMoreElements();) {
            CompactState compactState = enumeration1.nextElement();
            compactState.addAccess(paramVector);
        }
    }

    public CompactState relabel(Relation paramRelation, LTSOutput paramLTSOutput) {
        this.alphaStop.relabel(paramRelation);
        if (this.alphaStop.relabelDuplicates() && this.machines.size() > 1) {
            compose(paramLTSOutput);
            this.composition.relabel(paramRelation);
            return this.composition;
        }
        for (Enumeration enumeration = this.machines.elements(); enumeration.hasMoreElements();) {
            CompactState compactState = enumeration.nextElement();
            compactState.relabel(paramRelation);
        }
        return null;
    }

    protected void initAlphaStop() {
        this.alphaStop = new CompactState();
        this.alphaStop.name = this.name;
        this.alphaStop.maxStates = 1;
        this.alphaStop.states = new EventState[this.alphaStop.maxStates];
        this.alphaStop.states[0] = null;
        Hashtable hashtable = new Hashtable();
        for (Enumeration enumeration = this.machines.elements(); enumeration.hasMoreElements();) {
            CompactState compactState = enumeration.nextElement();
            for (byte b1 = 1; b1 < compactState.alphabet.length; b1++)
                hashtable.put(compactState.alphabet[b1], compactState.alphabet[b1]);
        }
        this.alphaStop.alphabet = new String[hashtable.size() + 1];
        this.alphaStop.alphabet[0] = "tau";
        byte b = 1;
        for (Enumeration enumeration1 = hashtable.keys(); enumeration1.hasMoreElements();) {
            String str = enumeration1.nextElement();
            this.alphaStop.alphabet[b] = str;
            b++;
        }
    }
}
