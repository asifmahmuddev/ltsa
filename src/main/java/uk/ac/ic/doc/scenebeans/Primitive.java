package uk.ac.ic.doc.scenebeans;

import java.awt.Graphics2D;
import java.awt.Shape;

public interface Primitive extends SceneGraph {
    Shape getShape(Graphics2D paramGraphics2D);

    Shape getLastDrawnShape();
}
