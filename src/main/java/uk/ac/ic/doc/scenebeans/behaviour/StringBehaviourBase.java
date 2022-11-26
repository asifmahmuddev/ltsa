package uk.ac.ic.doc.scenebeans.behaviour;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import uk.ac.ic.doc.scenebeans.StringBehaviour;
import uk.ac.ic.doc.scenebeans.StringBehaviourListener;

public abstract class StringBehaviourBase implements StringBehaviour, Serializable {
    private List _behaviour_listeners;

    protected StringBehaviourBase() {
        this._behaviour_listeners = new ArrayList();
    }

    protected StringBehaviourBase(List paramList) {
        this._behaviour_listeners = paramList;
    }

    public synchronized void addStringBehaviourListener(StringBehaviourListener paramStringBehaviourListener) {
        this._behaviour_listeners.add(paramStringBehaviourListener);
    }

    public synchronized void removeStringBehaviourListener(StringBehaviourListener paramStringBehaviourListener) {
        this._behaviour_listeners.remove(paramStringBehaviourListener);
    }

    protected synchronized void postUpdate(String paramString) {
        for (Iterator iterator = this._behaviour_listeners.iterator(); iterator.hasNext();)
            ((StringBehaviourListener) iterator.next()).behaviourUpdated(paramString);
    }
}
