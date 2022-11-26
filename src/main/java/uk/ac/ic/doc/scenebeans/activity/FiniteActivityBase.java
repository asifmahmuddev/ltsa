package uk.ac.ic.doc.scenebeans.activity;

public abstract class FiniteActivityBase extends ActivityBase {
    private String _activity_name = null;

    public boolean isFinite() {
        return true;
    }

    public String getActivityName() {
        return this._activity_name;
    }

    public void setActivityName(String paramString) {
        this._activity_name = paramString;
    }

    protected synchronized void postActivityComplete() {
        postActivityComplete(this._activity_name);
    }
}
