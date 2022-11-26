package uk.ac.ic.doc.scenebeans;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Point2D;
import java.io.Serializable;

public class Gradient extends StyleBase {
    private boolean _is_cyclic = false;
    private Point2D _from_pt;
    private Point2D _to_pt;
    private Color _from_col;
    private Color _to_col;

    public boolean isCyclic() {
        return this._is_cyclic;
    }

    public void setCyclic(boolean paramBoolean) {
        this._is_cyclic = true;
        setDirty(true);
    }

    public Point2D getFromPoint() {
        return this._from_pt;
    }

    public void setFromPoint(Point2D paramPoint2D) {
        this._from_pt = paramPoint2D;
        setDirty(true);
    }

    public Color getFromColor() {
        return this._from_col;
    }

    public void setFromColor(Color paramColor) {
        this._from_col = paramColor;
        setDirty(true);
    }

    public Point2D getToPoint() {
        return this._to_pt;
    }

    public void setToPoint(Point2D paramPoint2D) {
        this._to_pt = paramPoint2D;
        setDirty(true);
    }

    public Color getToColor() {
        return this._to_col;
    }

    public void setToColor(Color paramColor) {
        this._to_col = paramColor;
        setDirty(true);
    }

    public Style.Change changeStyle(Graphics2D paramGraphics2D) {
        Paint paint = paramGraphics2D.getPaint();
        GradientPaint gradientPaint = new GradientPaint(this._from_pt, this._from_col, this._to_pt, this._to_col, this._is_cyclic);
        paramGraphics2D.setPaint(gradientPaint);
        return new Style.Change(this, paint, gradientPaint) {
            private final Paint val$old_paint;
            private final Paint val$new_paint;
            private final Gradient this$0;

            public void restoreStyle(Graphics2D param1Graphics2D) {
                param1Graphics2D.setPaint(this.val$old_paint);
            }

            public void reapplyStyle(Graphics2D param1Graphics2D) {
                param1Graphics2D.setPaint(this.val$new_paint);
            }
        };
    }

    class FromPointAdapter implements PointBehaviourListener, Serializable {
        private final Gradient this$0;

        FromPointAdapter(Gradient this$0) {
            this.this$0 = this$0;
        }

        public void behaviourUpdated(Point2D param1Point2D) {
            this.this$0.setFromPoint(param1Point2D);
        }
    }

    public final FromPointAdapter newFromPointAdapter() {
        return new FromPointAdapter(this);
    }

    class ToPointAdapter implements PointBehaviourListener, Serializable {
        private final Gradient this$0;

        ToPointAdapter(Gradient this$0) {
            this.this$0 = this$0;
        }

        public void behaviourUpdated(Point2D param1Point2D) {
            this.this$0.setToPoint(param1Point2D);
        }
    }

    public final ToPointAdapter newToPointAdapter() {
        return new ToPointAdapter(this);
    }

    class FromColorAdapter implements ColorBehaviourListener, Serializable {
        private final Gradient this$0;

        FromColorAdapter(Gradient this$0) {
            this.this$0 = this$0;
        }

        public void behaviourUpdated(Color param1Color) {
            this.this$0.setFromColor(param1Color);
        }
    }

    public final FromColorAdapter newFromColorAdapter() {
        return new FromColorAdapter(this);
    }

    class ToColorAdapter implements ColorBehaviourListener, Serializable {
        private final Gradient this$0;

        ToColorAdapter(Gradient this$0) {
            this.this$0 = this$0;
        }

        public void behaviourUpdated(Color param1Color) {
            this.this$0.setToColor(param1Color);
        }
    }

    public final ToColorAdapter newToColorAdapter() {
        return new ToColorAdapter(this);
    }
}
