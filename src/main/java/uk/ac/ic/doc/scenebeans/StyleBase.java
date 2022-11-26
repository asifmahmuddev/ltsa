package uk.ac.ic.doc.scenebeans;

import java.awt.Graphics2D;

public abstract class StyleBase extends SceneGraphBase implements Style {
    private SceneGraph _child;
    private transient Style.Change _last_drawn_style = null;
    private SceneGraph _last_drawn_child = new Null();

    protected StyleBase() {
        this._child = new Null();
    }

    protected StyleBase(SceneGraph paramSceneGraph) {
        this._child = (paramSceneGraph == null) ? new Null() : paramSceneGraph;
    }

    public void accept(SceneGraphProcessor paramSceneGraphProcessor) {
        paramSceneGraphProcessor.process(this);
    }

    public SceneGraph getStyledGraph() {
        return this._child;
    }

    public void setStyledGraph(SceneGraph paramSceneGraph) {
        if (paramSceneGraph == null) {
            this._child = new Null();
        } else {
            this._child = paramSceneGraph;
        }
        setDirty(true);
    }

    public Style.Change getLastDrawnStyle() {
        return this._last_drawn_style;
    }

    public SceneGraph getLastDrawnStyledGraph() {
        return this._last_drawn_child;
    }

    public void draw(Graphics2D paramGraphics2D) {
        Style.Change change = changeStyle(paramGraphics2D);
        this._child.draw(paramGraphics2D);
        change.restoreStyle(paramGraphics2D);
        this._last_drawn_style = change;
        this._last_drawn_child = this._child;
        setDirty(false);
    }

    public abstract Style.Change changeStyle(Graphics2D paramGraphics2D);
}
