package uk.ac.ic.doc.scenebeans.animation;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import uk.ac.ic.doc.scenebeans.SceneGraph;
import uk.ac.ic.doc.scenebeans.TransformBase;

class WindowTransform extends TransformBase {
    private double _width = 0.0D, _height = 0.0D;
    private double _child_width = 1.0D, _child_height = 1.0D;
    private double _translate_x = 0.0D, _translate_y = 0.0D;
    private double _scale_x = 1.0D;
    private double _scale_y = 1.0D;
    private boolean _is_centered = false;
    private boolean _is_stretched = false;
    private boolean _is_aspect_fixed = false;

    public boolean isCentered() {
        return this._is_centered;
    }

    public void setCentered(boolean paramBoolean) {
        this._is_centered = paramBoolean;
        updateTransform();
    }

    public boolean isStretched() {
        return this._is_stretched;
    }

    public void setStretched(boolean paramBoolean) {
        this._is_stretched = paramBoolean;
        updateTransform();
    }

    public boolean isAspectFixed() {
        return this._is_aspect_fixed;
    }

    public void setAspectFixed(boolean paramBoolean) {
        this._is_aspect_fixed = paramBoolean;
        updateTransform();
    }

    public AffineTransform getTransform() {
        AffineTransform affineTransform = AffineTransform.getTranslateInstance(this._translate_x, this._translate_y);
        affineTransform.scale(this._scale_x, this._scale_y);
        return affineTransform;
    }

    public void setWindowSize(double paramDouble1, double paramDouble2) {
        this._width = paramDouble1;
        this._height = paramDouble2;
        updateTransform();
    }

    public void updateTransform() {
        if (this._is_centered) {
            this._translate_x = this._width / 2.0D;
            this._translate_y = this._height / 2.0D;
        } else {
            this._translate_x = 0.0D;
            this._translate_y = 0.0D;
        }
        double d1 = this._width / this._child_width;
        double d2 = this._height / this._child_height;
        this._scale_x = this._scale_y = Math.min(d1, d2);
        this._scale_x = d1;
        this._scale_y = d2;
        this._scale_x = 1.0D;
        this._scale_y = 1.0D;
        setDirty(true);
    }

    public void setTransformedGraph(Animation paramAnimation) {
        this._child_width = paramAnimation.getWidth();
        this._child_height = paramAnimation.getHeight();
        setTransformedGraph((SceneGraph) paramAnimation);
        updateTransform();
    }

    protected void transform(Graphics2D paramGraphics2D) {
        paramGraphics2D.translate(this._translate_x, this._translate_y);
        paramGraphics2D.scale(this._scale_x, this._scale_y);
    }
}
