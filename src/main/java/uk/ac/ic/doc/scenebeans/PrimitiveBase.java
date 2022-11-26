package uk.ac.ic.doc.scenebeans;

import java.awt.Graphics2D;
import java.awt.Shape;

public abstract class PrimitiveBase extends SceneGraphBase implements Primitive {
    private boolean _is_filled = true;
    private transient Shape _last_drawn = null;

    public boolean isFilled() {
        return this._is_filled;
    }

    public void setFilled(boolean paramBoolean) {
        this._is_filled = paramBoolean;
        setDirty(true);
    }

    public void draw(Graphics2D paramGraphics2D) {
        Shape shape = getShape(paramGraphics2D);
        if (this._is_filled) {
            paramGraphics2D.fill(shape);
        } else {
            paramGraphics2D.draw(shape);
        }
        this._last_drawn = shape;
        setDirty(false);
    }

    public void accept(SceneGraphProcessor paramSceneGraphProcessor) {
        paramSceneGraphProcessor.process(this);
    }

    public Shape getLastDrawnShape() {
        return this._last_drawn;
    }

    public abstract Shape getShape(Graphics2D paramGraphics2D);
}
