package uk.ac.ic.doc.scenebeans.behaviour;

import java.io.Serializable;
import uk.ac.ic.doc.scenebeans.DoubleBehaviourListener;

public class ToMove extends DoubleActivityBase implements Serializable {
    private double _from;
    private double _to;
    private double _duration;
    private double _timeout;

    public ToMove() {
        this._from = 0.0D;
        this._to = 0.0D;
        this._duration = this._timeout = 1.0D;
    }

    public ToMove(double paramDouble1, double paramDouble2, double paramDouble3) {
        this._to = paramDouble2;
        this._from = paramDouble1;
        this._duration = this._timeout = paramDouble3;
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
        this._duration = this._timeout = paramDouble;
    }

    public double getValue() {
        return this._from + (1.0D - this._timeout / this._duration) * (this._to - this._from);
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
                this._from = this._to;
                postActivityComplete();
            }
        }
        postUpdate(getValue());
    }

    class FromAdapter implements DoubleBehaviourListener, Serializable {
        private final ToMove this$0;

        FromAdapter(ToMove this$0) {
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
        private final ToMove this$0;

        ToAdapter(ToMove this$0) {
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
        private final ToMove this$0;

        DurationAdapter(ToMove this$0) {
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
