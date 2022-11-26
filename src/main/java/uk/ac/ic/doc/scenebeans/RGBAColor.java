package uk.ac.ic.doc.scenebeans;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.io.Serializable;

public class RGBAColor extends StyleBase {
    private float _r;
    private float _g;
    private float _b;
    private float _a;

    public RGBAColor() {
        this._r = this._g = this._b = 0.5F;
        this._a = 1.0F;
    }

    public RGBAColor(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, SceneGraph paramSceneGraph) {
        super(paramSceneGraph);
        this._r = (float) paramDouble1;
        this._g = (float) paramDouble2;
        this._b = (float) paramDouble3;
        this._a = (float) paramDouble4;
    }

    public RGBAColor(Color paramColor, SceneGraph paramSceneGraph) {
        super(paramSceneGraph);
        setColor(paramColor);
    }

    public Color getColor() {
        return new Color(this._r, this._g, this._b, this._a);
    }

    public void setColor(Color paramColor) {
        this._r = (float) (paramColor.getRed() / 255.0D);
        this._g = (float) (paramColor.getGreen() / 255.0D);
        this._b = (float) (paramColor.getBlue() / 255.0D);
        this._a = (float) (paramColor.getAlpha() / 255.0D);
        setDirty(true);
    }

    public double getRed() {
        return this._r;
    }

    public void setRed(double paramDouble) {
        this._r = (float) paramDouble;
        setDirty(true);
    }

    public double getGreen() {
        return this._g;
    }

    public void setGreen(double paramDouble) {
        this._g = (float) paramDouble;
        setDirty(true);
    }

    public double getBlue() {
        return this._b;
    }

    public void setBlue(double paramDouble) {
        this._b = (float) paramDouble;
        setDirty(true);
    }

    public double getAlpha() {
        return this._a;
    }

    public void setAlpha(double paramDouble) {
        this._a = (float) paramDouble;
        setDirty(true);
    }

    public Style.Change changeStyle(Graphics2D paramGraphics2D) {
        Paint paint = paramGraphics2D.getPaint();
        Color color = getColor();
        paramGraphics2D.setPaint(getColor());
        return new Style.Change(this, paint, color) {
            private final Paint val$old_paint;
            private final Paint val$new_paint;
            private final RGBAColor this$0;

            public void restoreStyle(Graphics2D param1Graphics2D) {
                param1Graphics2D.setPaint(this.val$old_paint);
            }

            public void reapplyStyle(Graphics2D param1Graphics2D) {
                param1Graphics2D.setPaint(this.val$new_paint);
            }
        };
    }

    public class ColorAdapter implements ColorBehaviourListener, Serializable {
        private final RGBAColor this$0;

        public ColorAdapter(RGBAColor this$0) {
            this.this$0 = this$0;
        }

        public void behaviourUpdated(Color param1Color) {
            this.this$0.setColor(param1Color);
        }
    }

    public final ColorAdapter newColorAdapter() {
        return new ColorAdapter(this);
    }

    public class RedAdapter implements DoubleBehaviourListener, Serializable {
        private final RGBAColor this$0;

        public RedAdapter(RGBAColor this$0) {
            this.this$0 = this$0;
        }

        public void behaviourUpdated(double param1Double) {
            this.this$0.setRed(param1Double);
        }
    }

    public final RedAdapter newRedAdapter() {
        return new RedAdapter(this);
    }

    public class GreenAdapter implements DoubleBehaviourListener, Serializable {
        private final RGBAColor this$0;

        public GreenAdapter(RGBAColor this$0) {
            this.this$0 = this$0;
        }

        public void behaviourUpdated(double param1Double) {
            this.this$0.setGreen(param1Double);
        }
    }

    public final GreenAdapter newGreenAdapter() {
        return new GreenAdapter(this);
    }

    public class BlueAdapter implements DoubleBehaviourListener, Serializable {
        private final RGBAColor this$0;

        public BlueAdapter(RGBAColor this$0) {
            this.this$0 = this$0;
        }

        public void behaviourUpdated(double param1Double) {
            this.this$0.setBlue(param1Double);
        }
    }

    public final BlueAdapter newBlueAdapter() {
        return new BlueAdapter(this);
    }

    public class AlphaAdapter implements DoubleBehaviourListener, Serializable {
        private final RGBAColor this$0;

        public AlphaAdapter(RGBAColor this$0) {
            this.this$0 = this$0;
        }

        public void behaviourUpdated(double param1Double) {
            this.this$0.setAlpha(param1Double);
        }
    }

    public final AlphaAdapter newAlphaAdapter() {
        return new AlphaAdapter(this);
    }
}
