package uk.ac.ic.doc.scenebeans.behaviour;

import java.io.Serializable;
import uk.ac.ic.doc.scenebeans.DoubleBehaviourListener;

public class Bounce extends DoubleActivityBase implements Serializable {
    private double _from;
    private double _to;
    private double _duration;
    private double _timeout;
    private boolean _is_increasing;

    public Bounce() {
        this._from = this._to = this._duration = this._timeout = 0.0D;
        this._is_increasing = true;
    }

    public Bounce(double paramDouble1, double paramDouble2, double paramDouble3) {
        this._from = paramDouble1;
        this._to = paramDouble2;
        this._duration = paramDouble3;
        this._timeout = 0.0D;
        this._is_increasing = true;
    }

    public double getFrom() {
        return this._from;
    }

    public void setFrom(double paramDouble) {
        this._from = paramDouble;
    }

    public double getTo() {
        return this._to;
    }

    public void setTo(double paramDouble) {
        this._to = paramDouble;
    }

    public double getDuration() {
        return this._duration;
    }

    public void setDuration(double paramDouble) {
        this._duration = paramDouble;
    }

    public double getValue() {
        return this._from + ratio() * (this._to - this._from);
    }

    public boolean isFinite() {
        return false;
    }

    public void reset() {
        this._timeout = 0.0D;
        postUpdate(getValue());
    }

    public void performActivity(double paramDouble) {
        this._timeout += paramDouble;
        while (this._timeout >= this._duration) {
            this._timeout -= this._duration;
            this._is_increasing = !this._is_increasing;
            if (this._is_increasing)
                postActivityComplete();
        }
        postUpdate(getValue());
    }

    private final double ratio() {
        if (this._is_increasing)
            return this._timeout / this._duration;
        return 1.0D - this._timeout / this._duration;
    }

    class FromAdapter implements DoubleBehaviourListener, Serializable {
        private final Bounce this$0;

        FromAdapter(Bounce this$0) {
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
        private final Bounce this$0;

        ToAdapter(Bounce this$0) {
            this.this$0 = this$0;
        }

        public void behaviourUpdated(double param1Double) {
            this.this$0.setTo(param1Double);
        }
    }

    public final DoubleBehaviourListener newToAdapter() {
        return new ToAdapter(this);
    }

    class DurationAdapter implements DoubleBehaviourListener, Serializable {
        private final Bounce this$0;

        DurationAdapter(Bounce this$0) {
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
