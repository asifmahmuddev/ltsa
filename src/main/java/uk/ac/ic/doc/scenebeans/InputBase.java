package uk.ac.ic.doc.scenebeans;

import java.awt.Graphics2D;

public abstract class InputBase extends SceneGraphBase implements Input {
    private SceneGraph _sensitive;

    protected InputBase() {
        this._sensitive = new Null();
    }

    protected InputBase(SceneGraph paramSceneGraph) {
        this._sensitive = paramSceneGraph;
    }

    public SceneGraph getSensitiveGraph() {
        return this._sensitive;
    }

    public void setSensitiveGraph(SceneGraph paramSceneGraph) {
        if (paramSceneGraph == null) {
            this._sensitive = new Null();
        } else {
            this._sensitive = paramSceneGraph;
        }
    }

    public void draw(Graphics2D paramGraphics2D) {
        this._sensitive.draw(paramGraphics2D);
        setDirty(false);
    }

    public void accept(SceneGraphProcessor paramSceneGraphProcessor) {
        paramSceneGraphProcessor.process(this);
    }
}
