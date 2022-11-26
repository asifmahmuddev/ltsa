package uk.ac.ic.doc.scenebeans;

import java.awt.Graphics2D;
import java.io.Serializable;

public interface SceneGraph extends Serializable {
    boolean isDirty();

    void setDirty(boolean paramBoolean);

    void draw(Graphics2D paramGraphics2D);

    void accept(SceneGraphProcessor paramSceneGraphProcessor);
}
