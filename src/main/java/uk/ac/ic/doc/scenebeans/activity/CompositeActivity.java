package uk.ac.ic.doc.scenebeans.activity;

public abstract class CompositeActivity extends FiniteActivityBase implements ActivityRunner {
    public abstract void removeActivity(Activity paramActivity);

    public abstract void addActivity(Activity paramActivity);
}
