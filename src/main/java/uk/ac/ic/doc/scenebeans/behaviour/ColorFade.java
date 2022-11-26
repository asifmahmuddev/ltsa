package uk.ac.ic.doc.scenebeans.behaviour;

import java.awt.Color;
import java.io.Serializable;
import uk.ac.ic.doc.scenebeans.ColorBehaviourListener;
import uk.ac.ic.doc.scenebeans.DoubleBehaviourListener;

public class ColorFade extends ColorActivityBase {
    private float _from_r;
    private float _from_g;
    private float _from_b;
    private float _from_a;
    private float _to_r;
    private float _to_g;
    private float _to_b;
    private float _to_a;
    private double _duration;
    private double _timeout;

    public ColorFade() {
        this(Color.black, Color.white, 1.0D);
    }

    public ColorFade(Color paramColor1, Color paramColor2, double paramDouble) {
        setFrom(paramColor1);
        setTo(paramColor2);
        this._duration = paramDouble;
        this._timeout = 0.0D;
    }

    public Color getFrom() {
        return new Color(this._from_r, this._from_g, this._from_b, this._from_a);
    }

    public void setFrom(Color paramColor) {
        this._from_r = (float) (paramColor.getRed() / 255.0D);
        this._from_g = (float) (paramColor.getGreen() / 255.0D);
        this._from_b = (float) (paramColor.getBlue() / 255.0D);
        this._from_a = (float) (paramColor.getAlpha() / 255.0D);
    }

    public Color getTo() {
        return new Color(this._to_r, this._to_g, this._to_b, this._to_a);
    }

    public void setTo(Color paramColor) {
        this._to_r = (float) (paramColor.getRed() / 255.0D);
        this._to_g = (float) (paramColor.getGreen() / 255.0D);
        this._to_b = (float) (paramColor.getBlue() / 255.0D);
        this._to_a = (float) (paramColor.getAlpha() / 255.0D);
    }

    public double getDuration() {
        return this._duration;
    }

    public void setDuration(double paramDouble) {
        this._duration = paramDouble;
    }

    public Color getValue() {
        return new Color(current(this._from_r, this._to_r), current(this._from_g, this._to_g), current(this._from_b, this._to_g), current(this._from_a, this._to_a));
    }

    public boolean isFinite() {
        return true;
    }

    public void reset() {
        this._timeout = 0.0D;
        postUpdate(getValue());
    }

    public void performActivity(double paramDouble) {
        this._timeout += paramDouble;
        if (this._timeout >= this._duration) {
            this._timeout = this._duration;
            postActivityComplete();
        } else {
            postUpdate(getValue());
        }
    }

    private final double ratio() {
        return this._timeout / this._duration;
    }

    private final float current(float paramFloat1, float paramFloat2) {
        return (float) (paramFloat1 + ratio() * (paramFloat2 - paramFloat1));
    }

    class FromAdapter implements ColorBehaviourListener, Serializable {
        private final ColorFade this$0;

        FromAdapter(ColorFade this$0) {
            this.this$0 = this$0;
        }

        public void behaviourUpdated(Color param1Color) {
            this.this$0.setFrom(param1Color);
        }
    }

    public final ColorBehaviourListener newFromAdapter() {
        return new FromAdapter(this);
    }

    class ToAdapter implements ColorBehaviourListener, Serializable {
        private final ColorFade this$0;

        ToAdapter(ColorFade this$0) {
            this.this$0 = this$0;
        }

        public void behaviourUpdated(Color param1Color) {
            this.this$0.setTo(param1Color);
        }
    }

    public final ColorBehaviourListener newToAdapter() {
        return new ToAdapter(this);
    }

    class DurationAdapter implements DoubleBehaviourListener, Serializable {
        private final ColorFade this$0;

        DurationAdapter(ColorFade this$0) {
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
