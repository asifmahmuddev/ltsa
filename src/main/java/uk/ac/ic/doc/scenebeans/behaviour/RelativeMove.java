package uk.ac.ic.doc.scenebeans.behaviour;

import java.io.Serializable;
import uk.ac.ic.doc.scenebeans.DoubleBehaviourListener;

public class RelativeMove extends DoubleActivityBase implements Serializable {
    private double _from;
    private double _delta;
    private double _duration;
    private double _timeout;

    public RelativeMove() {
        this._from = 0.0D;
        this._delta = 0.0D;
        this._duration = this._timeout = 1.0D;
    }

    public RelativeMove(double paramDouble1, double paramDouble2, double paramDouble3) {
        this._delta = paramDouble2;
        this._from = paramDouble1;
        this._duration = this._timeout = paramDouble3;
    }

    public double getFrom() {
        return this._from;
    }

    public void setFrom(double paramDouble) {
        this._from = paramDouble;
    }

    public double getDelta() {
        return this._delta;
    }

    public void setDelta(double paramDouble) {
        this._delta = paramDouble;
    }

    public double getDuration() {
        return this._duration;
    }

    public void setDuration(double paramDouble) {
        this._duration = this._timeout = paramDouble;
    }

    public double getValue() {
        return this._from + (1.0D - this._timeout / this._duration) * this._delta;
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
                this._from += this._delta;
                this._delta = 0.0D;
                postActivityComplete();
            }
            postUpdate(getValue());
        }
    }

    class FromAdapter implements DoubleBehaviourListener, Serializable {
        private final RelativeMove this$0;

        FromAdapter(RelativeMove this$0) {
            this.this$0 = this$0;
        }

        public void behaviourUpdated(double param1Double) {
            this.this$0.setFrom(param1Double);
        }
    }

    public final DoubleBehaviourListener newFromAdapter() {
        return new FromAdapter(this);
    }

    class DurationAdapter implements DoubleBehaviourListener, Serializable {
        private final RelativeMove this$0;

        DurationAdapter(RelativeMove this$0) {
            this.this$0 = this$0;
        }

        public void behaviourUpdated(double param1Double) {
            this.this$0.setDuration(param1Double);
        }
    }

    public final DoubleBehaviourListener newDurationAdapter() {
        return new DurationAdapter(this);
    }

    class DeltaAdapter implements DoubleBehaviourListener, Serializable {
        private final RelativeMove this$0;

        DeltaAdapter(RelativeMove this$0) {
            this.this$0 = this$0;
        }

        public void behaviourUpdated(double param1Double) {
            this.this$0.setDelta(param1Double);
        }
    }

    public final DoubleBehaviourListener newDeltaAdapter() {
        return new DeltaAdapter(this);
    }
}
