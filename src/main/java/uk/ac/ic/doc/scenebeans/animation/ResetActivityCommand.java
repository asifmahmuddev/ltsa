package uk.ac.ic.doc.scenebeans.animation;

import uk.ac.ic.doc.scenebeans.activity.Activity;

public class ResetActivityCommand implements Command {
    private Activity _activity;

    public ResetActivityCommand(Activity paramActivity) {
        this._activity = paramActivity;
    }

    public Activity getActivity() {
        return this._activity;
    }

    public void setActivity(Activity paramActivity) {
        this._activity = paramActivity;
    }

    public void invoke() throws CommandException {
        this._activity.reset();
    }
}
