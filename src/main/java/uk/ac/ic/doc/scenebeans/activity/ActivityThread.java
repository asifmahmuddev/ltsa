package uk.ac.ic.doc.scenebeans.activity;

public class ActivityThread implements ActivityRunner, Runnable {
    Object _perform_lock;
    ActivityList _activities = ActivityList.EMPTY;
    Object _list_lock = new Object();
    long _start;
    Thread _thread = null;
    boolean _stop = false;
    long _sleep = 100L;

    public ActivityThread() {
        this._perform_lock = this;
    }

    public ActivityThread(Object paramObject) {
        this._perform_lock = paramObject;
    }

    public void addActivity(Activity paramActivity) {
        synchronized (this._list_lock) {
            paramActivity.setActivityRunner(this);
            this._activities = this._activities.add(paramActivity);
        }
    }

    public void removeActivity(Activity paramActivity) {
        synchronized (this._list_lock) {
            paramActivity.setActivityRunner(null);
            this._activities = this._activities.remove(paramActivity);
        }
    }

    public long getSleepDelay() {
        synchronized (this._perform_lock) {
            return this._sleep;
        }
    }

    public void setSleepDelay(long paramLong) {
        synchronized (this._perform_lock) {
            this._sleep = paramLong;
        }
    }

    public void start() {
        synchronized (this._perform_lock) {
            this._start = System.currentTimeMillis();
            this._stop = false;
            this._thread = new Thread(this);
            this._thread.start();
        }
    }

    public void stop() throws InterruptedException {
        synchronized (this._perform_lock) {
            this._stop = true;
            this._thread.interrupt();
            this._thread.join();
            this._thread = null;
        }
    }

    public void run() {
        try {
            while (!this._stop) {
                long l;
                synchronized (this._perform_lock) {
                    long l1 = System.currentTimeMillis();
                    long l2 = l1 - this._start;
                    double d = l2 / 1000.0D;
                    this._activities.performActivities(d);
                    this._start = l1;
                    l = this._sleep;
                }
                if (l != 0L)
                    Thread.sleep(l);
            }
        } catch (InterruptedException interruptedException) {
        }
    }
}
