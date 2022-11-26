package uk.ac.ic.doc.scenebeans;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.Serializable;

public class Translate extends TransformBase {
    private double _x;
    private double _y;

    public Translate() {
        this._x = 0.0D;
        this._y = 0.0D;
    }

    public Translate(double paramDouble1, double paramDouble2, SceneGraph paramSceneGraph) {
        super(paramSceneGraph);
        this._x = paramDouble1;
        this._y = paramDouble2;
    }

    public Point2D getTranslation() {
        return new Point2D.Double(this._x, this._y);
    }

    public void setTranslation(Point2D paramPoint2D) {
        this._x = paramPoint2D.getX();
        this._y = paramPoint2D.getY();
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
        return AffineTransform.getTranslateInstance(this._x, this._y);
    }

    public class TranslationAdapter implements PointBehaviourListener, Serializable {
        private final Translate this$0;

        public TranslationAdapter(Translate this$0) {
            this.this$0 = this$0;
        }

        public void behaviourUpdated(Point2D param1Point2D) {
            this.this$0.setX(param1Point2D.getX());
            this.this$0.setY(param1Point2D.getY());
        }
    }

    public final TranslationAdapter newTranslationAdapter() {
        return new TranslationAdapter(this);
    }

    public class XAdapter implements DoubleBehaviourListener, Serializable {
        private final Translate this$0;

        public XAdapter(Translate this$0) {
            this.this$0 = this$0;
        }

        public void behaviourUpdated(double param1Double) {
            this.this$0.setX(param1Double);
        }
    }

    public final XAdapter newXAdapter() {
        return new XAdapter(this);
    }

    public class YAdapter implements DoubleBehaviourListener, Serializable {
        private final Translate this$0;

        public YAdapter(Translate this$0) {
            this.this$0 = this$0;
        }

        public void behaviourUpdated(double param1Double) {
            this.this$0.setY(param1Double);
        }
    }

    public final YAdapter newYAdapter() {
        return new YAdapter(this);
    }
}
