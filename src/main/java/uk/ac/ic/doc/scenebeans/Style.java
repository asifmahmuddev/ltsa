package uk.ac.ic.doc.scenebeans;

import java.awt.Graphics2D;

public interface Style extends SceneGraph {
    SceneGraph getStyledGraph();

    void setStyledGraph(SceneGraph paramSceneGraph);

    Change changeStyle(Graphics2D paramGraphics2D);

    Change getLastDrawnStyle();

    SceneGraph getLastDrawnStyledGraph();

    public static interface Change {
        void restoreStyle(Graphics2D param1Graphics2D);

        void reapplyStyle(Graphics2D param1Graphics2D);
    }
}
