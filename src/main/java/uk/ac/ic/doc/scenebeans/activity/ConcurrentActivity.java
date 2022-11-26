package uk.ac.ic.doc.scenebeans.activity;

import java.util.Iterator;
import uk.ac.ic.doc.scenebeans.event.AnimationEvent;
import uk.ac.ic.doc.scenebeans.event.AnimationListener;

public class ConcurrentActivity extends CompositeActivity implements AnimationListener {
    private ActivityList _activities = ActivityList.EMPTY;
    int _finite_count = 0;
    int _complete = 0;

    public synchronized void reset() {
        this._complete = 0;
        for (Iterator iterator = this._activities.iterator(); iterator.hasNext();)
            ((Activity) iterator.next()).reset();
    }

    public synchronized void addActivity(Activity paramActivity) {
        paramActivity.setActivityRunner(this);
        if (paramActivity.isFinite()) {
            this._finite_count++;
            paramActivity.addAnimationListener(this);
        }
        this._activities = this._activities.add(paramActivity);
    }

    public synchronized void removeActivity(Activity paramActivity) {
        if (paramActivity.isFinite()) {
            this._finite_count--;
            paramActivity.removeAnimationListener(this);
        }
        this._activities = this._activities.remove(paramActivity);
        paramActivity.setActivityRunner(null);
    }

    public void performActivity(double paramDouble) {
        this._activities.performActivities(paramDouble);
    }

    public void animationEvent(AnimationEvent paramAnimationEvent) {
        this._complete++;
        if (this._complete == this._finite_count)
            postActivityComplete();
    }
}
