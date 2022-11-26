package uk.ac.ic.doc.scenebeans.activity;

import java.util.ArrayList;
import java.util.List;
import uk.ac.ic.doc.scenebeans.event.AnimationEvent;
import uk.ac.ic.doc.scenebeans.event.AnimationListener;

public class SequentialActivity extends CompositeActivity implements AnimationListener {
    private List _activities = new ArrayList();
    int _current = 0;

    public synchronized void reset() {
        this._current = 0;
        for (int i = this._activities.size() - 1; i >= 0; i--)
            ((Activity) this._activities.get(i)).reset();
    }

    public synchronized void addActivity(Activity paramActivity) {
        if (!paramActivity.isFinite())
            throw new IllegalArgumentException("infinite activity added to sequence");
        paramActivity.setActivityRunner(this);
        paramActivity.addAnimationListener(this);
        this._activities.add(paramActivity);
    }

    public synchronized void removeActivity(Activity paramActivity) {
        paramActivity.setActivityRunner(null);
        this._activities.remove(paramActivity);
    }

    public synchronized void performActivity(double paramDouble) {
        if (this._current < this._activities.size())
            ((Activity) this._activities.get(this._current)).performActivity(paramDouble);
    }

    public void animationEvent(AnimationEvent paramAnimationEvent) {
        this._current++;
        if (this._current == this._activities.size())
            postActivityComplete();
    }
}
