package uk.ac.ic.doc.scenebeans.behaviour;

import java.io.Serializable;
import uk.ac.ic.doc.scenebeans.DoubleBehaviourListener;

public class ConstantSpeedMove extends DoubleActivityBase implements Serializable {
    private double _from;
    private double _to;
    private double _speed;
    private double _timeout;

    public ConstantSpeedMove() {
        this._from = 0.0D;
        this._to = 0.0D;
        this._speed = 0.01D;
        this._timeout = 1.0D;
    }

    public ConstantSpeedMove(double paramDouble1, double paramDouble2, double paramDouble3) {
        this._to = paramDouble2;
        this._from = paramDouble1;
        this._speed = paramDouble3;
        this._timeout = duration();
    }

    public double getFrom() {
        return this._from;
    }

    public void setFrom(double paramDouble) {
        this._from = paramDouble;
        this._timeout = duration();
    }

    public double getTo() {
        return this._to;
    }

    public void setTo(double paramDouble) {
        this._to = paramDouble;
        this._timeout = duration();
    }

    public double getSpeed() {
        return this._speed;
    }

    public void setSpeed(double paramDouble) {
        this._speed = paramDouble;
        this._timeout = duration();
    }

    public double getValue() {
        return this._from + (1.0D - this._timeout / duration()) * (this._to - this._from);
    }

    public boolean isFinite() {
        return true;
    }

    public void reset() {
        this._from = getValue();
        this._timeout = duration();
        postUpdate(getValue());
    }

    public void performActivity(double paramDouble) {
        if (this._timeout > 0.0D) {
            this._timeout -= paramDouble;
            if (this._timeout <= 0.0D) {
                this._timeout = 0.0D;
                this._from = this._to;
                postActivityComplete();
            }
            postUpdate(getValue());
        }
    }

    private double duration() {
        return Math.max(this._speed * Math.abs(this._to - this._from), 0.001D);
    }

    class FromAdapter implements DoubleBehaviourListener, Serializable {
        private final ConstantSpeedMove this$0;

        FromAdapter(ConstantSpeedMove this$0) {
            this.this$0 = this$0;
        }

        public void behaviourUpdated(double param1Double) {
            this.this$0.setFrom(param1Double);
        }
    }

    public final DoubleBehaviourListener newFromAdapter() {
        return new FromAdapter(this);
    }

    class ToAdapter implements DoubleBehaviourListener, Serializable {
        private final ConstantSpeedMove this$0;

        ToAdapter(ConstantSpeedMove this$0) {
            this.this$0 = this$0;
        }

        public void behaviourUpdated(double param1Double) {
            this.this$0.setTo(param1Double);
        }
    }

    public final DoubleBehaviourListener newToAdapter() {
        return new ToAdapter(this);
    }

    class SpeedAdapter implements DoubleBehaviourListener, Serializable {
        private final ConstantSpeedMove this$0;

        SpeedAdapter(ConstantSpeedMove this$0) {
            this.this$0 = this$0;
        }

        public void behaviourUpdated(double param1Double) {
            this.this$0.setSpeed(param1Double);
        }
    }

    public final DoubleBehaviourListener newSpeedAdapter() {
        return new SpeedAdapter(this);
    }
}
