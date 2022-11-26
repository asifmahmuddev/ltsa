package uk.ac.ic.doc.scenebeans.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import uk.ac.ic.doc.scenebeans.event.AnimationEvent;
import uk.ac.ic.doc.scenebeans.event.AnimationListener;

public abstract class ActivityBase implements Activity {
    private ActivityRunner _runner = null;
    private List _animation_listeners = null;

    public ActivityRunner getActivityRunner() {
        return this._runner;
    }

    public void setActivityRunner(ActivityRunner paramActivityRunner) {
        if (this._runner != null && paramActivityRunner != null)
            throw new IllegalStateException("activity already has a runner");
        this._runner = paramActivityRunner;
    }

    public synchronized void addAnimationListener(AnimationListener paramAnimationListener) {
        if (this._animation_listeners == null)
            this._animation_listeners = new ArrayList();
        this._animation_listeners.add(paramAnimationListener);
    }

    public synchronized void removeAnimationListener(AnimationListener paramAnimationListener) {
        if (this._animation_listeners != null)
            this._animation_listeners.remove(paramAnimationListener);
    }

    protected synchronized void postActivityComplete(String paramString) {
        if (this._animation_listeners != null) {
            AnimationEvent animationEvent = new AnimationEvent(this, paramString);
            for (Iterator iterator = this._animation_listeners.iterator(); iterator.hasNext();)
                ((AnimationListener) iterator.next()).animationEvent(animationEvent);
        }
    }

    public abstract void performActivity(double paramDouble);

    public abstract void reset();

    public abstract boolean isFinite();
}
