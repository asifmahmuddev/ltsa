package uk.ac.ic.doc.scenebeans.behaviour;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import uk.ac.ic.doc.scenebeans.DoubleBehaviour;
import uk.ac.ic.doc.scenebeans.DoubleBehaviourListener;

public abstract class DoubleBehaviourBase implements DoubleBehaviour, Serializable {
    private List _behaviour_listeners;

    protected DoubleBehaviourBase() {
        this._behaviour_listeners = new ArrayList();
    }

    protected DoubleBehaviourBase(List paramList) {
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
