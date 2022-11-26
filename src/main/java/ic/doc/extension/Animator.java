package ic.doc.extension;

import java.util.BitSet;
import java.util.Vector;

public interface Animator {
    BitSet initialise(Vector paramVector);

    String[] getMenuNames();

    String[] getAllNames();

    BitSet menuStep(int paramInt);

    BitSet singleStep();

    int actionChosen();

    String actionNameChosen();

    boolean isError();

    boolean isEnd();

    boolean nonMenuChoice();

    BitSet getPriorityActions();

    boolean getPriority();

    void message(String paramString);

    boolean hasErrorTrace();

    boolean traceChoice();

    BitSet traceStep();
}
