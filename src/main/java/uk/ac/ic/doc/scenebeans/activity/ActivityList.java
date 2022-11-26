package uk.ac.ic.doc.scenebeans.activity;

import java.util.Iterator;

public abstract class ActivityList {
    private ActivityList() {
    }

    public final ActivityList add(Activity paramActivity) {
        return new Node(paramActivity, this);
    }

    public Iterator iterator() {
        return new Iterator(this) {
            private ActivityList _current;
            private final ActivityList this$0;

            public boolean hasNext() {
                return (this._current != ActivityList.EMPTY);
            }

            public Object next() {
                Activity activity = ((ActivityList.Node) this._current)._element;
                this._current = ((ActivityList.Node) this._current)._next;
                return activity;
            }

            public void remove() {
                throw new UnsupportedOperationException("attempt to remove an elements from an ActivityList");
            }
        };
    }

    public static final ActivityList EMPTY = new EmptyActivityList();

    public abstract void performActivities(double paramDouble);

    public abstract ActivityList remove(Activity paramActivity);

    private static class EmptyActivityList extends ActivityList {
        private EmptyActivityList() {
        }

        public ActivityList remove(Activity param1Activity) {
            return this;
        }

        public void performActivities(double param1Double) {
        }
    }

    static class Node extends ActivityList {
        private Activity _element;
        private ActivityList _next;

        Node(Activity param1Activity, ActivityList param1ActivityList) {
            this._element = param1Activity;
            this._next = param1ActivityList;
        }

        Node(Activity param1Activity) {
            this._element = param1Activity;
            this._next = ActivityList.EMPTY;
        }

        public void performActivities(double param1Double) {
            this._element.performActivity(param1Double);
            this._next.performActivities(param1Double);
        }

        public ActivityList remove(Activity param1Activity) {
            if (this._element == param1Activity)
                return this._next;
            ActivityList activityList = this._next.remove(param1Activity);
            if (activityList == this._next)
                return this;
            return new Node(this._element, activityList);
        }
    }
}
