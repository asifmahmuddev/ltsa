package uk.ac.ic.doc.scenebeans;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

public class Rectangle extends PrimitiveBase {
    private double _x;
    private double _y;
    private double _w;
    private double _h;

    public Rectangle() {
        this(0.0D, 0.0D, 1.0D, 1.0D);
    }

    public Rectangle(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) {
        this._x = paramDouble1;
        this._y = paramDouble2;
        this._w = paramDouble3;
        this._h = paramDouble4;
    }

    public Shape getShape(Graphics2D paramGraphics2D) {
        return new Rectangle2D.Double(this._x, this._y, this._w, this._h);
    }

    public double getX() {
        return this._x;
    }

    public void setX(double paramDouble) {
        this._x = paramDouble;
        setDirty(true);
    }

    public double getY() {
        return this._y;
    }

    public void setY(double paramDouble) {
        this._y = paramDouble;
        setDirty(true);
    }

    public double getWidth() {
        return this._w;
    }

    public void setWidth(double paramDouble) {
        this._w = paramDouble;
        setDirty(true);
    }

    public double getHeight() {
        return this._h;
    }

    public void setHeight(double paramDouble) {
        this._h = paramDouble;
        setDirty(true);
    }

    class XAdapter implements DoubleBehaviourListener, Serializable {
        private final Rectangle this$0;

        XAdapter(Rectangle this$0) {
            this.this$0 = this$0;
        }

        public void behaviourUpdated(double param1Double) {
            this.this$0.setX(param1Double);
        }
    }

    public final XAdapter newXAdapter() {
        return new XAdapter(this);
    }

    class YAdapter implements DoubleBehaviourListener, Serializable {
        private final Rectangle this$0;

        YAdapter(Rectangle this$0) {
            this.this$0 = this$0;
        }

        public void behaviourUpdated(double param1Double) {
            this.this$0.setY(param1Double);
        }
    }

    public final YAdapter newYAdapter() {
        return new YAdapter(this);
    }

    class WidthAdapter implements DoubleBehaviourListener, Serializable {
        private final Rectangle this$0;

        WidthAdapter(Rectangle this$0) {
            this.this$0 = this$0;
        }

        public void behaviourUpdated(double param1Double) {
            this.this$0.setWidth(param1Double);
        }
    }

    public final WidthAdapter newWidthAdapter() {
        return new WidthAdapter(this);
    }

    class HeightAdapter implements DoubleBehaviourListener, Serializable {
        private final Rectangle this$0;

        HeightAdapter(Rectangle this$0) {
            this.this$0 = this$0;
        }

        public void behaviourUpdated(double param1Double) {
            this.this$0.setHeight(param1Double);
        }
    }

    public final HeightAdapter newHeightAdapter() {
        return new HeightAdapter(this);
    }
}
