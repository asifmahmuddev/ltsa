package uk.ac.ic.doc.scenebeans;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.io.Serializable;

public class Circle extends PrimitiveBase {
    private double _radius;

    public Circle() {
        this._radius = 1.0D;
    }

    public Circle(double paramDouble) {
        this._radius = paramDouble;
    }

    public Shape getShape(Graphics2D paramGraphics2D) {
        return new Ellipse2D.Double(-this._radius, -this._radius, this._radius * 2.0D, this._radius * 2.0D);
    }

    public double getRadius() {
        return this._radius;
    }

    public void setRadius(double paramDouble) {
        this._radius = paramDouble;
        setDirty(true);
    }

    public class Radius implements DoubleBehaviourListener, Serializable {
        private final Circle this$0;

        public Radius(Circle this$0) {
            this.this$0 = this$0;
        }

        public void behaviourUpdated(double param1Double) {
            this.this$0.setRadius(param1Double);
        }
    }

    public final Radius newRadiusAdapter() {
        return new Radius(this);
    }
}
