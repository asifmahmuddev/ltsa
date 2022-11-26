package uk.ac.ic.doc.scenebeans.activity;

import java.io.Serializable;
import uk.ac.ic.doc.scenebeans.event.AnimationListener;

public interface Activity extends Serializable {
    boolean isFinite();

    void setActivityRunner(ActivityRunner paramActivityRunner);

    ActivityRunner getActivityRunner();

    void addAnimationListener(AnimationListener paramAnimationListener);

    void removeAnimationListener(AnimationListener paramAnimationListener);

    void reset();

    void performActivity(double paramDouble);
}
