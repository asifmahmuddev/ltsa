package uk.ac.ic.doc.scenebeans;

import java.awt.geom.AffineTransform;

public interface Transform extends SceneGraph {
    SceneGraph getTransformedGraph();

    void setTransformedGraph(SceneGraph paramSceneGraph);

    AffineTransform getTransform();

    SceneGraph getLastDrawnTransformedGraph();

    AffineTransform getLastDrawnTransform();
}
