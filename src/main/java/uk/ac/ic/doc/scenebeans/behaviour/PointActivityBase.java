package uk.ac.ic.doc.scenebeans.behaviour;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import uk.ac.ic.doc.scenebeans.PointBehaviour;
import uk.ac.ic.doc.scenebeans.PointBehaviourListener;
import uk.ac.ic.doc.scenebeans.activity.FiniteActivityBase;

public abstract class PointActivityBase extends FiniteActivityBase implements PointBehaviour, Serializable {
    private List _listeners;

    protected PointActivityBase() {
        this._listeners = new ArrayList();
    }

    protected PointActivityBase(List paramList) {
        this._listeners = paramList;
    }

    public synchronized void addPointBehaviourListener(PointBehaviourListener paramPointBehaviourListener) {
        this._listeners.add(paramPointBehaviourListener);
    }

    public synchronized void removePointBehaviourListener(PointBehaviourListener paramPointBehaviourListener) {
        this._listeners.remove(paramPointBehaviourListener);
    }

    protected synchronized void postUpdate(Point2D paramPoint2D) {
        for (Iterator iterator = this._listeners.iterator(); iterator.hasNext();)
            ((PointBehaviourListener) iterator.next()).behaviourUpdated(paramPoint2D);
    }
}
