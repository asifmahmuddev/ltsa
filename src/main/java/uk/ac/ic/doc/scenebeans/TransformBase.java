package uk.ac.ic.doc.scenebeans;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

public abstract class TransformBase extends SceneGraphBase implements Transform {
    private SceneGraph _child;
    private transient SceneGraph _last_drawn_child = new Null();
    private transient AffineTransform _last_drawn_transform = null;

    protected TransformBase() {
        this._child = new Null();
    }

    protected TransformBase(SceneGraph paramSceneGraph) {
        this._child = (paramSceneGraph == null) ? new Null() : paramSceneGraph;
    }

    public SceneGraph getTransformedGraph() {
        return this._child;
    }

    public void setTransformedGraph(SceneGraph paramSceneGraph) {
        this._child = (paramSceneGraph == null) ? new Null() : paramSceneGraph;
        setDirty(true);
    }

    public SceneGraph getLastDrawnTransformedGraph() {
        return this._last_drawn_child;
    }

    public AffineTransform getLastDrawnTransform() {
        return this._last_drawn_transform;
    }

    public void accept(SceneGraphProcessor paramSceneGraphProcessor) {
        paramSceneGraphProcessor.process(this);
    }

    public void draw(Graphics2D paramGraphics2D) {
        AffineTransform affineTransform1 = paramGraphics2D.getTransform();
        AffineTransform affineTransform2 = getTransform();
        paramGraphics2D.transform(affineTransform2);
        this._child.draw(paramGraphics2D);
        paramGraphics2D.setTransform(affineTransform1);
        this._last_drawn_child = this._child;
        this._last_drawn_transform = affineTransform2;
        setDirty(false);
    }

    public abstract AffineTransform getTransform();
}
