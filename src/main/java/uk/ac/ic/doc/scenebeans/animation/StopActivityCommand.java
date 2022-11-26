package uk.ac.ic.doc.scenebeans.animation;

import uk.ac.ic.doc.scenebeans.activity.Activity;
import uk.ac.ic.doc.scenebeans.activity.ActivityRunner;

public class StopActivityCommand implements Command {
    private Activity _activity;

    public StopActivityCommand(Activity paramActivity) {
        this._activity = paramActivity;
    }

    public Activity getActivity() {
        return this._activity;
    }

    public void setActivity(Activity paramActivity) {
        this._activity = paramActivity;
    }

    public void invoke() throws CommandException {
        ActivityRunner activityRunner = this._activity.getActivityRunner();
        if (activityRunner != null)
            activityRunner.removeActivity(this._activity);
    }
}
