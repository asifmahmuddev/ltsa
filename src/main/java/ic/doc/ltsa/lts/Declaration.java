package ic.doc.ltsa.lts;

public abstract class Declaration {
    public static final int TAU = 0;
    public static final int ERROR = -1;
    public static final int STOP = 0;
    public static final int SUCCESS = 1;

    public void explicitStates(StateMachine paramStateMachine) {
    }

    public void crunch(StateMachine paramStateMachine) {
    }

    public void transition(StateMachine paramStateMachine) {
    }
}
