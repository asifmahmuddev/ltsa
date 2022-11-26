package uk.ac.ic.doc.scenebeans.cag;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import uk.ac.ic.doc.scenebeans.CompositeNode;
import uk.ac.ic.doc.scenebeans.Input;
import uk.ac.ic.doc.scenebeans.Primitive;
import uk.ac.ic.doc.scenebeans.SceneGraphProcessor;
import uk.ac.ic.doc.scenebeans.Style;
import uk.ac.ic.doc.scenebeans.Transform;

public abstract class CAGProcessor implements SceneGraphProcessor {
    private Graphics2D _graphics;
    private AffineTransform _transform;
    private Area _area = null;

    protected CAGProcessor(Graphics2D paramGraphics2D) {
        this._graphics = paramGraphics2D;
        this._transform = new AffineTransform();
    }

    protected CAGProcessor(Graphics2D paramGraphics2D, AffineTransform paramAffineTransform) {
        this._graphics = paramGraphics2D;
        this._transform = new AffineTransform(paramAffineTransform);
    }

    public Area getArea() {
        return (this._area == null) ? new Area() : this._area;
    }

    public void process(Primitive paramPrimitive) {
        Area area = new Area(paramPrimitive.getShape(this._graphics));
        area.transform(this._transform);
        if (this._area == null) {
            this._area = area;
        } else {
            accumulateArea(this._area, area);
        }
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
        paramStyle.getStyledGraph().accept(this);
    }

    public void process(CompositeNode paramCompositeNode) {
        for (byte b = 0; b < paramCompositeNode.getVisibleSubgraphCount(); b++)
            paramCompositeNode.getVisibleSubgraph(b).accept(this);
    }

    protected abstract void accumulateArea(Area paramArea1, Area paramArea2);
}
