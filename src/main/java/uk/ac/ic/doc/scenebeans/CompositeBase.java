package uk.ac.ic.doc.scenebeans;

import java.awt.Graphics2D;
import java.util.ArrayList;

public abstract class CompositeBase extends SceneGraphBase implements CompositeNode {
    private ArrayList _nodes = new ArrayList();
    private transient ArrayList _last_drawn_nodes = new ArrayList();

    public int getSubgraphCount() {
        return this._nodes.size();
    }

    public SceneGraph getSubgraph(int paramInt) {
        return this._nodes.get(paramInt);
    }

    public int getVisibleSubgraphCount() {
        return getSubgraphCount();
    }

    public SceneGraph getVisibleSubgraph(int paramInt) {
        return getSubgraph(paramInt);
    }

    public int getLastDrawnSubgraphCount() {
        return this._last_drawn_nodes.size();
    }

    public SceneGraph getLastDrawnSubgraph(int paramInt) {
        return this._last_drawn_nodes.get(paramInt);
    }

    public void accept(SceneGraphProcessor paramSceneGraphProcessor) {
        paramSceneGraphProcessor.process(this);
    }

    public void addSubgraph(SceneGraph paramSceneGraph) {
        this._nodes.add(paramSceneGraph);
        setDirty(true);
    }

    public void removeSubgraph(SceneGraph paramSceneGraph) {
        this._nodes.remove(paramSceneGraph);
        setDirty(true);
    }

    public void removeSubgraph(int paramInt) {
        this._nodes.remove(paramInt);
        setDirty(true);
    }

    public void draw(Graphics2D paramGraphics2D) {
        this._last_drawn_nodes.clear();
        for (int i = getVisibleSubgraphCount() - 1; i >= 0; i--) {
            SceneGraph sceneGraph = getVisibleSubgraph(i);
            sceneGraph.draw(paramGraphics2D);
            this._last_drawn_nodes.add(sceneGraph);
        }
        setDirty(false);
    }
}
