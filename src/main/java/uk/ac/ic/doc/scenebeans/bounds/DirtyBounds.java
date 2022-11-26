package uk.ac.ic.doc.scenebeans.bounds;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import uk.ac.ic.doc.scenebeans.CompositeNode;
import uk.ac.ic.doc.scenebeans.Input;
import uk.ac.ic.doc.scenebeans.Primitive;
import uk.ac.ic.doc.scenebeans.SceneGraph;
import uk.ac.ic.doc.scenebeans.Style;
import uk.ac.ic.doc.scenebeans.Transform;

public class DirtyBounds extends Bounds {
    public static Rectangle2D getBounds(SceneGraph paramSceneGraph, Graphics2D paramGraphics2D) {
        DirtyBounds dirtyBounds = new DirtyBounds(paramGraphics2D);
        try {
            paramSceneGraph.accept(dirtyBounds);
        } catch (RuntimeException runtimeException) {
            throw runtimeException;
        } catch (Exception exception) {
            throw new RuntimeException(exception.getMessage());
        }
        return dirtyBounds.getBounds();
    }

    public DirtyBounds(Graphics2D paramGraphics2D) {
        super(paramGraphics2D);
    }

    public DirtyBounds(Graphics2D paramGraphics2D, AffineTransform paramAffineTransform) {
        super(paramGraphics2D, paramAffineTransform);
    }

    public void process(Primitive paramPrimitive) {
        if (paramPrimitive.isDirty())
            addBoundsOf((SceneGraph) paramPrimitive);
    }

    public void process(Transform paramTransform) {
        if (paramTransform.isDirty()) {
            addBoundsOf((SceneGraph) paramTransform);
        } else {
            super.process(paramTransform);
        }
    }

    public void process(Input paramInput) {
        if (paramInput.isDirty()) {
            addBoundsOf((SceneGraph) paramInput);
        } else {
            super.process(paramInput);
        }
    }

    public void process(Style paramStyle) {
        if (paramStyle.isDirty()) {
            addBoundsOf((SceneGraph) paramStyle);
        } else {
            super.process(paramStyle);
        }
    }

    public void process(CompositeNode paramCompositeNode) {
        if (paramCompositeNode.isDirty()) {
            addBoundsOf((SceneGraph) paramCompositeNode);
        } else {
            super.process(paramCompositeNode);
        }
    }

    private void addBoundsOf(SceneGraph paramSceneGraph) {
        addOldBoundsOf(paramSceneGraph);
        addNewBoundsOf(paramSceneGraph);
    }

    private void addOldBoundsOf(SceneGraph paramSceneGraph) {
        LastDrawnBounds lastDrawnBounds = new LastDrawnBounds(getGraphics(), getTransform());
        paramSceneGraph.accept(lastDrawnBounds);
        addBounds(lastDrawnBounds.getBounds());
    }

    private void addNewBoundsOf(SceneGraph paramSceneGraph) {
        Bounds bounds = new Bounds(getGraphics(), getTransform());
        paramSceneGraph.accept(bounds);
        addBounds(bounds.getBounds());
    }
}
