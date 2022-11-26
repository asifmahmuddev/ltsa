package uk.ac.ic.doc.scenebeans;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import uk.ac.ic.doc.scenebeans.event.AnimationEvent;
import uk.ac.ic.doc.scenebeans.event.AnimationListener;

public class MouseClick extends InputBase {
    private List _activity_listeners = null;
    private String _pressed_event = "pressed";
    private String _released_event = "released";

    public MouseClick() {
    }

    public MouseClick(SceneGraph paramSceneGraph) {
        super(paramSceneGraph);
    }

    public String getPressedEvent() {
        return this._pressed_event;
    }

    public void setPressedEvent(String paramString) {
        this._pressed_event = paramString;
    }

    public String getReleasedEvent() {
        return this._released_event;
    }

    public void setReleasedEvent(String paramString) {
        this._released_event = paramString;
    }

    public void postMousePressed() {
        postAnimationEvent(this._pressed_event);
    }

    public void postMouseReleased() {
        postAnimationEvent(this._released_event);
    }

    public synchronized void addAnimationListener(AnimationListener paramAnimationListener) {
        if (this._activity_listeners == null)
            this._activity_listeners = new ArrayList();
        this._activity_listeners.add(paramAnimationListener);
    }

    public synchronized void removeAnimationListener(AnimationListener paramAnimationListener) {
        if (this._activity_listeners != null)
            this._activity_listeners.remove(paramAnimationListener);
    }

    protected synchronized void postAnimationEvent(String paramString) {
        if (this._activity_listeners != null) {
            AnimationEvent animationEvent = new AnimationEvent(this, paramString);
            for (Iterator iterator = this._activity_listeners.iterator(); iterator.hasNext();)
                ((AnimationListener) iterator.next()).animationEvent(animationEvent);
        }
    }

    public static void mousePressed(List paramList) {
        ListIterator listIterator = paramList.listIterator(paramList.size());
        while (listIterator.hasPrevious()) {
            MouseClick mouseClick = (MouseClick) listIterator.previous();
            if (mouseClick instanceof MouseClick) {
                ((MouseClick) mouseClick).postMousePressed();
                return;
            }
        }
    }

    public static void mouseReleased(List paramList) {
        ListIterator listIterator = paramList.listIterator(paramList.size());
        while (listIterator.hasPrevious()) {
            MouseClick mouseClick = (MouseClick) listIterator.previous();
            if (mouseClick instanceof MouseClick) {
                ((MouseClick) mouseClick).postMouseReleased();
                return;
            }
        }
    }
}
