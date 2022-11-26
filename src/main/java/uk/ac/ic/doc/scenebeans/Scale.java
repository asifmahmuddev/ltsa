package uk.ac.ic.doc.scenebeans;

import java.awt.geom.AffineTransform;
import java.io.Serializable;

public class Scale extends TransformBase {
    private double _x;
    private double _y;

    public Scale() {
        this._x = 1.0D;
        this._y = 1.0D;
    }

    public Scale(double paramDouble1, double paramDouble2, SceneGraph paramSceneGraph) {
        super(paramSceneGraph);
        this._x = paramDouble1;
        this._y = paramDouble2;
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

    public AffineTransform getTransform() {
        return AffineTransform.getScaleInstance(this._x, this._y);
    }

    public class X implements DoubleBehaviourListener, Serializable {
        private final Scale this$0;

        public X(Scale this$0) {
            this.this$0 = this$0;
        }

        public void behaviourUpdated(double param1Double) {
            this.this$0.setX(param1Double);
        }
    }

    public final X newXAdapter() {
        return new X(this);
    }

    public class Y implements DoubleBehaviourListener, Serializable {
        private final Scale this$0;

        public Y(Scale this$0) {
            this.this$0 = this$0;
        }

        public void behaviourUpdated(double param1Double) {
            this.this$0.setY(param1Double);
        }
    }

    public final Y newYAdapter() {
        return new Y(this);
    }
}
