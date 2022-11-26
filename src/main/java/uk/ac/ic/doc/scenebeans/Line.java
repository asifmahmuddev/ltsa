package uk.ac.ic.doc.scenebeans;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.Serializable;

public class Line extends SceneGraphBase implements Primitive {
    private double _x0;
    private double _y0;
    private double _x1;
    private double _y1;
    private Shape _last_drawn = null;

    public Line() {
        this._x0 = 0.0D;
        this._y0 = 0.0D;
        this._x1 = 1.0D;
        this._y1 = 1.0D;
    }

    public Line(Point2D paramPoint2D1, Point2D paramPoint2D2) {
        this._x0 = paramPoint2D1.getX();
        this._y0 = paramPoint2D1.getY();
        this._x1 = paramPoint2D2.getX();
        this._y1 = paramPoint2D2.getY();
    }

    public Line(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) {
        this._x0 = paramDouble1;
        this._y0 = paramDouble2;
        this._x1 = paramDouble3;
        this._y1 = paramDouble4;
    }

    public Shape getShape(Graphics2D paramGraphics2D) {
        return new Line2D.Double(this._x0, this._y0, this._x1, this._y1);
    }

    public Shape getLastDrawnShape() {
        return this._last_drawn;
    }

    public Point2D getStart() {
        return new Point2D.Double(this._x0, this._y0);
    }

    public void setStart(Point2D paramPoint2D) {
        this._x0 = paramPoint2D.getX();
        this._y0 = paramPoint2D.getY();
        setDirty(true);
    }

    public Point2D getEnd() {
        return new Point2D.Double(this._x1, this._y1);
    }

    public void setEnd(Point2D paramPoint2D) {
        this._x1 = paramPoint2D.getX();
        this._y1 = paramPoint2D.getY();
        setDirty(true);
    }

    public double getStartX() {
        return this._x0;
    }

    public void setStartX(double paramDouble) {
        this._x0 = paramDouble;
        setDirty(true);
    }

    public double getStartY() {
        return this._y0;
    }

    public void setStartY(double paramDouble) {
        this._y0 = paramDouble;
        setDirty(true);
    }

    public double getEndX() {
        return this._x1;
    }

    public void setEndX(double paramDouble) {
        this._x1 = paramDouble;
        setDirty(true);
    }

    public double getEndY() {
        return this._y1;
    }

    public void setEndY(double paramDouble) {
        this._y1 = paramDouble;
        setDirty(true);
    }

    public void accept(SceneGraphProcessor paramSceneGraphProcessor) {
        paramSceneGraphProcessor.process(this);
    }

    public void draw(Graphics2D paramGraphics2D) {
        Shape shape = getShape(paramGraphics2D);
        paramGraphics2D.draw(shape);
        this._last_drawn = shape;
        setDirty(false);
    }

    public class Start implements PointBehaviourListener, Serializable {
        private final Line this$0;

        public Start(Line this$0) {
            this.this$0 = this$0;
        }

        public void behaviourUpdated(Point2D param1Point2D) {
            this.this$0.setStart(param1Point2D);
        }
    }

    public final Start newStartAdapter() {
        return new Start(this);
    }

    public class End implements PointBehaviourListener, Serializable {
        private final Line this$0;

        public End(Line this$0) {
            this.this$0 = this$0;
        }

        public void behaviourUpdated(Point2D param1Point2D) {
            this.this$0.setEnd(param1Point2D);
        }
    }

    public final End newEndAdapter() {
        return new End(this);
    }

    public class StartX implements DoubleBehaviourListener, Serializable {
        private final Line this$0;

        public StartX(Line this$0) {
            this.this$0 = this$0;
        }

        public void behaviourUpdated(double param1Double) {
            this.this$0.setStartX(param1Double);
        }
    }

    public final StartX newStartXAdapter() {
        return new StartX(this);
    }

    public class StartY implements DoubleBehaviourListener, Serializable {
        private final Line this$0;

        public StartY(Line this$0) {
            this.this$0 = this$0;
        }

        public void behaviourUpdated(double param1Double) {
            this.this$0.setStartY(param1Double);
        }
    }

    public final StartY newStartYAdapter() {
        return new StartY(this);
    }

    public class EndX implements DoubleBehaviourListener, Serializable {
        private final Line this$0;

        public EndX(Line this$0) {
            this.this$0 = this$0;
        }

        public void behaviourUpdated(double param1Double) {
            this.this$0.setEndX(param1Double);
        }
    }

    public final EndX newEndXAdapter() {
        return new EndX(this);
    }

    public class EndY implements DoubleBehaviourListener, Serializable {
        private final Line this$0;

        public EndY(Line this$0) {
            this.this$0 = this$0;
        }

        public void behaviourUpdated(double param1Double) {
            this.this$0.setEndY(param1Double);
        }
    }

    public final EndY newEndYAdapter() {
        return new EndY(this);
    }
}
