package uk.ac.ic.doc.scenebeans.behaviour;

import java.awt.geom.Point2D;
import java.io.Serializable;

public class Track extends PointActivityBase implements Serializable {
    private double[] _nodes;
    private String[] _events;
    private double _timeout = 0.0D;
    private int _current = 0;

    public Track() {
        this._nodes = new double[0];
        this._events = new String[0];
    }

    public Track(int paramInt) {
        this._nodes = new double[3 * paramInt - 1];
        this._events = new String[paramInt - 1];
    }

    public synchronized int getPointCount() {
        return (this._nodes.length + 1) / 3;
    }

    public synchronized void setPointCount(int paramInt) {
        double[] arrayOfDouble = new double[3 * paramInt - 1];
        System.arraycopy(this._nodes, 0, arrayOfDouble, 0, Math.min(this._nodes.length, arrayOfDouble.length));
        this._nodes = arrayOfDouble;
        String[] arrayOfString = new String[paramInt - 1];
        System.arraycopy(this._events, 0, arrayOfString, 0, Math.min(this._events.length, arrayOfString.length));
        this._events = arrayOfString;
    }

    public synchronized Point2D getPoint(int paramInt) {
        paramInt *= 3;
        return new Point2D.Double(this._nodes[paramInt], this._nodes[paramInt + 1]);
    }

    public synchronized void setPoint(int paramInt, Point2D paramPoint2D) {
        paramInt *= 3;
        this._nodes[paramInt] = paramPoint2D.getX();
        this._nodes[paramInt + 1] = paramPoint2D.getY();
    }

    public synchronized double getX(int paramInt) {
        return this._nodes[paramInt * 3];
    }

    public synchronized void setX(int paramInt, double paramDouble) {
        this._nodes[paramInt * 3] = paramDouble;
    }

    public synchronized double getY(int paramInt) {
        return this._nodes[paramInt * 3 + 1];
    }

    public synchronized void setY(int paramInt, double paramDouble) {
        this._nodes[paramInt * 3 + 1] = paramDouble;
    }

    public synchronized double getDuration(int paramInt) {
        return this._nodes[paramInt * 3 + 2];
    }

    public synchronized void setDuration(int paramInt, double paramDouble) {
        this._nodes[paramInt * 3 + 2] = paramDouble;
    }

    public synchronized String getEvent(int paramInt) {
        return this._events[paramInt];
    }

    public synchronized void setEvent(int paramInt, String paramString) {
        this._events[paramInt] = paramString;
    }

    public synchronized Point2D getValue() {
        if (hasFinished())
            return getPoint(getPointCount() - 1);
        double d1 = getX(this._current);
        double d2 = getY(this._current);
        double d3 = getX(this._current + 1);
        double d4 = getY(this._current + 1);
        return new Point2D.Double(d1 + ratio() * (d3 - d1), d2 + ratio() * (d4 - d2));
    }

    public boolean isFinite() {
        return true;
    }

    public synchronized void reset() {
        this._current = 0;
        this._timeout = 0.0D;
        postUpdate(getValue());
    }

    public synchronized void performActivity(double paramDouble) {
        this._timeout += paramDouble;
        while (!hasFinished() && this._timeout >= getDuration(this._current)) {
            this._timeout -= getDuration(this._current);
            if (this._events[this._current] != null)
                postActivityComplete(this._events[this._current]);
            this._current++;
        }
        if (hasFinished())
            postActivityComplete();
        postUpdate(getValue());
    }

    private double ratio() {
        double d = getDuration(this._current);
        return this._timeout / d;
    }

    private boolean hasFinished() {
        return (this._current >= getPointCount() - 1);
    }
}
