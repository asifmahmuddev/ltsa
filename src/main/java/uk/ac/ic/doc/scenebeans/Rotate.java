package uk.ac.ic.doc.scenebeans;

import java.awt.geom.AffineTransform;
import java.io.Serializable;

public class Rotate extends TransformBase {
    private double _theta;

    public Rotate() {
        this._theta = 0.0D;
    }

    public Rotate(double paramDouble, SceneGraph paramSceneGraph) {
        super(paramSceneGraph);
        this._theta = paramDouble;
    }

    public double getAngle() {
        return this._theta;
    }

    public void setAngle(double paramDouble) {
        this._theta = paramDouble;
        setDirty(true);
    }

    public AffineTransform getTransform() {
        return AffineTransform.getRotateInstance(this._theta);
    }

    public class Angle implements DoubleBehaviourListener, Serializable {
        private final Rotate this$0;

        public Angle(Rotate this$0) {
            this.this$0 = this$0;
        }

        public void behaviourUpdated(double param1Double) {
            this.this$0.setAngle(param1Double);
        }
    }

    public final Angle newAngleAdapter() {
        return new Angle(this);
    }
}
