package uk.ac.ic.doc.scenebeans.animation;

import uk.ac.ic.doc.scenebeans.activity.Activity;
import uk.ac.ic.doc.scenebeans.activity.ActivityRunner;

public class StartActivityCommand implements Command {
    private Activity _activity;
    private ActivityRunner _runner;

    public StartActivityCommand(Activity paramActivity, ActivityRunner paramActivityRunner) {
        this._activity = paramActivity;
        this._runner = paramActivityRunner;
    }

    public Activity getActivity() {
        return this._activity;
    }

    public void setActivity(Activity paramActivity) {
        this._activity = paramActivity;
    }

    public ActivityRunner getActivityRunner() {
        return this._runner;
    }

    public void setActivityRunner(ActivityRunner paramActivityRunner) {
        this._runner = paramActivityRunner;
    }

    public void invoke() throws CommandException {
        this._runner.addActivity(this._activity);
    }
}
