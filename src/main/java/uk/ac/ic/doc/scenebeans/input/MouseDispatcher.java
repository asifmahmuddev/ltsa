package uk.ac.ic.doc.scenebeans.input;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.NoninvertibleTransformException;
import java.util.List;
import uk.ac.ic.doc.scenebeans.MouseClick;
import uk.ac.ic.doc.scenebeans.MouseMotion;
import uk.ac.ic.doc.scenebeans.SceneGraph;
import uk.ac.ic.doc.scenebeans.pick.Picker;

public class MouseDispatcher implements MouseListener, MouseMotionListener {
    private SceneGraph _scene_graph;
    private Object _lock;

    public MouseDispatcher() {
        this._scene_graph = null;
        this._lock = null;
    }

    public MouseDispatcher(SceneGraph paramSceneGraph, Object paramObject) {
        this._scene_graph = paramSceneGraph;
        this._lock = paramObject;
    }

    public SceneGraph getSceneGraph() {
        return this._scene_graph;
    }

    public void setSceneGraph(SceneGraph paramSceneGraph) {
        this._scene_graph = paramSceneGraph;
    }

    public Object getLock() {
        return this._lock;
    }

    public void setLock(Object paramObject) {
        this._lock = paramObject;
    }

    public void mouseEntered(MouseEvent paramMouseEvent) {
        mouseMoved(paramMouseEvent);
    }

    public void mouseExited(MouseEvent paramMouseEvent) {
    }

    public void mousePressed(MouseEvent paramMouseEvent) {
        if (this._scene_graph == null)
            return;
        try {
            synchronized (this._lock) {
                Component component = (Component) paramMouseEvent.getSource();
                Graphics2D graphics2D = (Graphics2D) component.getGraphics();
                List list = Picker.pick(graphics2D, this._scene_graph, paramMouseEvent.getX(), paramMouseEvent.getY());
                MouseClick.mousePressed(list);
                mouseDragged(paramMouseEvent);
            }
        } catch (NoninvertibleTransformException noninvertibleTransformException) {
        }
    }

    public void mouseReleased(MouseEvent paramMouseEvent) {
        if (this._scene_graph == null)
            return;
        try {
            synchronized (this._lock) {
                Component component = (Component) paramMouseEvent.getSource();
                Graphics2D graphics2D = (Graphics2D) component.getGraphics();
                List list = Picker.pick(graphics2D, this._scene_graph, paramMouseEvent.getX(), paramMouseEvent.getY());
                MouseClick.mouseReleased(list);
            }
        } catch (NoninvertibleTransformException noninvertibleTransformException) {
        }
    }

    public void mouseClicked(MouseEvent paramMouseEvent) {
    }

    public void mouseMoved(MouseEvent paramMouseEvent) {
        if (this._scene_graph == null)
            return;
        try {
            synchronized (this._lock) {
                MouseMotion.mouseMoved(this._scene_graph, paramMouseEvent.getX(), paramMouseEvent.getY());
            }
        } catch (NoninvertibleTransformException noninvertibleTransformException) {
        }
    }

    public void mouseDragged(MouseEvent paramMouseEvent) {
        if (this._scene_graph == null)
            return;
        try {
            synchronized (this._lock) {
                MouseMotion.mouseDragged(this._scene_graph, paramMouseEvent.getX(), paramMouseEvent.getY());
            }
        } catch (NoninvertibleTransformException noninvertibleTransformException) {
        }
    }

    public void attachTo(Component paramComponent) {
        paramComponent.addMouseListener(this);
        paramComponent.addMouseMotionListener(this);
    }

    public void removeFrom(Component paramComponent) {
        paramComponent.removeMouseListener(this);
        paramComponent.removeMouseMotionListener(this);
    }
}
