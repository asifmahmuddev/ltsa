package uk.ac.ic.doc.scenebeans.behaviour;

import java.awt.geom.Point2D;
import java.io.Serializable;
import uk.ac.ic.doc.scenebeans.DoubleBehaviourListener;
import uk.ac.ic.doc.scenebeans.PointBehaviourListener;

public class CopyPoint extends PointActivityBase implements Serializable {
    private Point2D _point = new Point2D.Double(0.0D, 0.0D);
    private Point2D _offset = new Point2D.Double(0.0D, 0.0D);

    public Point2D getPoint() {
        return this._point;
    }

    public void setPoint(Point2D paramPoint2D) {
        this._point = paramPoint2D;
    }

    public Point2D getOffset() {
        return this._offset;
    }

    public void setOffset(Point2D paramPoint2D) {
        this._offset = paramPoint2D;
    }

    public double getX() {
        return this._point.getX();
    }

    public void setX(double paramDouble) {
        this._point = new Point2D.Double(paramDouble, this._point.getY());
    }

    public double getY() {
        return this._point.getY();
    }

    public void setY(double paramDouble) {
        this._point = new Point2D.Double(this._point.getX(), paramDouble);
    }

    public Point2D getValue() {
        return new Point2D.Double(this._point.getX() + this._offset.getX(), this._point.getY() + this._offset.getY());
    }

    public boolean isFinite() {
        return false;
    }

    public void reset() {
        postUpdate(getValue());
    }

    public void performActivity(double paramDouble) {
        postUpdate(getValue());
    }

    class PointAdapter implements PointBehaviourListener {
        private final CopyPoint this$0;

        PointAdapter(CopyPoint this$0) {
            this.this$0 = this$0;
        }

        public void behaviourUpdated(Point2D param1Point2D) {
            this.this$0.setPoint(param1Point2D);
        }
    }

    class OffsetAdapter implements PointBehaviourListener {
        private final CopyPoint this$0;

        OffsetAdapter(CopyPoint this$0) {
            this.this$0 = this$0;
        }

        public void behaviourUpdated(Point2D param1Point2D) {
            this.this$0.setOffset(param1Point2D);
        }
    }

    class XAdapter implements DoubleBehaviourListener {
        private final CopyPoint this$0;

        XAdapter(CopyPoint this$0) {
            this.this$0 = this$0;
        }

        public void behaviourUpdated(double param1Double) {
            this.this$0.setX(param1Double);
        }
    }

    class YAdapter implements DoubleBehaviourListener {
        private final CopyPoint this$0;

        YAdapter(CopyPoint this$0) {
            this.this$0 = this$0;
        }

        public void behaviourUpdated(double param1Double) {
            this.this$0.setY(param1Double);
        }
    }

    public final XAdapter newXAdapter() {
        return new XAdapter(this);
    }

    public final YAdapter newYAdapter() {
        return new YAdapter(this);
    }

    public final PointAdapter newPointAdapter() {
        return new PointAdapter(this);
    }

    public final OffsetAdapter newOffsetAdapter() {
        return new OffsetAdapter(this);
    }
}
