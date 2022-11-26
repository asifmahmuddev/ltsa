package uk.ac.ic.doc.scenebeans.behaviour;

import java.io.Serializable;
import uk.ac.ic.doc.scenebeans.DoubleBehaviourListener;
import uk.ac.ic.doc.scenebeans.activity.FiniteActivityBase;

public class RandomTimer extends FiniteActivityBase implements Serializable {
    private double _min_duration = this._max_duration = this._timeout = 1.0D;
    private double _max_duration;
    private double _timeout;

    public double getMinDuration() {
        return this._min_duration;
    }

    public void setMinDuration(double paramDouble) {
        this._min_duration = paramDouble;
    }

    public double getMaxDuration() {
        return this._max_duration;
    }

    public void setMaxDuration(double paramDouble) {
        this._max_duration = paramDouble;
    }

    public boolean isFinite() {
        return true;
    }

    public void reset() {
        double d = this._max_duration - this._min_duration;
        this._timeout = this._min_duration + d * Math.random();
    }

    public void performActivity(double paramDouble) {
        if (this._timeout > 0.0D) {
            this._timeout -= paramDouble;
            if (this._timeout <= 0.0D) {
                this._timeout = 0.0D;
                postActivityComplete();
            }
        }
    }

    class MinDuration implements DoubleBehaviourListener {
        private final RandomTimer this$0;

        MinDuration(RandomTimer this$0) {
            this.this$0 = this$0;
        }

        public void behaviourUpdated(double param1Double) {
            this.this$0.setMinDuration(param1Double);
        }
    }

    public MinDuration newMinDurationAdapter() {
        return new MinDuration(this);
    }

    class MaxDuration implements DoubleBehaviourListener {
        private final RandomTimer this$0;

        MaxDuration(RandomTimer this$0) {
            this.this$0 = this$0;
        }

        public void behaviourUpdated(double param1Double) {
            this.this$0.setMaxDuration(param1Double);
        }
    }

    public MaxDuration newMaxDurationAdapter() {
        return new MaxDuration(this);
    }
}
