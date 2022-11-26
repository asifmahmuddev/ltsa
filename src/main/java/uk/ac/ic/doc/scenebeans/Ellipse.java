package uk.ac.ic.doc.scenebeans;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.io.Serializable;

public class Ellipse extends PrimitiveBase {
    private double _x_radius;
    private double _y_radius;

    public Ellipse() {
        this._x_radius = 1.0D;
        this._y_radius = 1.0D;
    }

    public Ellipse(double paramDouble1, double paramDouble2) {
        this._x_radius = paramDouble1;
        this._y_radius = paramDouble2;
    }

    public Shape getShape(Graphics2D paramGraphics2D) {
        return new Ellipse2D.Double(-this._x_radius, -this._y_radius, this._x_radius * 2.0D, this._y_radius * 2.0D);
    }

    public double getXRadius() {
        return this._x_radius;
    }

    public void setXRadius(double paramDouble) {
        this._x_radius = paramDouble;
        setDirty(true);
    }

    public double getYRadius() {
        return this._y_radius;
    }

    public void setYRadius(double paramDouble) {
        this._y_radius = paramDouble;
        setDirty(true);
    }

    public class XRadius implements DoubleBehaviourListener, Serializable {
        private final Ellipse this$0;

        public XRadius(Ellipse this$0) {
            this.this$0 = this$0;
        }

        public void behaviourUpdated(double param1Double) {
            this.this$0.setXRadius(param1Double);
        }
    }

    public final XRadius newXRadiusAdapter() {
        return new XRadius(this);
    }

    public class YRadius implements DoubleBehaviourListener, Serializable {
        private final Ellipse this$0;

        public YRadius(Ellipse this$0) {
            this.this$0 = this$0;
        }

        public void behaviourUpdated(double param1Double) {
            this.this$0.setYRadius(param1Double);
        }
    }

    public final YRadius newYRadiusAdapter() {
        return new YRadius(this);
    }
}
