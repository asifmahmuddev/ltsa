package ic.doc.ltsa.lts;

import ic.doc.ltsa.lts.ltl.AssertDefinition;
import ic.doc.ltsa.lts.ltl.FormulaFactory;
import ic.doc.ltsa.lts.ltl.LTLparser;
import ic.doc.ltsa.lts.ltl.PredicateDefinition;
import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

public class LTSCompiler {
    private Lex lex;
    private LTSOutput output;
    private String currentDirectory;
    private Symbol current;
    static Hashtable processes;
    static Hashtable compiled;
    static Hashtable composites;

    public LTSCompiler(LTSInput paramLTSInput, LTSOutput paramLTSOutput, String paramString) {
        this.lex = new Lex(paramLTSInput);
        this.output = paramLTSOutput;
        this.currentDirectory = paramString;
        Diagnostics.init(paramLTSOutput);
        SeqProcessRef.output = paramLTSOutput;
        StateMachine.output = paramLTSOutput;
        Expression.constants = new Hashtable();
        Range.ranges = new Hashtable();
        LabelSet.constants = new Hashtable();
        ProgressDefinition.definitions = new Hashtable();
        MenuDefinition.definitions = new Hashtable();
        PredicateDefinition.init();
        AssertDefinition.init();
    }

    private Symbol next_symbol() {
        return this.current = this.lex.next_symbol();
    }

    private void push_symbol() {
        this.lex.push_symbol();
    }

    private void error(String paramString) {
        Diagnostics.fatal(paramString, this.current);
    }

    private void current_is(int paramInt, String paramString) {
        if (this.current.kind != paramInt)
            error(paramString);
    }

    public CompositeState compile(String paramString) {
        processes = new Hashtable();
        composites = new Hashtable();
        compiled = new Hashtable();
        doparse(composites, processes, compiled);
        ProgressDefinition.compile();
        MenuDefinition.compile();
        CompositionExpression compositionExpression = (CompositionExpression) composites.get(paramString);
        if (compositionExpression == null && composites.size() > 0) {
            Enumeration enumeration = composites.elements();
            compositionExpression = enumeration.nextElement();
        }
        if (compositionExpression != null)
            return compositionExpression.compose(null);
        compileProcesses(processes, compiled);
        return noCompositionExpression(compiled);
    }

    private void compileProcesses(Hashtable paramHashtable1, Hashtable paramHashtable2) {
        Enumeration enumeration = paramHashtable1.elements();
        while (enumeration.hasMoreElements()) {
            ProcessSpec processSpec = enumeration.nextElement();
            if (!processSpec.imported()) {
                StateMachine stateMachine = new StateMachine(processSpec);
                CompactState compactState = stateMachine.makeCompactState();
                this.output.outln("Compiled: " + compactState.name);
                paramHashtable2.put(compactState.name, compactState);
                continue;
            }
            AutCompactState autCompactState = new AutCompactState(processSpec.name, processSpec.importFile);
            this.output.outln("Imported: " + autCompactState.name);
            paramHashtable2.put(autCompactState.name, autCompactState);
        }
    }

    public void parse(Hashtable paramHashtable1, Hashtable paramHashtable2) {
        doparse(paramHashtable1, paramHashtable2, null);
    }

    private void doparse(Hashtable paramHashtable1, Hashtable paramHashtable2, Hashtable paramHashtable3) {
        next_symbol();
        while (this.current.kind != 99) {
            if (this.current.kind == 1) {
                next_symbol();
                constantDefinition(Expression.constants);
            } else if (this.current.kind == 3) {
                next_symbol();
                rangeDefinition();
            } else if (this.current.kind == 9) {
                next_symbol();
                setDefinition();
            } else if (this.current.kind == 10) {
                next_symbol();
                progressDefinition();
            } else if (this.current.kind == 11) {
                next_symbol();
                menuDefinition();
            } else if (this.current.kind == 12) {
                next_symbol();
                animationDefinition();
            } else if (this.current.kind == 21) {
                next_symbol();
                assertDefinition();
            } else if (this.current.kind == 22) {
                next_symbol();
                predicateDefinition();
            } else if (this.current.kind == 19) {
                next_symbol();
                ProcessSpec processSpec = importDefinition();
                if (paramHashtable2.put(processSpec.name.toString(), processSpec) != null)
                    Diagnostics.fatal("duplicate process definition: " + processSpec.name, processSpec.name);
            } else if (this.current.kind == 40 || this.current.kind == 15 || this.current.kind == 16 || this.current.kind == 2 || this.current.kind == 17) {
                boolean bool1 = false;
                boolean bool2 = false;
                boolean bool3 = false;
                boolean bool4 = false;
                if (this.current.kind == 15) {
                    bool1 = true;
                    next_symbol();
                }
                if (this.current.kind == 16) {
                    bool2 = true;
                    next_symbol();
                }
                if (this.current.kind == 17) {
                    bool4 = true;
                    next_symbol();
                }
                if (this.current.kind == 2) {
                    bool3 = true;
                    next_symbol();
                }
                if (this.current.kind != 40) {
                    ProcessSpec processSpec = stateDefns();
                    if (paramHashtable2.put(processSpec.name.toString(), processSpec) != null)
                        Diagnostics.fatal("duplicate process definition: " + processSpec.name, processSpec.name);
                    processSpec.isProperty = bool3;
                    processSpec.isMinimal = bool2;
                    processSpec.isDeterministic = bool1;
                } else if (this.current.kind == 40) {
                    CompositionExpression compositionExpression = composition();
                    compositionExpression.composites = paramHashtable1;
                    compositionExpression.processes = paramHashtable2;
                    compositionExpression.compiledProcesses = paramHashtable3;
                    compositionExpression.output = this.output;
                    compositionExpression.makeDeterministic = bool1;
                    compositionExpression.makeProperty = bool3;
                    compositionExpression.makeMinimal = bool2;
                    compositionExpression.makeCompose = bool4;
                    if (paramHashtable1.put(compositionExpression.name.toString(), compositionExpression) != null)
                        Diagnostics.fatal("duplicate composite definition: " + compositionExpression.name, compositionExpression.name);
                }
            } else {
                ProcessSpec processSpec = stateDefns();
                if (paramHashtable2.put(processSpec.name.toString(), processSpec) != null)
                    Diagnostics.fatal("duplicate process definition: " + processSpec.name, processSpec.name);
            }
            next_symbol();
        }
    }

    private CompositeState noCompositionExpression(Hashtable paramHashtable) {
        Vector vector = new Vector(16);
        Enumeration enumeration = paramHashtable.elements();
        while (enumeration.hasMoreElements())
            vector.addElement(enumeration.nextElement());
        return new CompositeState(vector);
    }

    private CompositionExpression composition() {
        current_is(40, "|| expected");
        next_symbol();
        CompositionExpression compositionExpression = new CompositionExpression();
        current_is(123, "process identifier expected");
        compositionExpression.name = this.current;
        next_symbol();
        paramDefns(compositionExpression.init_constants, compositionExpression.parameters);
        current_is(64, "= expected");
        next_symbol();
        compositionExpression.body = compositebody();
        compositionExpression.priorityActions = priorityDefn(compositionExpression);
        if (this.current.kind == 70 || this.current.kind == 68) {
            compositionExpression.exposeNotHide = (this.current.kind == 68);
            next_symbol();
            compositionExpression.alphaHidden = labelSet();
        }
        current_is(66, "dot expected");
        return compositionExpression;
    }

    private CompositeBody compositebody() {
        CompositeBody compositeBody = new CompositeBody();
        if (this.current.kind == 4) {
            next_symbol();
            compositeBody.boolexpr = new Stack();
            expression(compositeBody.boolexpr);
            current_is(5, "keyword then expected");
            next_symbol();
            compositeBody.thenpart = compositebody();
            if (this.current.kind == 6) {
                next_symbol();
                compositeBody.elsepart = compositebody();
            }
        } else if (this.current.kind == 7) {
            next_symbol();
            compositeBody.range = forallRanges();
            compositeBody.thenpart = compositebody();
        } else {
            if (isLabel()) {
                ActionLabels actionLabels = labelElement();
                if (this.current.kind == 71) {
                    compositeBody.accessSet = actionLabels;
                    next_symbol();
                    if (isLabel()) {
                        compositeBody.prefix = labelElement();
                        current_is(38, " : expected");
                        next_symbol();
                    }
                } else if (this.current.kind == 38) {
                    compositeBody.prefix = actionLabels;
                    next_symbol();
                } else {
                    error(" : or :: expected");
                }
            }
            if (this.current.kind == 53) {
                compositeBody.procRefs = processRefs();
                compositeBody.relabelDefns = relabelDefns();
            } else {
                compositeBody.singleton = processRef();
                compositeBody.relabelDefns = relabelDefns();
            }
        }
        return compositeBody;
    }

    private ActionLabels forallRanges() {
        current_is(62, "range expected");
        ActionLabels actionLabels1 = range();
        ActionLabels actionLabels2 = actionLabels1;
        while (this.current.kind == 62) {
            ActionLabels actionLabels = range();
            actionLabels2.addFollower(actionLabels);
            actionLabels2 = actionLabels;
        }
        return actionLabels1;
    }

    private Vector processRefs() {
        Vector vector = new Vector();
        current_is(53, "( expected");
        next_symbol();
        if (this.current.kind != 54) {
            vector.addElement(compositebody());
            while (this.current.kind == 40) {
                next_symbol();
                vector.addElement(compositebody());
            }
            current_is(54, ") expected");
        }
        next_symbol();
        return vector;
    }

    private Vector relabelDefns() {
        if (this.current.kind != 33)
            return null;
        next_symbol();
        return relabelSet();
    }

    private LabelSet priorityDefn(CompositionExpression paramCompositionExpression) {
        if (this.current.kind != 51 && this.current.kind != 48)
            return null;
        if (this.current.kind == 48)
            paramCompositionExpression.priorityIsLow = false;
        next_symbol();
        return labelSet();
    }

    private Vector relabelSet() {
        current_is(60, "{ expected");
        next_symbol();
        Vector vector = new Vector();
        vector.addElement(relabelDefn());
        while (this.current.kind == 39) {
            next_symbol();
            vector.addElement(relabelDefn());
        }
        current_is(61, "} expected");
        next_symbol();
        return vector;
    }

    private RelabelDefn relabelDefn() {
        RelabelDefn relabelDefn = new RelabelDefn();
        if (this.current.kind == 7) {
            next_symbol();
            relabelDefn.range = forallRanges();
            relabelDefn.defns = relabelSet();
        } else {
            relabelDefn.newlabel = labelElement();
            current_is(33, "/ expected");
            next_symbol();
            relabelDefn.oldlabel = labelElement();
        }
        return relabelDefn;
    }

    private ProcessRef processRef() {
        ProcessRef processRef = new ProcessRef();
        current_is(123, "process identifier expected");
        processRef.name = this.current;
        next_symbol();
        processRef.actualParams = actualParameters();
        return processRef;
    }

    private Vector actualParameters() {
        if (this.current.kind != 53)
            return null;
        Vector vector = new Vector();
        next_symbol();
        Stack stack = new Stack();
        expression(stack);
        vector.addElement(stack);
        while (this.current.kind == 39) {
            next_symbol();
            stack = new Stack();
            expression(stack);
            vector.addElement(stack);
        }
        current_is(54, ") - expected");
        next_symbol();
        return vector;
    }

    private ProcessSpec stateDefns() {
        ProcessSpec processSpec = new ProcessSpec();
        current_is(123, "process identifier expected");
        Symbol symbol = this.current;
        next_symbol();
        paramDefns(processSpec.init_constants, processSpec.parameters);
        push_symbol();
        this.current = symbol;
        processSpec.stateDefns.addElement(stateDefn());
        while (this.current.kind == 39) {
            next_symbol();
            processSpec.stateDefns.addElement(stateDefn());
        }
        if (this.current.kind == 30) {
            next_symbol();
            processSpec.alphaAdditions = labelSet();
        }
        processSpec.alphaRelabel = relabelDefns();
        if (this.current.kind == 70 || this.current.kind == 68) {
            processSpec.exposeNotHide = (this.current.kind == 68);
            next_symbol();
            processSpec.alphaHidden = labelSet();
        }
        processSpec.getname();
        current_is(66, "dot expected");
        return processSpec;
    }

    private boolean isLabelSet() {
        if (this.current.kind == 60)
            return true;
        if (this.current.kind != 123)
            return false;
        return LabelSet.constants.containsKey(this.current.toString());
    }

    private boolean isLabel() {
        return (isLabelSet() || this.current.kind == 124 || this.current.kind == 62);
    }

    private ProcessSpec importDefinition() {
        current_is(123, "imported process identifier expected");
        ProcessSpec processSpec = new ProcessSpec();
        processSpec.name = this.current;
        next_symbol();
        current_is(64, "= expected");
        next_symbol();
        current_is(27, " - imported file name expected");
        processSpec.importFile = new File(this.currentDirectory, this.current.toString());
        return processSpec;
    }

    private void animationDefinition() {
        current_is(123, "animation identifier expected");
        MenuDefinition menuDefinition = new MenuDefinition();
        menuDefinition.name = this.current;
        next_symbol();
        current_is(64, "= expected");
        next_symbol();
        current_is(27, " - XML file name expected");
        menuDefinition.params = this.current;
        next_symbol();
        if (this.current.kind == 18) {
            next_symbol();
            current_is(123, " - target composition name expected");
            menuDefinition.target = this.current;
            next_symbol();
        }
        if (this.current.kind == 17) {
            next_symbol();
            current_is(60, "{ expected");
            next_symbol();
            current_is(123, "animation name expected");
            Symbol symbol = this.current;
            next_symbol();
            menuDefinition.addAnimationPart(symbol, relabelDefns());
            while (this.current.kind == 40) {
                next_symbol();
                current_is(123, "animation name expected");
                symbol = this.current;
                next_symbol();
                menuDefinition.addAnimationPart(symbol, relabelDefns());
            }
            current_is(61, "} expected");
            next_symbol();
        }
        if (this.current.kind == 13) {
            next_symbol();
            menuDefinition.actionMapDefn = relabelSet();
        }
        if (this.current.kind == 14) {
            next_symbol();
            menuDefinition.controlMapDefn = relabelSet();
        }
        push_symbol();
        if (MenuDefinition.definitions.put(menuDefinition.name.toString(), menuDefinition) != null)
            Diagnostics.fatal("duplicate menu/animation definition: " + menuDefinition.name, menuDefinition.name);
    }

    private void menuDefinition() {
        current_is(123, "menu identifier expected");
        MenuDefinition menuDefinition = new MenuDefinition();
        menuDefinition.name = this.current;
        next_symbol();
        current_is(64, "= expected");
        next_symbol();
        menuDefinition.actions = labelElement();
        push_symbol();
        if (MenuDefinition.definitions.put(menuDefinition.name.toString(), menuDefinition) != null)
            Diagnostics.fatal("duplicate menu/animation definition: " + menuDefinition.name, menuDefinition.name);
    }

    private void progressDefinition() {
        current_is(123, "progress test identifier expected");
        ProgressDefinition progressDefinition = new ProgressDefinition();
        progressDefinition.name = this.current;
        next_symbol();
        if (this.current.kind == 62)
            progressDefinition.range = forallRanges();
        current_is(64, "= expected");
        next_symbol();
        if (this.current.kind == 4) {
            next_symbol();
            progressDefinition.pactions = labelElement();
            current_is(5, "then expected");
            next_symbol();
            progressDefinition.cactions = labelElement();
        } else {
            progressDefinition.pactions = labelElement();
        }
        if (ProgressDefinition.definitions.put(progressDefinition.name.toString(), progressDefinition) != null)
            Diagnostics.fatal("duplicate progress test: " + progressDefinition.name, progressDefinition.name);
        push_symbol();
    }

    private void setDefinition() {
        current_is(123, "set identifier expected");
        Symbol symbol = this.current;
        next_symbol();
        current_is(64, "= expected");
        next_symbol();
        LabelSet labelSet = new LabelSet(symbol, setValue());
        push_symbol();
    }

    private LabelSet labelSet() {
        if (this.current.kind == 60)
            return new LabelSet(setValue());
        if (this.current.kind == 123) {
            LabelSet labelSet = (LabelSet) LabelSet.constants.get(this.current.toString());
            if (labelSet == null)
                error("set definition not found for: " + this.current);
            next_symbol();
            return labelSet;
        }
        error("{ or set identifier expected");
        return null;
    }

    private Vector setValue() {
        current_is(60, "{ expected");
        next_symbol();
        Vector vector = new Vector();
        vector.addElement(labelElement());
        while (this.current.kind == 39) {
            next_symbol();
            vector.addElement(labelElement());
        }
        current_is(61, "} expected");
        next_symbol();
        return vector;
    }

    private ActionLabels labelElement() {
        if (this.current.kind != 124 && !isLabelSet() && this.current.kind != 62)
            error("identifier, label set or range expected");
        ActionLabels actionLabels = null;
        if (this.current.kind == 124) {
            if ("tau".equals(this.current.toString()))
                error("'tau' cannot be used as an action label");
            actionLabels = new ActionName(this.current);
            next_symbol();
        } else if (isLabelSet()) {
            LabelSet labelSet = labelSet();
            if (this.current.kind == 70) {
                next_symbol();
                LabelSet labelSet1 = labelSet();
                ActionSetExpr actionSetExpr = new ActionSetExpr(labelSet, labelSet1);
            } else {
                ActionSet actionSet = new ActionSet(labelSet);
            }
        } else if (this.current.kind == 62) {
            actionLabels = range();
        }
        if (this.current.kind == 66 || this.current.kind == 62) {
            if (this.current.kind == 66)
                next_symbol();
            if (actionLabels != null)
                actionLabels.addFollower(labelElement());
        }
        return actionLabels;
    }

    private void constantDefinition(Hashtable paramHashtable) {
        current_is(123, "constant, upper case identifier expected");
        Symbol symbol = this.current;
        next_symbol();
        current_is(64, "= expected");
        next_symbol();
        Stack stack = new Stack();
        simpleExpression(stack);
        push_symbol();
        if (paramHashtable.put(symbol.toString(), Expression.getValue(stack, null, null)) != null)
            Diagnostics.fatal("duplicate constant definition: " + symbol, symbol);
    }

    private void paramDefns(Hashtable paramHashtable, Vector paramVector) {
        if (this.current.kind == 53) {
            next_symbol();
            parameterDefinition(paramHashtable, paramVector);
            while (this.current.kind == 39) {
                next_symbol();
                parameterDefinition(paramHashtable, paramVector);
            }
            current_is(54, ") expected");
            next_symbol();
        }
    }

    private void parameterDefinition(Hashtable paramHashtable, Vector paramVector) {
        current_is(123, "parameter, upper case identifier expected");
        Symbol symbol = this.current;
        next_symbol();
        current_is(64, "= expected");
        next_symbol();
        Stack stack = new Stack();
        expression(stack);
        push_symbol();
        if (paramHashtable.put(symbol.toString(), Expression.getValue(stack, null, null)) != null)
            Diagnostics.fatal("duplicate parameter definition: " + symbol, symbol);
        if (paramVector != null) {
            paramVector.addElement(symbol.toString());
            next_symbol();
        }
    }

    private StateDefn stateDefn() {
        StateDefn stateDefn = new StateDefn();
        current_is(123, "process identifier expected");
        stateDefn.name = this.current;
        next_symbol();
        if (this.current.kind == 68) {
            stateDefn.accept = true;
            next_symbol();
        }
        if (this.current.kind == 66 || this.current.kind == 62) {
            if (this.current.kind == 66)
                next_symbol();
            stateDefn.range = labelElement();
        }
        current_is(64, "= expected");
        next_symbol();
        stateDefn.stateExpr = stateExpr();
        return stateDefn;
    }

    private Stack getEvaluatedExpression() {
        Stack stack = new Stack();
        simpleExpression(stack);
        int i = Expression.evaluate(stack, null, null);
        stack = new Stack();
        stack.push(new Symbol(25, i));
        return stack;
    }

    private void rangeDefinition() {
        current_is(123, "range name, upper case identifier expected");
        Symbol symbol = this.current;
        next_symbol();
        current_is(64, "= expected");
        next_symbol();
        Range range = new Range();
        range.low = getEvaluatedExpression();
        current_is(67, "..  expected");
        next_symbol();
        range.high = getEvaluatedExpression();
        if (Range.ranges.put(symbol.toString(), range) != null)
            Diagnostics.fatal("duplicate range definition: " + symbol, symbol);
        push_symbol();
    }

    private ActionLabels range() {
        if (this.current.kind == 62) {
            ActionExpr actionExpr;
            next_symbol();
            Stack stack1 = null;
            Stack stack2 = null;
            if (this.current.kind != 124) {
                if (isLabelSet()) {
                    ActionSet actionSet = new ActionSet(labelSet());
                } else if (this.current.kind == 123 && Range.ranges.containsKey(this.current.toString())) {
                    ActionRange actionRange = new ActionRange((Range) Range.ranges.get(this.current.toString()));
                    next_symbol();
                } else {
                    stack1 = new Stack();
                    expression(stack1);
                    actionExpr = new ActionExpr(stack1);
                }
                if (this.current.kind == 67) {
                    next_symbol();
                    stack2 = new Stack();
                    expression(stack2);
                    ActionRange actionRange = new ActionRange(stack1, stack2);
                }
            } else {
                Symbol symbol = this.current;
                next_symbol();
                if (this.current.kind == 38) {
                    next_symbol();
                    if (isLabelSet()) {
                        ActionVarSet actionVarSet = new ActionVarSet(symbol, labelSet());
                    } else if (this.current.kind == 123 && Range.ranges.containsKey(this.current.toString())) {
                        ActionVarRange actionVarRange = new ActionVarRange(symbol, (Range) Range.ranges.get(this.current.toString()));
                        next_symbol();
                    } else {
                        stack1 = new Stack();
                        expression(stack1);
                        current_is(67, "..  expected");
                        next_symbol();
                        stack2 = new Stack();
                        expression(stack2);
                        ActionVarRange actionVarRange = new ActionVarRange(symbol, stack1, stack2);
                    }
                } else {
                    push_symbol();
                    this.current = symbol;
                    stack1 = new Stack();
                    expression(stack1);
                    if (this.current.kind == 67) {
                        next_symbol();
                        stack2 = new Stack();
                        expression(stack2);
                        ActionRange actionRange = new ActionRange(stack1, stack2);
                    } else {
                        actionExpr = new ActionExpr(stack1);
                    }
                }
            }
            current_is(63, "] expected");
            next_symbol();
            return actionExpr;
        }
        return null;
    }

    private StateExpr stateExpr() {
        StateExpr stateExpr = new StateExpr();
        if (this.current.kind == 123) {
            stateRef(stateExpr);
        } else if (this.current.kind == 4) {
            next_symbol();
            stateExpr.boolexpr = new Stack();
            expression(stateExpr.boolexpr);
            current_is(5, "keyword then expected");
            next_symbol();
            stateExpr.thenpart = stateExpr();
            if (this.current.kind == 6) {
                next_symbol();
                stateExpr.elsepart = stateExpr();
            } else {
                Symbol symbol = new Symbol(123, "STOP");
                StateExpr stateExpr1 = new StateExpr();
                stateExpr1.name = symbol;
                stateExpr.elsepart = stateExpr1;
            }
        } else if (this.current.kind == 53) {
            next_symbol();
            choiceExpr(stateExpr);
            current_is(54, ") expected");
            next_symbol();
        } else {
            error(" (, if or process identifier expected");
        }
        return stateExpr;
    }

    private void stateRef(StateExpr paramStateExpr) {
        current_is(123, "process identifier expected");
        paramStateExpr.name = this.current;
        next_symbol();
        while (this.current.kind == 65 || this.current.kind == 53) {
            paramStateExpr.addSeqProcessRef(new SeqProcessRef(paramStateExpr.name, actualParameters()));
            next_symbol();
            current_is(123, "process identifier expected");
            paramStateExpr.name = this.current;
            next_symbol();
        }
        if (this.current.kind == 62) {
            paramStateExpr.expr = new Vector();
            while (this.current.kind == 62) {
                next_symbol();
                Stack stack = new Stack();
                expression(stack);
                paramStateExpr.expr.addElement(stack);
                current_is(63, "] expected");
                next_symbol();
            }
        }
    }

    private void choiceExpr(StateExpr paramStateExpr) {
        paramStateExpr.choices = new Vector();
        paramStateExpr.choices.addElement(choiceElement());
        while (this.current.kind == 41) {
            next_symbol();
            paramStateExpr.choices.addElement(choiceElement());
        }
    }

    private ChoiceElement choiceElement() {
        ChoiceElement choiceElement1 = new ChoiceElement();
        if (this.current.kind == 8) {
            next_symbol();
            choiceElement1.guard = new Stack();
            expression(choiceElement1.guard);
        }
        choiceElement1.action = labelElement();
        current_is(69, "-> expected");
        ChoiceElement choiceElement2 = choiceElement1;
        ChoiceElement choiceElement3 = choiceElement1;
        next_symbol();
        while (this.current.kind == 124 || this.current.kind == 62 || isLabelSet()) {
            StateExpr stateExpr = new StateExpr();
            choiceElement2 = new ChoiceElement();
            choiceElement2.action = labelElement();
            stateExpr.choices = new Vector();
            stateExpr.choices.addElement(choiceElement2);
            choiceElement3.stateExpr = stateExpr;
            choiceElement3 = choiceElement2;
            current_is(69, "-> expected");
            next_symbol();
        }
        choiceElement2.stateExpr = stateExpr();
        return choiceElement1;
    }

    private Symbol event() {
        current_is(124, "event identifier expected");
        Symbol symbol = this.current;
        next_symbol();
        return symbol;
    }

    private ActionLabels labelConstant() {
        next_symbol();
        ActionLabels actionLabels = labelElement();
        if (actionLabels != null)
            return actionLabels;
        error("label definition expected");
        return null;
    }

    private void set_select(Stack paramStack) {
        Symbol symbol1 = this.current;
        next_symbol();
        current_is(53, "( expected to start set index selection");
        Symbol symbol2 = this.current;
        symbol2.setAny(labelConstant());
        symbol2.kind = 98;
        paramStack.push(symbol2);
        current_is(39, ", expected before set index expression");
        next_symbol();
        expression(paramStack);
        current_is(54, ") expected to end set index selection");
        next_symbol();
        paramStack.push(symbol1);
    }

    private void unary(Stack paramStack) {
        Symbol symbol1;
        Symbol symbol2;
        switch (this.current.kind) {
            case 30 :
                symbol1 = this.current;
                symbol1.kind = 29;
                next_symbol();
                break;
            case 31 :
                symbol1 = this.current;
                symbol1.kind = 28;
                next_symbol();
                break;
            case 45 :
                symbol1 = this.current;
                next_symbol();
                break;
            default :
                symbol1 = null;
                break;
        }
        switch (this.current.kind) {
            case 25 :
            case 123 :
            case 124 :
                paramStack.push(this.current);
                next_symbol();
                break;
            case 53 :
                next_symbol();
                expression(paramStack);
                current_is(54, ") expected to end expression");
                next_symbol();
                break;
            case 73 :
                symbol1 = new Symbol(this.current);
            case 72 :
                symbol2 = this.current;
                symbol2.setAny(labelConstant());
                symbol2.kind = 98;
                paramStack.push(symbol2);
                break;
            case 68 :
                set_select(paramStack);
                break;
            default :
                error("syntax error in expression");
                break;
        }
        if (symbol1 != null)
            paramStack.push(symbol1);
    }

    private void multiplicative(Stack paramStack) {
        unary(paramStack);
        while (this.current.kind == 32 || this.current.kind == 33 || this.current.kind == 34) {
            Symbol symbol = this.current;
            next_symbol();
            unary(paramStack);
            paramStack.push(symbol);
        }
    }

    private void additive(Stack paramStack) {
        multiplicative(paramStack);
        while (this.current.kind == 30 || this.current.kind == 31) {
            Symbol symbol = this.current;
            next_symbol();
            multiplicative(paramStack);
            paramStack.push(symbol);
        }
    }

    private void shift(Stack paramStack) {
        additive(paramStack);
        while (this.current.kind == 48 || this.current.kind == 51) {
            Symbol symbol = this.current;
            next_symbol();
            additive(paramStack);
            paramStack.push(symbol);
        }
    }

    private void relational(Stack paramStack) {
        shift(paramStack);
        while (this.current.kind == 47 || this.current.kind == 46 || this.current.kind == 50 || this.current.kind == 49) {
            Symbol symbol = this.current;
            next_symbol();
            shift(paramStack);
            paramStack.push(symbol);
        }
    }

    private void equality(Stack paramStack) {
        relational(paramStack);
        while (this.current.kind == 52 || this.current.kind == 44) {
            Symbol symbol = this.current;
            next_symbol();
            relational(paramStack);
            paramStack.push(symbol);
        }
    }

    private void and(Stack paramStack) {
        equality(paramStack);
        while (this.current.kind == 43) {
            Symbol symbol = this.current;
            next_symbol();
            equality(paramStack);
            paramStack.push(symbol);
        }
    }

    private void exclusive_or(Stack paramStack) {
        and(paramStack);
        while (this.current.kind == 35) {
            Symbol symbol = this.current;
            next_symbol();
            and(paramStack);
            paramStack.push(symbol);
        }
    }

    private void inclusive_or(Stack paramStack) {
        exclusive_or(paramStack);
        while (this.current.kind == 41) {
            Symbol symbol = this.current;
            next_symbol();
            exclusive_or(paramStack);
            paramStack.push(symbol);
        }
    }

    private void logical_and(Stack paramStack) {
        inclusive_or(paramStack);
        while (this.current.kind == 42) {
            Symbol symbol = this.current;
            next_symbol();
            inclusive_or(paramStack);
            paramStack.push(symbol);
        }
    }

    private void logical_or(Stack paramStack) {
        logical_and(paramStack);
        while (this.current.kind == 40) {
            Symbol symbol = this.current;
            next_symbol();
            logical_and(paramStack);
            paramStack.push(symbol);
        }
    }

    private void expression(Stack paramStack) {
        logical_or(paramStack);
    }

    private void simpleExpression(Stack paramStack) {
        additive(paramStack);
    }

    private void assertDefinition() {
        current_is(123, "LTL property identifier expected");
        Symbol symbol = this.current;
        next_symbol();
        current_is(64, "= expected");
        next_symbol();
        FormulaFactory formulaFactory = (new LTLparser(this.lex)).parse();
        push_symbol();
        if ((processes != null && processes.get(symbol.toString()) != null) || (composites != null && composites.get(symbol.toString()) != null))
            Diagnostics.fatal("name already defined  " + symbol, symbol);
        AssertDefinition.put(symbol, formulaFactory);
    }

    private void predicateDefinition() {
        current_is(123, "predicate identifier expected");
        Symbol symbol = this.current;
        next_symbol();
        current_is(64, "= expected");
        next_symbol();
        current_is(47, "< expected");
        next_symbol();
        ActionLabels actionLabels1 = labelElement();
        current_is(39, ", expected");
        next_symbol();
        ActionLabels actionLabels2 = labelElement();
        current_is(50, "> expected");
        next_symbol();
        if (this.current.kind == 24) {
            next_symbol();
            Stack stack = new Stack();
            simpleExpression(stack);
            push_symbol();
            PredicateDefinition.put(symbol, actionLabels1, actionLabels2, stack);
        } else {
            push_symbol();
            PredicateDefinition.put(symbol, actionLabels1, actionLabels2, null);
        }
    }
}
