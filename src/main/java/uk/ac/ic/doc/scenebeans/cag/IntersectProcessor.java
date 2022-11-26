package uk.ac.ic.doc.scenebeans.cag;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;

public class IntersectProcessor extends CAGProcessor {
    public IntersectProcessor(Graphics2D paramGraphics2D) {
        super(paramGraphics2D);
    }

    public IntersectProcessor(Graphics2D paramGraphics2D, AffineTransform paramAffineTransform) {
        super(paramGraphics2D, paramAffineTransform);
    }

    protected void accumulateArea(Area paramArea1, Area paramArea2) {
        paramArea1.intersect(paramArea2);
    }
}
