package uk.ac.ic.doc.scenebeans;

public interface CompositeNode extends SceneGraph {
    int getSubgraphCount();

    SceneGraph getSubgraph(int paramInt);

    int getVisibleSubgraphCount();

    SceneGraph getVisibleSubgraph(int paramInt);

    int getLastDrawnSubgraphCount();

    SceneGraph getLastDrawnSubgraph(int paramInt);

    void addSubgraph(SceneGraph paramSceneGraph);

    void removeSubgraph(SceneGraph paramSceneGraph);

    void removeSubgraph(int paramInt);
}
