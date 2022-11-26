package uk.ac.ic.doc.scenebeans.behaviour;

import java.awt.geom.Point2D;
import java.io.Serializable;
import uk.ac.ic.doc.scenebeans.DoubleBehaviourListener;
import uk.ac.ic.doc.scenebeans.PointBehaviourListener;

public class MovePoint extends PointActivityBase implements Serializable {
    private Point2D _from;
    private Point2D _to;
    private double _x_len;
    private double _y_len;
    private double _duration;
    private double _timeout;

    public MovePoint() {
        this._from = new Point2D.Double(0.0D, 0.0D);
        this._to = new Point2D.Double(0.0D, 0.0D);
        this._duration = this._timeout = 1.0D;
        setDistances();
    }

    public MovePoint(Point2D paramPoint2D1, Point2D paramPoint2D2, double paramDouble) {
        this._from = paramPoint2D1;
        this._to = paramPoint2D2;
        this._duration = this._timeout = paramDouble;
        setDistances();
    }

    public Point2D getFrom() {
        return this._from;
    }

    public void setFrom(Point2D paramPoint2D) {
        this._from = paramPoint2D;
        setDistances();
    }

    public Point2D getTo() {
        return this._to;
    }

    public void setTo(Point2D paramPoint2D) {
        this._to = paramPoint2D;
        setDistances();
    }

    public double getDuration() {
        return this._duration;
    }

    public void setDuration(double paramDouble) {
        this._duration = this._timeout = paramDouble;
    }

    public Point2D getValue() {
        double d1 = 1.0D - this._timeout / this._duration;
        double d2 = this._from.getX() + d1 * this._x_len;
        double d3 = this._from.getY() + d1 * this._y_len;
        return new Point2D.Double(d2, d3);
    }

    public boolean isFinite() {
        return true;
    }

    public void reset() {
        this._timeout = this._duration;
        postUpdate(getValue());
    }

    public void performActivity(double paramDouble) {
        if (this._timeout > 0.0D) {
            this._timeout -= paramDouble;
            if (this._timeout <= 0.0D) {
                this._timeout = 0.0D;
                postActivityComplete();
            }
            postUpdate(getValue());
        }
    }

    private void setDistances() {
        this._x_len = this._to.getX() - this._from.getX();
        this._y_len = this._to.getY() - this._from.getY();
    }

    class FromAdapter implements PointBehaviourListener, Serializable {
        private final MovePoint this$0;

        FromAdapter(MovePoint this$0) {
            this.this$0 = this$0;
        }

        public void behaviourUpdated(Point2D param1Point2D) {
            this.this$0.setFrom(param1Point2D);
        }
    }

    public final PointBehaviourListener newFromAdapter() {
        return new FromAdapter(this);
    }

    class ToAdapter implements PointBehaviourListener, Serializable {
        private final MovePoint this$0;

        ToAdapter(MovePoint this$0) {
            this.this$0 = this$0;
        }

        public void behaviourUpdated(Point2D param1Point2D) {
            this.this$0.setTo(param1Point2D);
        }
    }

    public final PointBehaviourListener newToAdapter() {
        return new ToAdapter(this);
    }

    class DurationAdapter implements DoubleBehaviourListener, Serializable {
        private final MovePoint this$0;

        DurationAdapter(MovePoint this$0) {
            this.this$0 = this$0;
        }

        public void behaviourUpdated(double param1Double) {
            this.this$0.setDuration(param1Double);
        }
    }

    public final DoubleBehaviourListener newDurationAdapter() {
        return new DurationAdapter(this);
    }
}
