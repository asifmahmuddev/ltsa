package uk.ac.ic.doc.scenebeans;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import uk.ac.ic.doc.scenebeans.behaviour.DoubleBehaviourBase;
import uk.ac.ic.doc.scenebeans.behaviour.PointBehaviourBase;

public class MouseMotion extends InputBase {
    private boolean _is_active = true;
    private boolean _is_dragged = true;
    private PositionFacet _pos = new PositionFacet();
    private DoubleFacet _x = new DoubleFacet();
    private DoubleFacet _y = new DoubleFacet();
    private DoubleFacet _angle = new DoubleFacet();

    public boolean isActive() {
        return this._is_active;
    }

    public void setActive(boolean paramBoolean) {
        this._is_active = paramBoolean;
    }

    public boolean isDragged() {
        return this._is_dragged;
    }

    public void setDragged(boolean paramBoolean) {
        this._is_dragged = paramBoolean;
    }

    public PointBehaviour getPositionFacet() {
        return (PointBehaviour) this._pos;
    }

    public DoubleBehaviour getxFacet() {
        return (DoubleBehaviour) this._x;
    }

    public DoubleBehaviour getyFacet() {
        return (DoubleBehaviour) this._y;
    }

    public DoubleBehaviour getAngleFacet() {
        return (DoubleBehaviour) this._angle;
    }

    public void updatePosition(double paramDouble1, double paramDouble2) {
        double d = Math.atan(paramDouble2 / paramDouble1);
        if (paramDouble1 >= 0.0D) {
            d = Math.atan(paramDouble2 / paramDouble1) - 1.5707963267948966D;
        } else {
            d = Math.atan(paramDouble2 / paramDouble1) + 1.5707963267948966D;
        }
        this._pos.postUpdate(new Point2D.Double(paramDouble1, paramDouble2));
        this._x.postUpdate(paramDouble1);
        this._y.postUpdate(paramDouble2);
        this._angle.postUpdate(d);
    }

    public static void mouseMoved(SceneGraph paramSceneGraph, double paramDouble1, double paramDouble2) throws NoninvertibleTransformException {
        Processor processor = new Processor(paramDouble1, paramDouble2, false);
        dispatchMouseMotion(paramSceneGraph, processor);
    }

    public static void mouseDragged(SceneGraph paramSceneGraph, double paramDouble1, double paramDouble2) throws NoninvertibleTransformException {
        Processor processor = new Processor(paramDouble1, paramDouble2, true);
        dispatchMouseMotion(paramSceneGraph, processor);
    }

    private static void dispatchMouseMotion(SceneGraph paramSceneGraph, Processor paramProcessor) throws NoninvertibleTransformException {
        try {
            paramSceneGraph.accept(paramProcessor);
        } catch (TransformFailure transformFailure) {
            throw transformFailure.cause;
        }
    }

    private static class PositionFacet extends PointBehaviourBase {
        private PositionFacet() {
        }

        public void postUpdate(Point2D param1Point2D) {
            super.postUpdate(param1Point2D);
        }
    }

    private static class DoubleFacet extends DoubleBehaviourBase {
        private DoubleFacet() {
        }

        public void postUpdate(double param1Double) {
            super.postUpdate(param1Double);
        }
    }

    private static class TransformFailure extends RuntimeException {
        NoninvertibleTransformException cause;

        TransformFailure(NoninvertibleTransformException param1NoninvertibleTransformException) {
            this.cause = param1NoninvertibleTransformException;
        }
    }

    private static class Processor implements SceneGraphProcessor {
        private AffineTransform _transform = new AffineTransform();
        private Point2D _point;
        private boolean _dragged;

        Processor(double param1Double1, double param1Double2, boolean param1Boolean) {
            this._point = new Point2D.Double(param1Double1, param1Double2);
            this._dragged = param1Boolean;
        }

        public void process(Primitive param1Primitive) {
        }

        public void process(CompositeNode param1CompositeNode) {
            for (byte b = 0; b < param1CompositeNode.getVisibleSubgraphCount(); b++)
                param1CompositeNode.getVisibleSubgraph(b).accept(this);
        }

        public void process(Transform param1Transform) {
            Point2D point2D = this._point;
            try {
                this._point = param1Transform.getTransform().inverseTransform(this._point, null);
            } catch (NoninvertibleTransformException noninvertibleTransformException) {
                throw new MouseMotion.TransformFailure(noninvertibleTransformException);
            }
            param1Transform.getTransformedGraph().accept(this);
            this._point = point2D;
        }

        public void process(Style param1Style) {
            param1Style.getStyledGraph().accept(this);
        }

        public void process(Input param1Input) {
            if (param1Input instanceof MouseMotion) {
                MouseMotion mouseMotion = (MouseMotion) param1Input;
                if (mouseMotion.isActive() && ((mouseMotion.isDragged() && this._dragged) || !mouseMotion.isDragged())) {
                    Point2D point2D = this._transform.transform(this._point, null);
                    mouseMotion.updatePosition(point2D.getX(), point2D.getY());
                }
            }
            param1Input.getSensitiveGraph().accept(this);
        }
    }
}
