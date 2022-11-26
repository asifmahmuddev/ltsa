package uk.ac.ic.doc.scenebeans;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.io.Serializable;

public class Polygon extends PrimitiveBase {
    private float[] _coords;

    public Polygon() {
        this._coords = new float[0];
    }

    public Polygon(float[] paramArrayOffloat) {
        this._coords = (float[]) paramArrayOffloat.clone();
    }

    public Polygon(double[] paramArrayOfdouble) {
        this._coords = new float[paramArrayOfdouble.length];
        for (byte b = 0; b < paramArrayOfdouble.length; b++)
            this._coords[b] = (float) paramArrayOfdouble[b];
    }

    public Polygon(int paramInt) {
        this._coords = new float[2 * paramInt];
    }

    public Shape getShape(Graphics2D paramGraphics2D) {
        GeneralPath generalPath = new GeneralPath(1, this._coords.length / 2 + 2);
        generalPath.moveTo(this._coords[0], this._coords[1]);
        for (byte b = 2; b < this._coords.length; b += 2)
            generalPath.lineTo(this._coords[b], this._coords[b + 1]);
        generalPath.closePath();
        return generalPath;
    }

    public int getPointCount() {
        return this._coords.length / 2;
    }

    public void setPointCount(int paramInt) {
        float[] arrayOfFloat = new float[paramInt * 2];
        System.arraycopy(this._coords, 0, arrayOfFloat, 0, Math.min(paramInt * 2, this._coords.length));
        this._coords = arrayOfFloat;
        setDirty(true);
    }

    public Point2D[] getPoints() {
        Point2D[] arrayOfPoint2D = new Point2D[this._coords.length / 2];
        for (byte b = 0; b < arrayOfPoint2D.length; b++)
            arrayOfPoint2D[b] = new Point2D.Float(this._coords[2 * b], this._coords[2 * b + 1]);
        return arrayOfPoint2D;
    }

    public Point2D getPoints(int paramInt) {
        return new Point2D.Float(this._coords[2 * paramInt], this._coords[2 * paramInt + 1]);
    }

    public void setPoints(Point2D[] paramArrayOfPoint2D) {
        this._coords = new float[paramArrayOfPoint2D.length * 2];
        for (byte b = 0; b < paramArrayOfPoint2D.length; b++) {
            this._coords[b * 2] = (float) paramArrayOfPoint2D[b].getX();
            this._coords[b * 2 + 1] = (float) paramArrayOfPoint2D[b].getY();
        }
        setDirty(true);
    }

    public void setPoints(int paramInt, Point2D paramPoint2D) {
        this._coords[2 * paramInt] = (float) paramPoint2D.getX();
        this._coords[2 * paramInt + 1] = (float) paramPoint2D.getY();
        setDirty(true);
    }

    public double getXCoord(int paramInt) {
        return this._coords[2 * paramInt];
    }

    public void setXCoord(int paramInt, double paramDouble) {
        this._coords[2 * paramInt] = (float) paramDouble;
        setDirty(true);
    }

    public double getYCoord(int paramInt) {
        return this._coords[2 * paramInt + 1];
    }

    public void setYCoord(int paramInt, double paramDouble) {
        this._coords[2 * paramInt + 1] = (float) paramDouble;
        setDirty(true);
    }

    public class XCoord implements DoubleBehaviourListener, Serializable {
        int _index;
        private final Polygon this$0;

        public XCoord(Polygon this$0, int param1Int) {
            this.this$0 = this$0;
            this._index = param1Int;
        }

        public void behaviourUpdated(double param1Double) {
            this.this$0.setXCoord(this._index, param1Double);
        }
    }

    public final XCoord newXCoordAdapter(int paramInt) {
        return new XCoord(this, paramInt);
    }

    public class YCoord implements DoubleBehaviourListener, Serializable {
        int _index;
        private final Polygon this$0;

        public YCoord(Polygon this$0, int param1Int) {
            this.this$0 = this$0;
            this._index = param1Int;
        }

        public void behaviourUpdated(double param1Double) {
            this.this$0.setYCoord(this._index, param1Double);
        }
    }

    public final YCoord newYCoordAdapter(int paramInt) {
        return new YCoord(this, paramInt);
    }

    public class Points implements PointBehaviourListener, Serializable {
        int _index;
        private final Polygon this$0;

        public Points(Polygon this$0, int param1Int) {
            this.this$0 = this$0;
            this._index = param1Int;
        }

        public void behaviourUpdated(Point2D param1Point2D) {
            this.this$0.setPoints(this._index, param1Point2D);
        }
    }

    public final Points newPointsAdapter(int paramInt) {
        return new Points(this, paramInt);
    }
}
