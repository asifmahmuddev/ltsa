package uk.ac.ic.doc.natutil;

import java.util.Enumeration;

public abstract class ImmutableList {
    public final ImmutableList add(Object paramObject) {
        return new Node(paramObject, this);
    }

    public Enumeration elements() {
        return new Enumeration(this) {
            private ImmutableList _current;

            public boolean hasMoreElements() {
                return !(this._current == ImmutableList.EMPTY);
            }

            public Object nextElement() {
                Object object = ((ImmutableList.Node) this._current)._element;
                this._current = ((ImmutableList.Node) this._current)._next;
                return object;
            }
        };
    }

    public static final ImmutableList EMPTY = new ImmutableList() {
        public ImmutableList removeIf(Predicate param1Predicate) {
            return this;
        }

        public ImmutableList remove(Object param1Object) {
            return this;
        }

        public void forAll(Procedure param1Procedure) {
        }

        public ImmutableList map(Function param1Function) {
            return this;
        }
    };

    public abstract void forAll(Procedure paramProcedure);

    public abstract ImmutableList map(Function paramFunction);

    public abstract ImmutableList remove(Object paramObject);

    public abstract ImmutableList removeIf(Predicate paramPredicate);

    static class Node extends ImmutableList {
        private Object _element;
        private ImmutableList _next;

        Node(Object param1Object, ImmutableList param1ImmutableList) {
            this._element = param1Object;
            this._next = param1ImmutableList;
        }

        Node(Object param1Object) {
            this._element = param1Object;
            this._next = ImmutableList.EMPTY;
        }

        public ImmutableList removeIf(Predicate param1Predicate) {
            ImmutableList immutableList = this._next.remove(param1Predicate);
            if (param1Predicate.evaluate(this._element))
                return immutableList;
            if (immutableList == this._next)
                return this;
            return new Node(this._element, immutableList);
        }

        public ImmutableList remove(Object param1Object) {
            if (this._element == param1Object)
                return this._next;
            ImmutableList immutableList = this._next.remove(param1Object);
            if (immutableList == this._next)
                return this;
            return new Node(this._element, immutableList);
        }

        public void forAll(Procedure param1Procedure) {
            param1Procedure.execute(this._element);
            this._next.forAll(param1Procedure);
        }

        public ImmutableList map(Function param1Function) {
            return new Node(param1Function.evaluate(this._element), this._next.map(param1Function));
        }
    }
}
