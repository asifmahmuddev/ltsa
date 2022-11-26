package ic.doc.ltsa.lts;

import java.util.Vector;

public interface Automata {
    String[] getAlphabet();

    MyList getTransitions(byte[] paramArrayOfbyte);

    String getViolatedProperty();

    Vector getTraceToState(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2);

    boolean END(byte[] paramArrayOfbyte);

    byte[] START();

    void setStackChecker(StackCheck paramStackCheck);

    boolean isPartialOrder();

    void disablePartialOrder();
}
