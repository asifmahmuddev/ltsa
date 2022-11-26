package uk.ac.ic.doc.scenebeans;

import java.awt.Graphics2D;

public abstract class SceneGraphBase implements SceneGraph {
    private transient boolean _is_dirty = true;

    public boolean isDirty() {
        return this._is_dirty;
    }

    public void setDirty(boolean paramBoolean) {
        this._is_dirty = paramBoolean;
    }

    public abstract void accept(SceneGraphProcessor paramSceneGraphProcessor);

    public abstract void draw(Graphics2D paramGraphics2D);
}
