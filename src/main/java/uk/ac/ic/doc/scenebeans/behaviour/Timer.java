package uk.ac.ic.doc.scenebeans.behaviour;

import java.io.Serializable;
import uk.ac.ic.doc.scenebeans.DoubleBehaviourListener;
import uk.ac.ic.doc.scenebeans.activity.FiniteActivityBase;

public class Timer extends FiniteActivityBase implements Serializable {
    private double _duration;
    private double _timeout;

    public Timer() {
        this._duration = this._timeout = 1.0D;
    }

    public Timer(double paramDouble1, double paramDouble2, double paramDouble3) {
        this._duration = this._timeout = paramDouble3;
    }

    public double getDuration() {
        return this._duration;
    }

    public void setDuration(double paramDouble) {
        this._duration = this._timeout = paramDouble;
    }

    public boolean isFinite() {
        return true;
    }

    public void reset() {
        this._timeout = this._duration;
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

    class DurationAdapter implements DoubleBehaviourListener, Serializable {
        private final Timer this$0;

        DurationAdapter(Timer this$0) {
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
