package uk.ac.ic.doc.scenebeans;

import java.awt.geom.Point2D;
import java.util.EventListener;

public interface PointBehaviourListener extends EventListener {
    void behaviourUpdated(Point2D paramPoint2D);
}
