package ic.doc.extension;

import java.util.BitSet;
import java.util.Vector;

public interface WebAnimator {
    BitSet initialise(Vector paramVector);

    BitSet menuStep(int paramInt);

    String[] getMenuNames();

    boolean isError();
}
