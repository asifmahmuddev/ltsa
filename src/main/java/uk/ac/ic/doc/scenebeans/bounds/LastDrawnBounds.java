package uk.ac.ic.doc.scenebeans.bounds;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import uk.ac.ic.doc.scenebeans.CompositeNode;
import uk.ac.ic.doc.scenebeans.Input;
import uk.ac.ic.doc.scenebeans.Primitive;
import uk.ac.ic.doc.scenebeans.SceneGraph;
import uk.ac.ic.doc.scenebeans.SceneGraphProcessor;
import uk.ac.ic.doc.scenebeans.Style;
import uk.ac.ic.doc.scenebeans.Transform;

public class LastDrawnBounds implements SceneGraphProcessor {
    private Rectangle2D _bounds = null;
    private Graphics2D _graphics;
    private AffineTransform _transform;

    public static Rectangle2D getBounds(SceneGraph paramSceneGraph, Graphics2D paramGraphics2D) {
        LastDrawnBounds lastDrawnBounds = new LastDrawnBounds(paramGraphics2D);
        try {
            paramSceneGraph.accept(lastDrawnBounds);
        } catch (RuntimeException runtimeException) {
            throw runtimeException;
        } catch (Exception exception) {
            throw new RuntimeException(exception.getMessage());
        }
        return lastDrawnBounds.getBounds();
    }

    public LastDrawnBounds(Graphics2D paramGraphics2D) {
        this._graphics = paramGraphics2D;
        this._transform = new AffineTransform();
    }

    public LastDrawnBounds(Graphics2D paramGraphics2D, AffineTransform paramAffineTransform) {
        this._graphics = paramGraphics2D;
        this._transform = new AffineTransform(paramAffineTransform);
    }

    public Rectangle2D getBounds() {
        return this._bounds;
    }

    public void process(Primitive paramPrimitive) {
        GeneralPath generalPath = new GeneralPath(paramPrimitive.getLastDrawnShape());
        generalPath.transform(this._transform);
        addBounds(generalPath.getBounds2D());
    }

    public void process(Transform paramTransform) {
        AffineTransform affineTransform = new AffineTransform(this._transform);
        this._transform.concatenate(paramTransform.getLastDrawnTransform());
        paramTransform.getLastDrawnTransformedGraph().accept(this);
        this._transform = affineTransform;
    }

    public void process(Input paramInput) {
        paramInput.getSensitiveGraph().accept(this);
    }

    public void process(Style paramStyle) {
        Style.Change change = paramStyle.getLastDrawnStyle();
        change.reapplyStyle(this._graphics);
        paramStyle.getLastDrawnStyledGraph().accept(this);
        change.restoreStyle(this._graphics);
    }

    public void process(CompositeNode paramCompositeNode) {
        for (byte b = 0; b < paramCompositeNode.getLastDrawnSubgraphCount(); b++)
            paramCompositeNode.getLastDrawnSubgraph(b).accept(this);
    }

    protected void addBounds(Rectangle2D paramRectangle2D) {
        if (paramRectangle2D != null)
            if (this._bounds == null) {
                this._bounds = paramRectangle2D;
            } else {
                this._bounds.add(paramRectangle2D);
            }
    }
}
