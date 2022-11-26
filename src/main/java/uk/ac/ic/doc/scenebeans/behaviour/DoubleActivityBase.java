package uk.ac.ic.doc.scenebeans.behaviour;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import uk.ac.ic.doc.scenebeans.DoubleBehaviour;
import uk.ac.ic.doc.scenebeans.DoubleBehaviourListener;
import uk.ac.ic.doc.scenebeans.activity.FiniteActivityBase;

public abstract class DoubleActivityBase extends FiniteActivityBase implements DoubleBehaviour {
    private List _behaviour_listeners;

    protected DoubleActivityBase() {
        this._behaviour_listeners = new ArrayList();
    }

    protected DoubleActivityBase(List paramList) {
        this._behaviour_listeners = paramList;
    }

    public synchronized void addDoubleBehaviourListener(DoubleBehaviourListener paramDoubleBehaviourListener) {
        this._behaviour_listeners.add(paramDoubleBehaviourListener);
    }

    public synchronized void removeDoubleBehaviourListener(DoubleBehaviourListener paramDoubleBehaviourListener) {
        this._behaviour_listeners.remove(paramDoubleBehaviourListener);
    }

    protected synchronized void postUpdate(double paramDouble) {
        for (Iterator iterator = this._behaviour_listeners.iterator(); iterator.hasNext();)
            ((DoubleBehaviourListener) iterator.next()).behaviourUpdated(paramDouble);
    }
}
