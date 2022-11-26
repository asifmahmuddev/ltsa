package uk.ac.ic.doc.scenebeans.pick;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;
import uk.ac.ic.doc.scenebeans.CompositeNode;
import uk.ac.ic.doc.scenebeans.Input;
import uk.ac.ic.doc.scenebeans.Primitive;
import uk.ac.ic.doc.scenebeans.SceneGraph;
import uk.ac.ic.doc.scenebeans.SceneGraphProcessor;
import uk.ac.ic.doc.scenebeans.Style;
import uk.ac.ic.doc.scenebeans.Transform;

public class Picker implements SceneGraphProcessor {
    private Graphics2D _gfx_context;
    private Point2D _point;
    private LinkedList _path = new LinkedList();
    private boolean _pick_successful = false;

    private class PickFailure extends RuntimeException {
        NoninvertibleTransformException cause;
        private final Picker this$0;

        PickFailure(Picker this$0, NoninvertibleTransformException param1NoninvertibleTransformException) {
            this.this$0 = this$0;
            this.cause = param1NoninvertibleTransformException;
        }
    }

    public static List pick(Graphics2D paramGraphics2D, SceneGraph paramSceneGraph, double paramDouble1, double paramDouble2) throws NoninvertibleTransformException {
        return pick(paramGraphics2D, paramSceneGraph, new Point2D.Double(paramDouble1, paramDouble2));
    }

    public static List pick(Graphics2D paramGraphics2D, SceneGraph paramSceneGraph, Point2D paramPoint2D) throws NoninvertibleTransformException {
        try {
            Picker picker = new Picker(paramGraphics2D, paramPoint2D);
            paramSceneGraph.accept(picker);
            return picker.getPath();
        } catch (PickFailure pickFailure) {
            throw pickFailure.cause;
        }
    }

    private Picker(Graphics2D paramGraphics2D, Point2D paramPoint2D) {
        this._gfx_context = paramGraphics2D;
        this._point = paramPoint2D;
    }

    List getPath() {
        return this._path;
    }

    public void process(Primitive paramPrimitive) {
        Shape shape = paramPrimitive.getShape(this._gfx_context);
        if (shape.contains(this._point)) {
            this._path.addFirst(paramPrimitive);
            this._pick_successful = true;
        }
    }

    public void process(CompositeNode paramCompositeNode) {
        for (byte b = 0; b < paramCompositeNode.getVisibleSubgraphCount(); b++) {
            SceneGraph sceneGraph = paramCompositeNode.getVisibleSubgraph(b);
            sceneGraph.accept(this);
            if (this._pick_successful) {
                this._path.addFirst(sceneGraph);
                return;
            }
        }
    }

    public void process(Transform paramTransform) {
        Point2D point2D = this._point;
        try {
            this._point = paramTransform.getTransform().inverseTransform(this._point, null);
        } catch (NoninvertibleTransformException noninvertibleTransformException) {
            throw new PickFailure(this, noninvertibleTransformException);
        }
        SceneGraph sceneGraph = paramTransform.getTransformedGraph();
        sceneGraph.accept(this);
        this._point = point2D;
        if (this._pick_successful) {
            this._path.addFirst(sceneGraph);
            return;
        }
    }

    public void process(Style paramStyle) {
        Style.Change change = paramStyle.changeStyle(this._gfx_context);
        SceneGraph sceneGraph = paramStyle.getStyledGraph();
        sceneGraph.accept(this);
        change.restoreStyle(this._gfx_context);
        if (this._pick_successful) {
            this._path.addFirst(sceneGraph);
            return;
        }
    }

    public void process(Input paramInput) {
        SceneGraph sceneGraph = paramInput.getSensitiveGraph();
        sceneGraph.accept(this);
        if (this._pick_successful) {
            this._path.addFirst(sceneGraph);
            return;
        }
    }
}
