package uk.ac.ic.doc.scenebeans;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.io.Serializable;

public class HSBAColor extends StyleBase {
    private float _h;
    private float _s;
    private float _b;
    private float _a;

    public HSBAColor() {
        this._h = this._s = this._b = 0.5F;
        this._a = 1.0F;
    }

    public HSBAColor(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, SceneGraph paramSceneGraph) {
        super(paramSceneGraph);
        this._h = (float) paramDouble1;
        this._s = (float) paramDouble2;
        this._b = (float) paramDouble3;
        this._a = (float) paramDouble4;
    }

    public HSBAColor(Color paramColor, SceneGraph paramSceneGraph) {
        super(paramSceneGraph);
        setColor(paramColor);
    }

    public Color getColor() {
        int i = Color.HSBtoRGB(this._h, this._s, this._b);
        int j = (int) (this._a * 255.0D) << 24;
        return new Color(i | j, true);
    }

    public void setColor(Color paramColor) {
        float[] arrayOfFloat = Color.RGBtoHSB(paramColor.getRed(), paramColor.getBlue(), paramColor.getGreen(), null);
        this._h = arrayOfFloat[0];
        this._s = arrayOfFloat[1];
        this._b = arrayOfFloat[2];
        this._a = paramColor.getAlpha() / 255.0F;
        setDirty(true);
    }

    public double getHue() {
        return this._h;
    }

    public void setHue(double paramDouble) {
        this._h = (float) paramDouble;
        setDirty(true);
    }

    public double getSaturation() {
        return this._s;
    }

    public void setSaturation(double paramDouble) {
        this._s = (float) paramDouble;
        setDirty(true);
    }

    public double getBrightness() {
        return this._b;
    }

    public void setBrightness(double paramDouble) {
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
            private final HSBAColor this$0;

            public void restoreStyle(Graphics2D param1Graphics2D) {
                param1Graphics2D.setPaint(this.val$old_paint);
            }

            public void reapplyStyle(Graphics2D param1Graphics2D) {
                param1Graphics2D.setPaint(this.val$new_paint);
            }
        };
    }

    class ColorAdapter implements ColorBehaviourListener, Serializable {
        private final HSBAColor this$0;

        ColorAdapter(HSBAColor this$0) {
            this.this$0 = this$0;
        }

        public void behaviourUpdated(Color param1Color) {
            this.this$0.setColor(param1Color);
        }
    }

    public final ColorAdapter newColorAdapter() {
        return new ColorAdapter(this);
    }

    class HueAdapter implements DoubleBehaviourListener, Serializable {
        private final HSBAColor this$0;

        HueAdapter(HSBAColor this$0) {
            this.this$0 = this$0;
        }

        public void behaviourUpdated(double param1Double) {
            this.this$0.setHue(param1Double);
        }
    }

    public final HueAdapter newHueAdapter() {
        return new HueAdapter(this);
    }

    class SaturationAdapter implements DoubleBehaviourListener, Serializable {
        private final HSBAColor this$0;

        SaturationAdapter(HSBAColor this$0) {
            this.this$0 = this$0;
        }

        public void behaviourUpdated(double param1Double) {
            this.this$0.setSaturation(param1Double);
        }
    }

    public final SaturationAdapter newSaturationAdapter() {
        return new SaturationAdapter(this);
    }

    class BrightnessAdapter implements DoubleBehaviourListener, Serializable {
        private final HSBAColor this$0;

        BrightnessAdapter(HSBAColor this$0) {
            this.this$0 = this$0;
        }

        public void behaviourUpdated(double param1Double) {
            this.this$0.setBrightness(param1Double);
        }
    }

    public final BrightnessAdapter newBrightnessAdapter() {
        return new BrightnessAdapter(this);
    }

    class AlphaAdapter implements DoubleBehaviourListener, Serializable {
        private final HSBAColor this$0;

        AlphaAdapter(HSBAColor this$0) {
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
