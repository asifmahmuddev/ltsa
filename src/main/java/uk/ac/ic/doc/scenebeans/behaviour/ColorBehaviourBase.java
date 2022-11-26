package uk.ac.ic.doc.scenebeans.behaviour;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import uk.ac.ic.doc.scenebeans.ColorBehaviour;
import uk.ac.ic.doc.scenebeans.ColorBehaviourListener;

public abstract class ColorBehaviourBase implements ColorBehaviour, Serializable {
    private List _listeners;

    protected ColorBehaviourBase() {
        this._listeners = new ArrayList();
    }

    protected ColorBehaviourBase(List paramList) {
        this._listeners = paramList;
    }

    public synchronized void addColorBehaviourListener(ColorBehaviourListener paramColorBehaviourListener) {
        this._listeners.add(paramColorBehaviourListener);
    }

    public synchronized void removeColorBehaviourListener(ColorBehaviourListener paramColorBehaviourListener) {
        this._listeners.remove(paramColorBehaviourListener);
    }

    protected synchronized void postUpdate(Color paramColor) {
        for (Iterator iterator = this._listeners.iterator(); iterator.hasNext();)
            ((ColorBehaviourListener) iterator.next()).behaviourUpdated(paramColor);
    }
}
