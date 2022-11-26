package ic.doc.ltsa.lts;

import ic.doc.extension.Relation;
import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

class ProcessSpec extends Declaration {
    Symbol name;
    Hashtable constants;
    Hashtable init_constants = new Hashtable();
    Vector parameters = new Vector();
    Vector stateDefns = new Vector();
    LabelSet alphaAdditions;
    LabelSet alphaHidden;
    Vector alphaRelabel;
    boolean isProperty = false;
    boolean isMinimal = false;
    boolean isDeterministic = false;
    boolean exposeNotHide = false;
    File importFile = null;

    public boolean imported() {
        return (this.importFile != null);
    }

    public String getname() {
        this.constants = (Hashtable) this.init_constants.clone();
        StateDefn stateDefn = this.stateDefns.firstElement();
        this.name = stateDefn.name;
        if (stateDefn.range != null)
            Diagnostics.fatal("process name cannot be indexed", this.name);
        return stateDefn.name.toString();
    }

    public void explicitStates(StateMachine paramStateMachine) {
        Enumeration enumeration = this.stateDefns.elements();
        while (enumeration.hasMoreElements()) {
            Declaration declaration = enumeration.nextElement();
            declaration.explicitStates(paramStateMachine);
        }
    }

    public void addAlphabet(StateMachine paramStateMachine) {
        if (this.alphaAdditions != null) {
            Vector vector = this.alphaAdditions.getActions(this.constants);
            Enumeration enumeration = vector.elements();
            while (enumeration.hasMoreElements()) {
                String str = enumeration.nextElement();
                if (!paramStateMachine.alphabet.containsKey(str))
                    paramStateMachine.alphabet.put(str, paramStateMachine.eventLabel.label());
            }
        }
    }

    public void hideAlphabet(StateMachine paramStateMachine) {
        if (this.alphaHidden == null)
            return;
        paramStateMachine.hidden = this.alphaHidden.getActions(this.constants);
    }

    public void relabelAlphabet(StateMachine paramStateMachine) {
        if (this.alphaRelabel == null)
            return;
        paramStateMachine.relabels = new Relation();
        Enumeration enumeration = this.alphaRelabel.elements();
        while (enumeration.hasMoreElements()) {
            RelabelDefn relabelDefn = enumeration.nextElement();
            relabelDefn.makeRelabels(this.constants, paramStateMachine.relabels);
        }
    }

    public void crunch(StateMachine paramStateMachine) {
        Enumeration enumeration = this.stateDefns.elements();
        while (enumeration.hasMoreElements()) {
            Declaration declaration = enumeration.nextElement();
            declaration.crunch(paramStateMachine);
        }
    }

    public void transition(StateMachine paramStateMachine) {
        Enumeration enumeration = this.stateDefns.elements();
        while (enumeration.hasMoreElements()) {
            Declaration declaration = enumeration.nextElement();
            declaration.transition(paramStateMachine);
        }
    }

    public void doParams(Vector paramVector) {
        Enumeration enumeration1 = paramVector.elements();
        Enumeration enumeration2 = this.parameters.elements();
        while (enumeration1.hasMoreElements() && enumeration2.hasMoreElements())
            this.constants.put(enumeration2.nextElement(), enumeration1.nextElement());
    }

    public ProcessSpec myclone() {
        ProcessSpec processSpec = new ProcessSpec();
        processSpec.name = this.name;
        processSpec.constants = (Hashtable) this.constants.clone();
        processSpec.init_constants = this.init_constants;
        processSpec.parameters = this.parameters;
        Enumeration enumeration = this.stateDefns.elements();
        while (enumeration.hasMoreElements())
            processSpec.stateDefns.addElement(((StateDefn) enumeration.nextElement()).myclone());
        processSpec.alphaAdditions = this.alphaAdditions;
        processSpec.alphaHidden = this.alphaHidden;
        processSpec.alphaRelabel = this.alphaRelabel;
        processSpec.isProperty = this.isProperty;
        processSpec.isMinimal = this.isMinimal;
        processSpec.isDeterministic = this.isDeterministic;
        processSpec.exposeNotHide = this.exposeNotHide;
        processSpec.importFile = this.importFile;
        return processSpec;
    }
}
