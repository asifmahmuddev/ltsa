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

public class Bounds implements SceneGraphProcessor {
    private Rectangle2D _bounds = null;
    private Graphics2D _graphics;
    private AffineTransform _transform;

    public static Rectangle2D getBounds(SceneGraph paramSceneGraph, Graphics2D paramGraphics2D) {
        Bounds bounds = new Bounds(paramGraphics2D);
        paramSceneGraph.accept(bounds);
        return bounds.getBounds();
    }

    public Bounds(Graphics2D paramGraphics2D) {
        this._graphics = paramGraphics2D;
        this._transform = new AffineTransform();
    }

    public Bounds(Graphics2D paramGraphics2D, AffineTransform paramAffineTransform) {
        this._graphics = paramGraphics2D;
        this._transform = new AffineTransform(paramAffineTransform);
    }

    public Rectangle2D getBounds() {
        return this._bounds;
    }

    public Graphics2D getGraphics() {
        return this._graphics;
    }

    public AffineTransform getTransform() {
        return new AffineTransform(this._transform);
    }

    public void process(Primitive paramPrimitive) {
        GeneralPath generalPath = new GeneralPath(paramPrimitive.getShape(this._graphics));
        generalPath.transform(this._transform);
        addBounds(generalPath.getBounds2D());
    }

    public void process(Transform paramTransform) {
        AffineTransform affineTransform = new AffineTransform(this._transform);
        this._transform.concatenate(paramTransform.getTransform());
        paramTransform.getTransformedGraph().accept(this);
        this._transform = affineTransform;
    }

    public void process(Input paramInput) {
        paramInput.getSensitiveGraph().accept(this);
    }

    public void process(Style paramStyle) {
        Style.Change change = paramStyle.changeStyle(this._graphics);
        paramStyle.getStyledGraph().accept(this);
        change.restoreStyle(this._graphics);
    }

    public void process(CompositeNode paramCompositeNode) {
        for (byte b = 0; b < paramCompositeNode.getVisibleSubgraphCount(); b++)
            paramCompositeNode.getVisibleSubgraph(b).accept(this);
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
